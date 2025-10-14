package com.beyt.jdq.mongo;

import com.beyt.jdq.deserializer.IDeserializer;
import com.beyt.jdq.dto.DynamicQuery;
import com.beyt.jdq.dto.enums.CriteriaOperator;
import com.beyt.jdq.exception.DynamicQueryValueSerializeException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Template class for building and executing MongoDB queries from DynamicQuery objects.
 * Provides methods similar to JPA DynamicQueryManager but for MongoDB.
 */
public class MongoSearchQueryTemplate {

    private final MongoTemplate mongoTemplate;
    private final IDeserializer deserializer;

    public MongoSearchQueryTemplate(MongoTemplate mongoTemplate, IDeserializer deserializer) {
        this.mongoTemplate = mongoTemplate;
        this.deserializer = deserializer;
    }

    /**
     * Find all entities matching the criteria list
     */
    public <Entity> List<Entity> findAll(Class<Entity> entityClass, List<com.beyt.jdq.dto.Criteria> searchCriteriaList) {
        DynamicQuery dynamicQuery = DynamicQuery.of(searchCriteriaList);
        return findAll(entityClass, dynamicQuery);
    }

    /**
     * Find all entities matching the dynamic query
     */
    public <Entity> List<Entity> findAll(Class<Entity> entityClass, DynamicQuery dynamicQuery) {
        // Check if any criteria require joins (reference DBRef fields)
        if (requiresJoin(entityClass, dynamicQuery)) {
            return executeAggregation(entityClass, dynamicQuery);
        } else {
            Query query = prepareQuery(entityClass, dynamicQuery);
            return mongoTemplate.find(query, entityClass);
        }
    }

    /**
     * Find all entities as page matching the criteria list
     */
    public <Entity> Page<Entity> findAllAsPage(Class<Entity> entityClass, List<com.beyt.jdq.dto.Criteria> searchCriteriaList, Pageable pageable) {
        DynamicQuery dynamicQuery = DynamicQuery.of(searchCriteriaList);
        dynamicQuery.setPageNumber(pageable.getPageNumber());
        dynamicQuery.setPageSize(pageable.getPageSize());
        return findAllAsPage(entityClass, dynamicQuery);
    }

    /**
     * Find all entities as page matching the dynamic query
     */
    public <Entity> Page<Entity> findAllAsPage(Class<Entity> entityClass, DynamicQuery dynamicQuery) {
        Pageable pageable = getPageable(dynamicQuery);
        
        if (requiresJoin(entityClass, dynamicQuery)) {
            List<Entity> all = executeAggregation(entityClass, dynamicQuery, pageable);
            long count = countWithAggregation(entityClass, dynamicQuery);
            return PageableExecutionUtils.getPage(all, pageable, () -> count);
        } else {
            Criteria criteria = applyCriteria(entityClass, dynamicQuery);
            Query query = Query.query(criteria).with(pageable);
            List<Entity> all = mongoTemplate.find(query, entityClass);
            return PageableExecutionUtils.getPage(
                all, 
                pageable, 
                () -> mongoTemplate.count(Query.query(criteria), entityClass)
            );
        }
    }

    /**
     * Count entities matching the criteria list
     */
    public <Entity> long count(Class<Entity> entityClass, List<com.beyt.jdq.dto.Criteria> searchCriteriaList) {
        DynamicQuery dynamicQuery = DynamicQuery.of(searchCriteriaList);
        
        if (requiresJoin(entityClass, dynamicQuery)) {
            return countWithAggregation(entityClass, dynamicQuery);
        } else {
            Criteria criteria = applyCriteria(entityClass, dynamicQuery);
            return mongoTemplate.count(Query.query(criteria), entityClass);
        }
    }

    /**
     * Prepare a MongoDB Query from DynamicQuery
     */
    public <Entity> Query prepareQuery(Class<Entity> entityClass, DynamicQuery searchQuery) {
        Criteria criteria = applyCriteria(entityClass, searchQuery);
        Query query = Query.query(criteria);
        
        if (searchQuery.getPageSize() != null && searchQuery.getPageNumber() != null) {
            query.with(getPageable(searchQuery));
        }
        
        return query;
    }

    /**
     * Get pageable from DynamicQuery
     */
    private Pageable getPageable(DynamicQuery searchQuery) {
        int pageNumber = searchQuery.getPageNumber() != null ? searchQuery.getPageNumber() : 0;
        int pageSize = searchQuery.getPageSize() != null ? searchQuery.getPageSize() : 20;
        
        return searchQuery.getOrderBy().stream().findFirst()
                .map(o -> PageRequest.of(pageNumber, pageSize, o.getSecond().getDirection(), o.getFirst()))
                .orElse(PageRequest.of(pageNumber, pageSize));
    }

    /**
     * Apply criteria from DynamicQuery and build MongoDB Criteria
     * Logic: Collects criteria into groups, splits groups on OR operator, then combines:
     * - Each group's criteria are combined with AND
     * - All groups are combined with OR
     * Example: (c1 AND c2) OR (c3 AND c4)
     */
    private <Entity> Criteria applyCriteria(Class<Entity> entityClass, DynamicQuery searchQuery) {
        return applyCriteriaList(entityClass, searchQuery.getWhere(), searchQuery);
    }

    /**
     * Recursively process a criteria list and build MongoDB Criteria
     * This method handles PARENTHES operator by recursively processing nested criteria lists
     */
    @SuppressWarnings("unchecked")
    private <Entity> Criteria applyCriteriaList(Class<Entity> entityClass, List<com.beyt.jdq.dto.Criteria> criteriaList, DynamicQuery searchQuery) {
        List<com.beyt.jdq.dto.Criteria> issuedCriteriaList = new ArrayList<>();
        List<Criteria> searchCriteriaList = new ArrayList<>();
        List<Criteria> andCriteriaList = new ArrayList<>();
        List<Criteria> orGroupList = new ArrayList<>();

        Map<CriteriaOperator, CriteriaBuilderFunction> criteriaBuilderMap = buildCriteriaMap(entityClass, issuedCriteriaList, searchQuery);

        for (com.beyt.jdq.dto.Criteria c : criteriaList) {
            // Handle PARENTHES operator - recursively process nested criteria
            if (CriteriaOperator.PARENTHES.equals(c.getOperation())) {
                if (c.getValues() == null || c.getValues().isEmpty()) {
                    continue;
                }
                try {
                    List<com.beyt.jdq.dto.Criteria> nestedCriteria = (List<com.beyt.jdq.dto.Criteria>) c.getValues().get(0);
                    Criteria nestedMongoCriteria = applyCriteriaList(entityClass, nestedCriteria, searchQuery);
                    andCriteriaList.add(nestedMongoCriteria);
                } catch (Exception e) {
                    throw new RuntimeException("Error processing PARENTHES operator for key: " + c.getKey(), e);
                }
                continue;
            }

            // Handle OR operator - finalize current AND group and start new one
            if (CriteriaOperator.OR.equals(c.getOperation())) {
                if (!andCriteriaList.isEmpty()) {
                    // Combine current group with AND and add to OR list
                    orGroupList.add(new Criteria().andOperator(andCriteriaList));
                    andCriteriaList = new ArrayList<>();
                }
                continue;
            }

            if (issuedCriteriaList.contains(c) || !criteriaBuilderMap.containsKey(c.getOperation())) {
                continue;
            }

            Criteria builtCriteria = criteriaBuilderMap.get(c.getOperation()).apply(c, searchQuery);
            
            if (c.getKey().startsWith("search.")) {
                searchCriteriaList.add(builtCriteria);
            } else {
                andCriteriaList.add(builtCriteria);
            }
        }

        // Add the final AND group to OR list
        if (!andCriteriaList.isEmpty()) {
            orGroupList.add(new Criteria().andOperator(andCriteriaList));
        }

        // Build final criteria
        Criteria finalCriteria;
        if (orGroupList.isEmpty()) {
            finalCriteria = new Criteria();
        } else if (orGroupList.size() == 1) {
            finalCriteria = orGroupList.get(0);
        } else {
            finalCriteria = new Criteria().orOperator(orGroupList);
        }

        // Add search criteria as additional OR condition if present
        if (CollectionUtils.isNotEmpty(searchCriteriaList)) {
            Criteria searchCriteria = new Criteria().orOperator(searchCriteriaList);
            if (orGroupList.isEmpty()) {
                finalCriteria = searchCriteria;
            } else {
                finalCriteria = new Criteria().andOperator(finalCriteria, searchCriteria);
            }
        }

        return finalCriteria;
    }

    /**
     * Build the criteria builder map for all operators
     */
    private <Entity> Map<CriteriaOperator, CriteriaBuilderFunction> buildCriteriaMap(
            Class<Entity> entityClass,
            List<com.beyt.jdq.dto.Criteria> issuedCriteriaList, 
            DynamicQuery searchQuery) {
        
        Map<CriteriaOperator, CriteriaBuilderFunction> criteriaBuilderMap = new HashMap<>();

        // EQUAL - supports multiple values (IN query)
        criteriaBuilderMap.put(CriteriaOperator.EQUAL, (sc, sq) -> {
            String fieldKey = sc.getKey().replace("<", ".");  // Convert left join syntax
            String mongoField = toMongoFieldPath(entityClass, fieldKey);
            if (sc.getValues() == null || sc.getValues().isEmpty()) {
                return where(mongoField).is(null);
            }
            List<Object> deserializedValues = deserializeValues(entityClass, fieldKey, sc.getValues());
            if (deserializedValues.size() == 1) {
                return where(mongoField).is(deserializedValues.get(0));
            }
            return where(mongoField).in(deserializedValues);
        });

        // NOT_EQUAL
        criteriaBuilderMap.put(CriteriaOperator.NOT_EQUAL, (sc, sq) -> {
            String fieldKey = sc.getKey().replace("<", ".");  // Convert left join syntax
            String mongoField = toMongoFieldPath(entityClass, fieldKey);
            if (sc.getValues() == null || sc.getValues().isEmpty()) {
                return where(mongoField).ne(null);
            }
            List<Object> deserializedValues = deserializeValues(entityClass, fieldKey, sc.getValues());
            if (deserializedValues.size() == 1) {
                return where(mongoField).ne(deserializedValues.get(0));
            }
            return where(mongoField).nin(deserializedValues);
        });

        // CONTAIN - case insensitive regex
        criteriaBuilderMap.put(CriteriaOperator.CONTAIN, (sc, sq) -> {
            String fieldKey = sc.getKey().replace("<", ".");  // Convert left join syntax
            String mongoField = toMongoFieldPath(entityClass, fieldKey);
            return where(mongoField).regex(".*" + escapeRegex(String.valueOf(sc.getValues().get(0))) + ".*", "i");
        });

        // DOES_NOT_CONTAIN
        criteriaBuilderMap.put(CriteriaOperator.DOES_NOT_CONTAIN, (sc, sq) -> {
            String fieldKey = sc.getKey().replace("<", ".");  // Convert left join syntax
            String mongoField = toMongoFieldPath(entityClass, fieldKey);
            return where(mongoField).not().regex(".*" + escapeRegex(String.valueOf(sc.getValues().get(0))) + ".*", "i");
        });

        // START_WITH
        criteriaBuilderMap.put(CriteriaOperator.START_WITH, (sc, sq) -> {
            String fieldKey = sc.getKey().replace("<", ".");  // Convert left join syntax
            String mongoField = toMongoFieldPath(entityClass, fieldKey);
            return where(mongoField).regex("^" + escapeRegex(String.valueOf(sc.getValues().get(0))) + ".*", "i");
        });

        // END_WITH
        criteriaBuilderMap.put(CriteriaOperator.END_WITH, (sc, sq) -> {
            String fieldKey = sc.getKey().replace("<", ".");  // Convert left join syntax
            String mongoField = toMongoFieldPath(entityClass, fieldKey);
            return where(mongoField).regex(".*" + escapeRegex(String.valueOf(sc.getValues().get(0))) + "$", "i");
        });

        // SPECIFIED - check if field is not null (true) or is null (false)
        // Also handles left join syntax with '<' (e.g., "department<id" means check if department is null)
        criteriaBuilderMap.put(CriteriaOperator.SPECIFIED, (sc, sq) -> {
            String fieldKey = sc.getKey().replace("<", ".");  // Convert left join syntax
            String mongoField = toMongoFieldPath(entityClass, fieldKey);
            boolean isSpecified = Boolean.parseBoolean(String.valueOf(sc.getValues().get(0)));
            if (isSpecified) {
                return where(mongoField).ne(null);
            } else {
                return where(mongoField).is(null);
            }
        });

        // Comparison operators
        criteriaBuilderMap.put(CriteriaOperator.GREATER_THAN, (sc, sq) -> {
            String fieldKey = sc.getKey().replace("<", ".");  // Convert left join syntax
            String mongoField = toMongoFieldPath(entityClass, fieldKey);
            Object value = deserializeValue(entityClass, fieldKey, sc.getValues().get(0));
            return where(mongoField).gt(value);
        });

        criteriaBuilderMap.put(CriteriaOperator.GREATER_THAN_OR_EQUAL, (sc, sq) -> {
            String fieldKey = sc.getKey().replace("<", ".");  // Convert left join syntax
            String mongoField = toMongoFieldPath(entityClass, fieldKey);
            Object value = deserializeValue(entityClass, fieldKey, sc.getValues().get(0));
            return where(mongoField).gte(value);
        });

        criteriaBuilderMap.put(CriteriaOperator.LESS_THAN, (sc, sq) -> {
            String fieldKey = sc.getKey().replace("<", ".");  // Convert left join syntax
            String mongoField = toMongoFieldPath(entityClass, fieldKey);
            Object value = deserializeValue(entityClass, fieldKey, sc.getValues().get(0));
            return where(mongoField).lt(value);
        });

        criteriaBuilderMap.put(CriteriaOperator.LESS_THAN_OR_EQUAL, (sc, sq) -> {
            String fieldKey = sc.getKey().replace("<", ".");  // Convert left join syntax
            String mongoField = toMongoFieldPath(entityClass, fieldKey);
            Object value = deserializeValue(entityClass, fieldKey, sc.getValues().get(0));
            return where(mongoField).lte(value);
        });

        return criteriaBuilderMap;
    }

    /**
     * Escape special regex characters
     */
    private String escapeRegex(String str) {
        return str.replaceAll("([\\\\\\[\\]{}()*+?.^$|])", "\\\\$1");
    }

    /**
     * Convert Java field path to MongoDB field path for aggregation pipelines
     * Maps @Id annotated fields to _id
     * For DBRef fields in aggregation pipelines, uses the lookup field names
     * 
     * Logic:
     * - When we encounter a DBRef field, we use the lookup field name (path_to_field_lookup)
     * - For nested DBRef fields, the lookup creates a new root-level field in the aggregation
     * - After $addFields for non-collection DBRefs, fields are accessible via the parent lookup field
     * 
     * Example: roles.roleAuthorizations.authorization.menuIcon
     * - roles (DBRef collection) -> uses roles_lookup at root
     * - roleAuthorizations (DBRef collection in Role) -> uses roles_roleAuthorizations_lookup at root  
     * - authorization (DBRef single in RoleAuthorization) -> after $addFields, accessible as roles_roleAuthorizations_lookup.authorization
     * - Result: roles_roleAuthorizations_lookup.authorization.menuIcon
     */
    private String toMongoFieldPath(Class<?> entityClass, String fieldPath) {
        String[] parts = fieldPath.split("\\.");
        Class<?> currentClass = entityClass;
        StringBuilder logicalPath = new StringBuilder();
        String lastLookupField = null; // Track the last lookup field we encountered
        StringBuilder resultPath = new StringBuilder();
        boolean afterNonCollectionDBRef = false; // Track if we're after a non-collection DBRef
        
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            
            // Build the logical path so far
            if (logicalPath.length() > 0) {
                logicalPath.append(".");
            }
            logicalPath.append(part);
            
            try {
                Field field = currentClass.getDeclaredField(part);
                
                // Check if this is a DBRef field
                if (field.isAnnotationPresent(DBRef.class)) {
                    boolean isCollection = Collection.class.isAssignableFrom(field.getType());
                    
                    if (i < parts.length - 1) {
                        // This is a DBRef that leads to more nested fields
                        // The lookup field name
                        lastLookupField = logicalPath.toString().replace(".", "_") + "_lookup";
                        
                        if (isCollection) {
                            // For collection DBRefs, the lookup field is at the root level
                            resultPath = new StringBuilder(lastLookupField);
                            afterNonCollectionDBRef = false;
                        } else {
                            // For non-collection DBRefs, after $addFields, it's accessible via the parent lookup
                            if (resultPath.length() > 0) {
                                resultPath.append(".");
                            }
                            resultPath.append(part);
                            afterNonCollectionDBRef = true;
                        }
                    } else {
                        // This is the final DBRef field (shouldn't happen in practice for queries on nested fields)
                        if (resultPath.length() > 0) {
                            resultPath.append(".");
                        }
                        resultPath.append(part);
                    }
                    
                    currentClass = getTargetClassFromField(field);
                } else if (field.isAnnotationPresent(org.springframework.data.annotation.Id.class)) {
                    // Check if this field is annotated with @Id - if so, use _id in MongoDB
                    if (resultPath.length() > 0) {
                        resultPath.append(".");
                    }
                    resultPath.append("_id");
                } else {
                    // Regular field
                    if (resultPath.length() > 0) {
                        resultPath.append(".");
                    }
                    resultPath.append(part);
                    
                    // Move to next class for nested paths
                    if (i < parts.length - 1 && !Collection.class.isAssignableFrom(field.getType())) {
                        currentClass = field.getType();
                    }
                }
            } catch (NoSuchFieldException e) {
                // Field not found, keep original name
                if (resultPath.length() > 0) {
                    resultPath.append(".");
                }
                resultPath.append(part);
            }
        }
        
        return resultPath.toString();
    }

    /**
     * Get the field type for a given field key in an entity class
     * Supports nested fields using dot notation (e.g. "address.city")
     * Also handles DBRef fields by looking up the referenced entity
     */
    private Class<?> getFieldType(Class<?> entityClass, String fieldKey) {
        try {
            String[] fieldParts = fieldKey.split("\\.");
            Class<?> currentClass = entityClass;
            
            for (String fieldName : fieldParts) {
                Field field = currentClass.getDeclaredField(fieldName);
                
                // If this field is a DBRef, get the target class
                if (field.isAnnotationPresent(DBRef.class)) {
                    currentClass = getTargetClassFromField(field);
                } else {
                    currentClass = field.getType();
                }
            }
            
            return currentClass;
        } catch (NoSuchFieldException e) {
            // If field is not found, return String as fallback
            return String.class;
        }
    }

    /**
     * Deserialize a list of values to the target type using the configured deserializer
     */
    private List<Object> deserializeValues(Class<?> entityClass, String fieldKey, List<Object> values) {
        if (values == null || values.isEmpty()) {
            return values;
        }

        Class<?> fieldType = getFieldType(entityClass, fieldKey);
        List<Object> result = new ArrayList<>();
        
        for (Object value : values) {
            try {
                Object deserialized = deserializer.deserialize(value.toString(), fieldType);
                result.add(deserialized);
            } catch (Exception e) {
                throw new DynamicQueryValueSerializeException(
                    "There is a " + fieldType.getSimpleName() + 
                    " Deserialization Problem in Criteria Key: " + fieldKey + 
                    ", Value: " + value.toString());
            }
        }
        
        return result;
    }

    /**
     * Deserialize a single value to the target type using the configured deserializer
     */
    private Object deserializeValue(Class<?> entityClass, String fieldKey, Object value) {
        if (value == null) {
            return null;
        }

        Class<?> fieldType = getFieldType(entityClass, fieldKey);
        
        try {
            return deserializer.deserialize(value.toString(), fieldType);
        } catch (Exception e) {
            throw new DynamicQueryValueSerializeException(
                "There is a " + fieldType.getSimpleName() + 
                " Deserialization Problem in Criteria Key: " + fieldKey + 
                ", Value: " + value.toString());
        }
    }

    /**
     * Check if the query requires join operations (DBRef lookups)
     * Detects:
     * 1. Nested field paths (e.g., "department.name") where first part is a DBRef field
     * 2. Left join syntax with '<' (e.g., "department<id")
     */
    private <Entity> boolean requiresJoin(Class<Entity> entityClass, DynamicQuery searchQuery) {
        return searchQuery.getWhere().stream().anyMatch(c -> isJoinCriteria(entityClass, c.getKey()));
    }

    /**
     * Check if a field key represents a join criteria
     * Handles both '.' and '<' syntax for nested paths
     */
    private boolean isJoinCriteria(Class<?> entityClass, String fieldKey) {
        // Convert left join syntax to dot notation for analysis
        String pathToAnalyze = fieldKey.replace("<", ".");
        
        // Handle nested path: "department.name"
        if (pathToAnalyze.contains(".")) {
            String firstPart = pathToAnalyze.substring(0, pathToAnalyze.indexOf("."));
            return isDBRefField(entityClass, firstPart);
        }
        
        return false;
    }

    /**
     * Check if a field is annotated with @DBRef
     */
    private boolean isDBRefField(Class<?> entityClass, String fieldName) {
        try {
            Field field = entityClass.getDeclaredField(fieldName);
            return field.isAnnotationPresent(DBRef.class);
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    /**
     * Execute aggregation pipeline for queries with joins
     */
    private <Entity> List<Entity> executeAggregation(Class<Entity> entityClass, DynamicQuery dynamicQuery) {
        return executeAggregation(entityClass, dynamicQuery, null);
    }

    /**
     * Execute aggregation pipeline for queries with joins with pagination
     */
    private <Entity> List<Entity> executeAggregation(Class<Entity> entityClass, DynamicQuery dynamicQuery, Pageable pageable) {
        Aggregation aggregation = buildAggregation(entityClass, dynamicQuery, pageable);
        AggregationResults<Entity> results = mongoTemplate.aggregate(aggregation, getCollectionName(entityClass), entityClass);
        return results.getMappedResults();
    }

    /**
     * Count with aggregation for queries with joins
     */
    @SuppressWarnings("unchecked")
    private <Entity> long countWithAggregation(Class<Entity> entityClass, DynamicQuery dynamicQuery) {
        Aggregation aggregation = buildAggregation(entityClass, dynamicQuery, null, true);
        AggregationResults<Map<String, Object>> results = mongoTemplate.aggregate(aggregation, getCollectionName(entityClass), (Class<Map<String, Object>>)(Class<?>)Map.class);
        List<Map<String, Object>> mappedResults = results.getMappedResults();
        if (mappedResults.isEmpty()) {
            return 0;
        }
        Object count = mappedResults.get(0).get("count");
        return count != null ? ((Number) count).longValue() : 0;
    }

    /**
     * Build aggregation pipeline with $lookup stages for joins
     */
    private <Entity> Aggregation buildAggregation(Class<Entity> entityClass, DynamicQuery dynamicQuery, Pageable pageable) {
        return buildAggregation(entityClass, dynamicQuery, pageable, false);
    }

    /**
     * Build aggregation pipeline with $lookup stages for joins
     */
    private <Entity> Aggregation buildAggregation(Class<Entity> entityClass, DynamicQuery dynamicQuery, Pageable pageable, boolean isCount) {
        List<org.springframework.data.mongodb.core.aggregation.AggregationOperation> operations = new ArrayList<>();
        
        // Collect all nested paths that need lookups (including deep nested paths)
        Set<String> allNestedPaths = collectAllNestedPaths(entityClass, dynamicQuery);
        
        // Build lookup stages for all paths, starting from the root
        Set<String> processedPaths = new HashSet<>();
        buildLookupsForPaths(entityClass, allNestedPaths, operations, processedPaths);
        
        // Build match criteria from DynamicQuery
        Criteria criteria = applyCriteria(entityClass, dynamicQuery);
        operations.add(match(criteria));
        
        if (isCount) {
            // For count, just return the count
            operations.add(Aggregation.count().as("count"));
        } else {
            // Add pagination if provided
            if (pageable != null) {
                if (pageable.getSort().isSorted()) {
                    operations.add(sort(pageable.getSort()));
                }
                operations.add(skip((long) pageable.getPageNumber() * pageable.getPageSize()));
                operations.add(limit(pageable.getPageSize()));
            } else if (dynamicQuery.getPageSize() != null && dynamicQuery.getPageNumber() != null) {
                Pageable pageableFromQuery = getPageable(dynamicQuery);
                if (pageableFromQuery.getSort().isSorted()) {
                    operations.add(sort(pageableFromQuery.getSort()));
                }
                operations.add(skip((long) pageableFromQuery.getPageNumber() * pageableFromQuery.getPageSize()));
                operations.add(limit(pageableFromQuery.getPageSize()));
            }
        }
        
        return Aggregation.newAggregation(operations);
    }

    /**
     * Build all $lookup stages needed for nested paths in the correct order
     */
    private void buildLookupsForPaths(Class<?> currentClass, Set<String> allNestedPaths, 
                                     List<org.springframework.data.mongodb.core.aggregation.AggregationOperation> operations,
                                     Set<String> processedPaths) {
        buildLookupsRecursive(currentClass, "", "", allNestedPaths, operations, processedPaths);
    }

    /**
     * Recursively build $lookup stages for nested paths
     * 
     * @param currentClass The current entity class being processed
     * @param currentPath The logical path (e.g., "roles.roleAuthorizations")
     * @param currentLookupPath The actual MongoDB path after lookups (e.g., "roles_lookup.roles_roleAuthorizations_lookup")
     * @param allNestedPaths All nested paths that need lookup
     * @param operations List of aggregation operations to add to
     * @param processedPaths Set of already processed paths
     */
    private void buildLookupsRecursive(Class<?> currentClass, String currentPath, String currentLookupPath, 
                                       Set<String> allNestedPaths,
                                       List<org.springframework.data.mongodb.core.aggregation.AggregationOperation> operations,
                                       Set<String> processedPaths) {
        // Find all direct children paths that need lookup
        for (String nestedPath : allNestedPaths) {
            String pathToCheck = nestedPath;
            
            // Handle left join syntax
            if (pathToCheck.contains("<")) {
                pathToCheck = pathToCheck.substring(0, pathToCheck.indexOf("<"));
            }
            
            // Skip if already processed
            if (processedPaths.contains(pathToCheck)) {
                continue;
            }
            
            // Get the first unprocessed segment
            String relativePath = currentPath.isEmpty() ? pathToCheck : 
                (pathToCheck.startsWith(currentPath + ".") ? pathToCheck.substring(currentPath.length() + 1) : null);
            
            if (relativePath == null) {
                continue;
            }
            
            // Check if this is a direct child (no more dots)
            String firstSegment = relativePath.contains(".") ? relativePath.substring(0, relativePath.indexOf(".")) : relativePath;
            String fullPathSoFar = currentPath.isEmpty() ? firstSegment : currentPath + "." + firstSegment;
            
            if (processedPaths.contains(fullPathSoFar)) {
                continue;
            }
            
            // Check if this field is a DBRef
            try {
                Field field = currentClass.getDeclaredField(firstSegment);
                if (field.isAnnotationPresent(DBRef.class)) {
                    // Add lookup operations
                    String targetCollection = getCollectionNameFromField(field);
                    String lookupAs = fullPathSoFar.replace(".", "_") + "_lookup";
                    
                    // Determine the local field to lookup from
                    // After previous lookups, we need to reference the looked-up field
                    String localField;
                    if (currentLookupPath.isEmpty()) {
                        localField = firstSegment;
                    } else {
                        localField = currentLookupPath + "." + firstSegment;
                    }
                    
                    // Check if it's a collection field
                    boolean isCollection = Collection.class.isAssignableFrom(field.getType());
                    
                    // Perform $lookup
                    // Both single references and collections use DBRef format with $id field
                    // Single: {$ref: "...", $id: ObjectId(...)}
                    // Array: [{$ref: "...", $id: ObjectId(...)}, ...]
                    operations.add(lookup(targetCollection, localField + ".$id", "_id", lookupAs));
                    
                    // Unwind the lookup result
                    operations.add(unwind(lookupAs, true));
                    
                    // For non-collection fields, replace the original DBRef with the looked up document
                    // For collections, we keep using the lookup field name in queries
                    String nextLookupPath;
                    if (!isCollection) {
                        operations.add(Aggregation.addFields()
                            .addField(localField).withValue("$" + lookupAs)
                            .build());
                        nextLookupPath = localField;
                    } else {
                        nextLookupPath = lookupAs;
                    }
                    
                    processedPaths.add(fullPathSoFar);
                    
                    // Get the target class and recurse for deeper paths
                    Class<?> targetClass = getTargetClassFromField(field);
                    buildLookupsRecursive(targetClass, fullPathSoFar, nextLookupPath, allNestedPaths, operations, processedPaths);
                }
            } catch (NoSuchFieldException e) {
                // Skip if field not found
            }
        }
    }

    /**
     * Get the target class from a field (handling collections)
     */
    private Class<?> getTargetClassFromField(Field field) {
        if (Collection.class.isAssignableFrom(field.getType())) {
            ParameterizedType genericType = (ParameterizedType) field.getGenericType();
            return (Class<?>) genericType.getActualTypeArguments()[0];
        } else {
            return field.getType();
        }
    }

    /**
     * Collect all nested paths from criteria (including deep nested paths)
     * Handles both '.' and '<' syntax for nested paths
     */
    private <Entity> Set<String> collectAllNestedPaths(Class<Entity> entityClass, DynamicQuery dynamicQuery) {
        Set<String> allPaths = new HashSet<>();
        
        for (com.beyt.jdq.dto.Criteria c : dynamicQuery.getWhere()) {
            String fieldKey = c.getKey();
            
            // Convert left join syntax to dot notation for analysis
            // "roles<roleAuthorizations<authorization<menuIcon" -> "roles.roleAuthorizations.authorization.menuIcon"
            String pathToAnalyze = fieldKey.replace("<", ".");
            
            // For nested paths like "department.name" or "roles.roleAuthorizations.authorization.menuIcon"
            if (pathToAnalyze.contains(".")) {
                // Add all parent paths that need lookup
                String[] segments = pathToAnalyze.split("\\.");
                for (int i = 0; i < segments.length - 1; i++) {
                    StringBuilder pathBuilder = new StringBuilder();
                    for (int j = 0; j <= i; j++) {
                        if (j > 0) pathBuilder.append(".");
                        pathBuilder.append(segments[j]);
                    }
                    allPaths.add(pathBuilder.toString());
                }
            }
        }
        
        return allPaths;
    }

    /**
     * Get the MongoDB collection name from a DBRef field
     */
    private String getCollectionNameFromField(Field field) {
        Class<?> refType;
        
        // Check if it's a List/Collection type
        if (Collection.class.isAssignableFrom(field.getType())) {
            ParameterizedType genericType = (ParameterizedType) field.getGenericType();
            refType = (Class<?>) genericType.getActualTypeArguments()[0];
        } else {
            refType = field.getType();
        }
        
        return getCollectionName(refType);
    }

    /**
     * Get the MongoDB collection name for an entity class
     */
    private String getCollectionName(Class<?> entityClass) {
        org.springframework.data.mongodb.core.mapping.Document docAnnotation = 
            entityClass.getAnnotation(org.springframework.data.mongodb.core.mapping.Document.class);
        
        if (docAnnotation != null && !docAnnotation.collection().isEmpty()) {
            return docAnnotation.collection();
        }
        
        // Default: convert class name to lowercase plural
        String className = entityClass.getSimpleName();
        return className.toLowerCase() + "s";
    }
}
