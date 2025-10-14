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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;

import java.lang.reflect.Field;
import java.util.*;

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
        Query query = prepareQuery(entityClass, dynamicQuery);
        return mongoTemplate.find(query, entityClass);
    }

    /**
     * Find all entities matching the dynamic query
     */
    public <Entity> List<Entity> findAll(Class<Entity> entityClass, DynamicQuery dynamicQuery) {
        Query query = prepareQuery(entityClass, dynamicQuery);
        return mongoTemplate.find(query, entityClass);
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
        Criteria criteria = applyCriteria(entityClass, dynamicQuery);
        Query query = Query.query(criteria).with(pageable);
        
        List<Entity> all = mongoTemplate.find(query, entityClass);
        
        return PageableExecutionUtils.getPage(
            all, 
            pageable, 
            () -> mongoTemplate.count(Query.query(criteria), entityClass)
        );
    }

    /**
     * Count entities matching the criteria list
     */
    public <Entity> long count(Class<Entity> entityClass, List<com.beyt.jdq.dto.Criteria> searchCriteriaList) {
        DynamicQuery dynamicQuery = DynamicQuery.of(searchCriteriaList);
        Criteria criteria = applyCriteria(entityClass, dynamicQuery);
        return mongoTemplate.count(Query.query(criteria), entityClass);
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
            if (sc.getValues() == null || sc.getValues().isEmpty()) {
                return where(sc.getKey()).is(null);
            }
            List<Object> deserializedValues = deserializeValues(entityClass, sc.getKey(), sc.getValues());
            if (deserializedValues.size() == 1) {
                return where(sc.getKey()).is(deserializedValues.get(0));
            }
            return where(sc.getKey()).in(deserializedValues);
        });

        // NOT_EQUAL
        criteriaBuilderMap.put(CriteriaOperator.NOT_EQUAL, (sc, sq) -> {
            if (sc.getValues() == null || sc.getValues().isEmpty()) {
                return where(sc.getKey()).ne(null);
            }
            List<Object> deserializedValues = deserializeValues(entityClass, sc.getKey(), sc.getValues());
            if (deserializedValues.size() == 1) {
                return where(sc.getKey()).ne(deserializedValues.get(0));
            }
            return where(sc.getKey()).nin(deserializedValues);
        });

        // CONTAIN - case insensitive regex
        criteriaBuilderMap.put(CriteriaOperator.CONTAIN, (sc, sq) -> 
            where(sc.getKey()).regex(".*" + escapeRegex(String.valueOf(sc.getValues().get(0))) + ".*", "i"));

        // DOES_NOT_CONTAIN
        criteriaBuilderMap.put(CriteriaOperator.DOES_NOT_CONTAIN, (sc, sq) -> 
            where(sc.getKey()).not().regex(".*" + escapeRegex(String.valueOf(sc.getValues().get(0))) + ".*", "i"));

        // START_WITH
        criteriaBuilderMap.put(CriteriaOperator.START_WITH, (sc, sq) -> 
            where(sc.getKey()).regex("^" + escapeRegex(String.valueOf(sc.getValues().get(0))) + ".*", "i"));

        // END_WITH
        criteriaBuilderMap.put(CriteriaOperator.END_WITH, (sc, sq) -> 
            where(sc.getKey()).regex(".*" + escapeRegex(String.valueOf(sc.getValues().get(0))) + "$", "i"));

        // SPECIFIED - check if field is not null (true) or is null (false)
        criteriaBuilderMap.put(CriteriaOperator.SPECIFIED, (sc, sq) -> {
            boolean isSpecified = Boolean.parseBoolean(String.valueOf(sc.getValues().get(0)));
            if (isSpecified) {
                return where(sc.getKey()).ne(null);
            } else {
                return where(sc.getKey()).is(null);
            }
        });

        // Comparison operators
        criteriaBuilderMap.put(CriteriaOperator.GREATER_THAN, (sc, sq) -> {
            Object value = deserializeValue(entityClass, sc.getKey(), sc.getValues().get(0));
            return where(sc.getKey()).gt(value);
        });

        criteriaBuilderMap.put(CriteriaOperator.GREATER_THAN_OR_EQUAL, (sc, sq) -> {
            Object value = deserializeValue(entityClass, sc.getKey(), sc.getValues().get(0));
            return where(sc.getKey()).gte(value);
        });

        criteriaBuilderMap.put(CriteriaOperator.LESS_THAN, (sc, sq) -> {
            Object value = deserializeValue(entityClass, sc.getKey(), sc.getValues().get(0));
            return where(sc.getKey()).lt(value);
        });

        criteriaBuilderMap.put(CriteriaOperator.LESS_THAN_OR_EQUAL, (sc, sq) -> {
            Object value = deserializeValue(entityClass, sc.getKey(), sc.getValues().get(0));
            return where(sc.getKey()).lte(value);
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
     * Get the field type for a given field key in an entity class
     * Supports nested fields using dot notation (e.g. "address.city")
     */
    private Class<?> getFieldType(Class<?> entityClass, String fieldKey) {
        try {
            String[] fieldParts = fieldKey.split("\\.");
            Class<?> currentClass = entityClass;
            
            for (String fieldName : fieldParts) {
                Field field = currentClass.getDeclaredField(fieldName);
                currentClass = field.getType();
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
}
