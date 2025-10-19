package com.beyt.jdq.elasticsearch.core;

import com.beyt.jdq.core.deserializer.IDeserializer;
import com.beyt.jdq.core.model.DynamicQuery;
import com.beyt.jdq.core.model.enums.CriteriaOperator;
import com.beyt.jdq.core.model.exception.DynamicQueryIllegalArgumentException;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.util.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.lucene.search.join.ScoreMode;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * Template class for building and executing Elasticsearch queries from DynamicQuery objects.
 * Provides methods similar to DynamicQueryManager but for Elasticsearch.
 */
public class ElasticsearchSearchQueryTemplate {

    private final ElasticsearchOperations elasticsearchOperations;
    private final IDeserializer deserializer;

    public ElasticsearchSearchQueryTemplate(ElasticsearchOperations elasticsearchOperations, IDeserializer deserializer) {
        this.elasticsearchOperations = elasticsearchOperations;
        this.deserializer = deserializer;
    }

    /**
     * Find all entities matching the criteria list
     */
    public <Entity> List<Entity> findAll(Class<Entity> entityClass, List<com.beyt.jdq.core.model.Criteria> searchCriteriaList) {
        DynamicQuery dynamicQuery = DynamicQuery.of(searchCriteriaList);
        return findAll(entityClass, dynamicQuery);
    }

    /**
     * Find all entities matching the dynamic query
     */
    public <Entity> List<Entity> findAll(Class<Entity> entityClass, DynamicQuery dynamicQuery) {
        NativeSearchQuery query = prepareQuery(entityClass, dynamicQuery);
        SearchHits<Entity> searchHits = elasticsearchOperations.search(query, entityClass);
        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    /**
     * Find all entities with projection matching the dynamic query
     */
    public <Entity, ResultType> List<ResultType> findAll(Class<Entity> entityClass, DynamicQuery dynamicQuery, Class<ResultType> resultClass) {
        // For now, return as the same type - projection will be implemented later
        List<Entity> results = findAll(entityClass, dynamicQuery);
        return results.stream()
                .map(entity -> (ResultType) entity)
                .collect(Collectors.toList());
    }

    /**
     * Find all entities as page matching the criteria list
     */
    public <Entity> Page<Entity> findAllAsPage(Class<Entity> entityClass, List<com.beyt.jdq.core.model.Criteria> searchCriteriaList, Pageable pageable) {
        DynamicQuery dynamicQuery = DynamicQuery.of(searchCriteriaList);
        dynamicQuery.setPageNumber(pageable.getPageNumber());
        dynamicQuery.setPageSize(pageable.getPageSize());
        return findAllAsPage(entityClass, dynamicQuery);
    }

    /**
     * Find all entities as page matching the dynamic query
     */
    public <Entity> Page<Entity> findAllAsPage(Class<Entity> entityClass, DynamicQuery dynamicQuery) {
        NativeSearchQuery query = prepareQuery(entityClass, dynamicQuery);
        SearchHits<Entity> searchHits = elasticsearchOperations.search(query, entityClass);
        
        return new org.springframework.data.domain.PageImpl<>(
                searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList()),
                PageRequest.of(
                        dynamicQuery.getPageNumber() != null ? dynamicQuery.getPageNumber() : 0,
                        dynamicQuery.getPageSize() != null ? dynamicQuery.getPageSize() : 20
                ),
                searchHits.getTotalHits()
        );
    }

    /**
     * Find all entities as page with projection matching the dynamic query
     */
    public <Entity, ResultType> Page<ResultType> findAllAsPage(Class<Entity> entityClass, DynamicQuery dynamicQuery, Class<ResultType> resultClass) {
        Page<Entity> page = findAllAsPage(entityClass, dynamicQuery);
        return page.map(entity -> (ResultType) entity);
    }

    /**
     * Count entities matching the criteria list
     */
    public <Entity> long count(Class<Entity> entityClass, List<com.beyt.jdq.core.model.Criteria> searchCriteriaList) {
        DynamicQuery dynamicQuery = DynamicQuery.of(searchCriteriaList);
        NativeSearchQuery query = prepareQuery(entityClass, dynamicQuery);
        SearchHits<Entity> searchHits = elasticsearchOperations.search(query, entityClass);
        return searchHits.getTotalHits();
    }

    /**
     * Prepare NativeSearchQuery from DynamicQuery
     */
    private <Entity> NativeSearchQuery prepareQuery(Class<Entity> entityClass, DynamicQuery dynamicQuery) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        
        // Build the main query from criteria
        QueryBuilder mainQuery = buildQueryFromCriteria(dynamicQuery.getWhere());
        queryBuilder.withQuery(mainQuery);
        
        // Add sorting
        if (dynamicQuery.getOrderBy() != null && !dynamicQuery.getOrderBy().isEmpty()) {
            for (Pair<String, com.beyt.jdq.core.model.enums.Order> orderPair : dynamicQuery.getOrderBy()) {
                Sort.Direction direction = orderPair.getSecond() == com.beyt.jdq.core.model.enums.Order.ASC 
                    ? Sort.Direction.ASC : Sort.Direction.DESC;
                queryBuilder.withSort(Sort.by(direction, orderPair.getFirst()));
            }
        }
        
        // Add pagination
        int pageNumber = dynamicQuery.getPageNumber() != null ? dynamicQuery.getPageNumber() : 0;
        int pageSize = dynamicQuery.getPageSize() != null ? dynamicQuery.getPageSize() : 20;
        queryBuilder.withPageable(PageRequest.of(pageNumber, pageSize));
        
        return queryBuilder.build();
    }

    /**
     * Build Elasticsearch QueryBuilder from criteria list
     */
    private QueryBuilder buildQueryFromCriteria(List<com.beyt.jdq.core.model.Criteria> criteriaList) {
        if (criteriaList == null || criteriaList.isEmpty()) {
            return QueryBuilders.matchAllQuery();
        }

        // Split criteria list by OR operators
        List<List<com.beyt.jdq.core.model.Criteria>> orGroups = new java.util.ArrayList<>();
        List<com.beyt.jdq.core.model.Criteria> currentGroup = new java.util.ArrayList<>();
        
        for (com.beyt.jdq.core.model.Criteria criteria : criteriaList) {
            if (criteria.getOperation() == CriteriaOperator.OR) {
                if (!currentGroup.isEmpty()) {
                    orGroups.add(currentGroup);
                    currentGroup = new java.util.ArrayList<>();
                }
            } else {
                currentGroup.add(criteria);
            }
        }
        
        // Add the last group
        if (!currentGroup.isEmpty()) {
            orGroups.add(currentGroup);
        }
        
        // If only one group, build it as AND query
        if (orGroups.size() == 1) {
            return buildAndQuery(orGroups.get(0));
        }
        
        // Multiple groups - combine with OR
        BoolQueryBuilder orQuery = QueryBuilders.boolQuery();
        for (List<com.beyt.jdq.core.model.Criteria> group : orGroups) {
            QueryBuilder groupQuery = buildAndQuery(group);
            orQuery.should(groupQuery);
        }
        orQuery.minimumShouldMatch(1);
        
        return orQuery;
    }
    
    /**
     * Build AND query from criteria list (no OR operators)
     */
    private QueryBuilder buildAndQuery(List<com.beyt.jdq.core.model.Criteria> criteriaList) {
        if (criteriaList.isEmpty()) {
            return QueryBuilders.matchAllQuery();
        }
        
        if (criteriaList.size() == 1) {
            return buildCriteriaQuery(criteriaList.get(0));
        }
        
        BoolQueryBuilder andQuery = QueryBuilders.boolQuery();
        for (com.beyt.jdq.core.model.Criteria criteria : criteriaList) {
            QueryBuilder criteriaQuery = buildCriteriaQuery(criteria);
            if (criteriaQuery != null) {
                andQuery.must(criteriaQuery);
            }
        }
        
        return andQuery;
    }

    /**
     * Build individual criteria query
     * Handles both regular fields and nested fields
     */
    @SuppressWarnings("unchecked")
    private QueryBuilder buildCriteriaQuery(com.beyt.jdq.core.model.Criteria criteria) {
        String fieldName = criteria.getKey();
        CriteriaOperator operator = criteria.getOperation();
        List<Object> values = criteria.getValues();
        
        // Handle PARENTHES operator (nested criteria)
        if (operator == CriteriaOperator.PARENTHES) {
            if (values != null && !values.isEmpty() && values.get(0) instanceof List) {
                List<com.beyt.jdq.core.model.Criteria> nestedCriteria = (List<com.beyt.jdq.core.model.Criteria>) values.get(0);
                return buildQueryFromCriteria(nestedCriteria);
            }
            return null;
        }
        
        if (StringUtils.isBlank(fieldName) || operator == null) {
            return null;
        }
        
        if (values == null || values.isEmpty()) {
            return null;
        }
        
        // Check if this is a nested field query
        // Handle left join syntax: "department<id" means check if department exists
        // For SPECIFIED operator, check the parent nested object, not the child field
        boolean hasLeftJoinSyntax = fieldName.contains("<");
        String normalizedFieldName = fieldName.replace("<", ".");
        
        // Special case: "department<id" with SPECIFIED=false should check if "department" doesn't exist
        // In Elasticsearch nested documents, if the nested object is null, none of its fields exist
        if (hasLeftJoinSyntax && operator == CriteriaOperator.SPECIFIED) {
            // Extract the parent path (before the last dot)
            int lastDotIndex = normalizedFieldName.lastIndexOf(".");
            if (lastDotIndex > 0) {
                String parentPath = normalizedFieldName.substring(0, lastDotIndex);
                // For nested documents, check if any field in the nested object exists
                // If the nested object is null, the field won't exist in Elasticsearch
                boolean shouldExist = Boolean.parseBoolean(values.get(0).toString());
                if (shouldExist) {
                    // Check if the nested field exists (the actual field, not the parent)
                    return QueryBuilders.existsQuery(normalizedFieldName);
                } else {
                    // Check if the nested field doesn't exist (meaning the parent nested object is null)
                    return QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(normalizedFieldName));
                }
            }
        }
        
        // Detect nested path (e.g., "department.name" or "roles.roleAuthorizations.authorization.menuIcon")
        NestedPathInfo nestedPathInfo = analyzeNestedPath(normalizedFieldName);
        
        if (nestedPathInfo != null && nestedPathInfo.hasNestedPath()) {
            // Build nested query
            return buildNestedQuery(nestedPathInfo, operator, values, false);
        }
        
        switch (operator) {
            case EQUAL:
                if (values.size() > 1) {
                    // Multiple values - build OR query with match_phrase for each value
                    BoolQueryBuilder orQuery = QueryBuilders.boolQuery();
                    for (Object value : values) {
                        // Use match_phrase for exact matching with analysis
                        orQuery.should(QueryBuilders.matchPhraseQuery(fieldName, value));
                    }
                    orQuery.minimumShouldMatch(1);
                    return orQuery;
                } else {
                    // Single value - use match_phrase for exact phrase matching
                    return QueryBuilders.matchPhraseQuery(fieldName, values.get(0));
                }
                
            case NOT_EQUAL:
                BoolQueryBuilder notEqualQuery = QueryBuilders.boolQuery();
                notEqualQuery.must(QueryBuilders.existsQuery(fieldName)); // Field must exist
                if (values.size() > 1) {
                    // Exclude all specified values
                    for (Object value : values) {
                        notEqualQuery.mustNot(QueryBuilders.matchPhraseQuery(fieldName, value));
                    }
                } else {
                    // Exclude single value
                    notEqualQuery.mustNot(QueryBuilders.matchPhraseQuery(fieldName, values.get(0)));
                }
                return notEqualQuery;
                
            case CONTAIN:
                return QueryBuilders.wildcardQuery(fieldName, "*" + escapeWildcard(values.get(0).toString()).toLowerCase() + "*")
                    .caseInsensitive(true);
                
            case DOES_NOT_CONTAIN:
                return QueryBuilders.boolQuery()
                    .must(QueryBuilders.existsQuery(fieldName)) // Field must exist
                    .mustNot(
                        QueryBuilders.wildcardQuery(fieldName, "*" + escapeWildcard(values.get(0).toString()).toLowerCase() + "*")
                            .caseInsensitive(true)
                    );
                
            case START_WITH:
                return QueryBuilders.prefixQuery(fieldName, values.get(0).toString().toLowerCase())
                    .caseInsensitive(true);
                
            case END_WITH:
                return QueryBuilders.wildcardQuery(fieldName, "*" + escapeWildcard(values.get(0).toString()).toLowerCase())
                    .caseInsensitive(true);
                
            case GREATER_THAN:
                return QueryBuilders.rangeQuery(fieldName).gt(values.get(0));
                
            case GREATER_THAN_OR_EQUAL:
                return QueryBuilders.rangeQuery(fieldName).gte(values.get(0));
                
            case LESS_THAN:
                return QueryBuilders.rangeQuery(fieldName).lt(values.get(0));
                
            case LESS_THAN_OR_EQUAL:
                return QueryBuilders.rangeQuery(fieldName).lte(values.get(0));
                
            case SPECIFIED:
                boolean exists = Boolean.parseBoolean(values.get(0).toString());
                if (exists) {
                    return QueryBuilders.existsQuery(fieldName);
                } else {
                    return QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(fieldName));
                }
                
            default:
                throw new DynamicQueryIllegalArgumentException("Unsupported operator: " + operator);
        }
    }

    /**
     * Escape wildcard special characters
     */
    private String escapeWildcard(String value) {
        return value.replace("\\", "\\\\")
                   .replace("*", "\\*")
                   .replace("?", "\\?");
    }
    
    /**
     * Analyze a field path to detect nested paths and extract information
     * Returns null if the path doesn't contain nested fields
     * 
     * Note: This implementation treats dot-notation fields as potentially nested.
     * Fields marked with @Field(type = FieldType.Object) should use regular queries,
     * while @Field(type = FieldType.Nested) requires nested queries.
     * 
     * Since we can't easily introspect field types at runtime without reflection,
     * we use a heuristic: only create nested paths for known nested relationships.
     * For now, we return all paths and rely on Elasticsearch to handle them appropriately.
     */
    private NestedPathInfo analyzeNestedPath(String fieldPath) {
        if (!fieldPath.contains(".")) {
            return null; // Not a nested path
        }
        
        // Common embedded (Object type) fields that should NOT use nested queries
        Set<String> embeddedFields = Set.of("address");
        
        String firstSegment = fieldPath.substring(0, fieldPath.indexOf("."));
        if (embeddedFields.contains(firstSegment)) {
            // This is an embedded object, not a nested document
            // Don't use nested query
            return null;
        }
        
        // For Elasticsearch, we need to identify which part of the path is nested
        // Example: "department.name" - if department is nested
        // Example: "roles.roleAuthorizations.authorization.menuIcon" - multi-level nested
        
        List<String> nestedPaths = new ArrayList<>();
        String[] segments = fieldPath.split("\\.");
        StringBuilder currentPath = new StringBuilder();
        
        for (int i = 0; i < segments.length - 1; i++) {
            if (currentPath.length() > 0) {
                currentPath.append(".");
            }
            currentPath.append(segments[i]);
            
            // Add as potential nested path
            nestedPaths.add(currentPath.toString());
        }
        
        if (nestedPaths.isEmpty()) {
            return null;
        }
        
        return new NestedPathInfo(nestedPaths, fieldPath);
    }
    
    /**
     * Build a nested query for Elasticsearch
     * Handles multi-level nesting like roles.roleAuthorizations.authorization.menuIcon
     */
    private QueryBuilder buildNestedQuery(NestedPathInfo pathInfo, CriteriaOperator operator, List<Object> values, boolean isLeftJoin) {
        // Build the inner query for the final field
        QueryBuilder innerQuery = buildFieldQuery(pathInfo.fullPath, operator, values);
        
        if (innerQuery == null) {
            return null;
        }
        
        // Wrap in nested queries from innermost to outermost
        // For "roles.roleAuthorizations.authorization.menuIcon", we need:
        // nested(roles.roleAuthorizations.authorization, nested(roles.roleAuthorizations, nested(roles, query)))
        
        List<String> nestedPaths = pathInfo.nestedPaths;
        QueryBuilder currentQuery = innerQuery;
        
        // Start from the deepest nested path and work outward
        for (int i = nestedPaths.size() - 1; i >= 0; i--) {
            String nestedPath = nestedPaths.get(i);
            
            // Use SCORE mode for better relevance, or AVG for numeric aggregations
            ScoreMode scoreMode = ScoreMode.None;
            
            if (isLeftJoin) {
                // For left joins, we want to include documents even if the nested path doesn't exist
                // Wrap in a bool query with should
                BoolQueryBuilder leftJoinQuery = QueryBuilders.boolQuery();
                leftJoinQuery.should(QueryBuilders.nestedQuery(nestedPath, currentQuery, scoreMode));
                leftJoinQuery.should(QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(nestedPath)));
                leftJoinQuery.minimumShouldMatch(1);
                currentQuery = leftJoinQuery;
            } else {
                // Inner join - use nested query directly
                currentQuery = QueryBuilders.nestedQuery(nestedPath, currentQuery, scoreMode);
            }
        }
        
        return currentQuery;
    }
    
    /**
     * Build a query for a single field (used within nested queries)
     * This is similar to buildCriteriaQuery but for a specific field without nesting logic
     */
    private QueryBuilder buildFieldQuery(String fieldName, CriteriaOperator operator, List<Object> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        
        switch (operator) {
            case EQUAL:
                if (values.size() > 1) {
                    BoolQueryBuilder orQuery = QueryBuilders.boolQuery();
                    for (Object value : values) {
                        orQuery.should(QueryBuilders.matchPhraseQuery(fieldName, value));
                    }
                    orQuery.minimumShouldMatch(1);
                    return orQuery;
                } else {
                    return QueryBuilders.matchPhraseQuery(fieldName, values.get(0));
                }
                
            case NOT_EQUAL:
                BoolQueryBuilder notEqualQuery = QueryBuilders.boolQuery();
                notEqualQuery.must(QueryBuilders.existsQuery(fieldName));
                if (values.size() > 1) {
                    for (Object value : values) {
                        notEqualQuery.mustNot(QueryBuilders.matchPhraseQuery(fieldName, value));
                    }
                } else {
                    notEqualQuery.mustNot(QueryBuilders.matchPhraseQuery(fieldName, values.get(0)));
                }
                return notEqualQuery;
                
            case CONTAIN:
                return QueryBuilders.wildcardQuery(fieldName, "*" + escapeWildcard(values.get(0).toString()).toLowerCase() + "*")
                    .caseInsensitive(true);
                
            case DOES_NOT_CONTAIN:
                return QueryBuilders.boolQuery()
                    .must(QueryBuilders.existsQuery(fieldName))
                    .mustNot(
                        QueryBuilders.wildcardQuery(fieldName, "*" + escapeWildcard(values.get(0).toString()).toLowerCase() + "*")
                            .caseInsensitive(true)
                    );
                
            case START_WITH:
                return QueryBuilders.prefixQuery(fieldName, values.get(0).toString().toLowerCase())
                    .caseInsensitive(true);
                
            case END_WITH:
                return QueryBuilders.wildcardQuery(fieldName, "*" + escapeWildcard(values.get(0).toString()).toLowerCase())
                    .caseInsensitive(true);
                
            case GREATER_THAN:
                return QueryBuilders.rangeQuery(fieldName).gt(values.get(0));
                
            case GREATER_THAN_OR_EQUAL:
                return QueryBuilders.rangeQuery(fieldName).gte(values.get(0));
                
            case LESS_THAN:
                return QueryBuilders.rangeQuery(fieldName).lt(values.get(0));
                
            case LESS_THAN_OR_EQUAL:
                return QueryBuilders.rangeQuery(fieldName).lte(values.get(0));
                
            case SPECIFIED:
                boolean exists = Boolean.parseBoolean(values.get(0).toString());
                if (exists) {
                    return QueryBuilders.existsQuery(fieldName);
                } else {
                    return QueryBuilders.boolQuery().mustNot(QueryBuilders.existsQuery(fieldName));
                }
                
            default:
                throw new DynamicQueryIllegalArgumentException("Unsupported operator: " + operator);
        }
    }
    
    /**
     * Helper class to store nested path information
     */
    private static class NestedPathInfo {
        private final List<String> nestedPaths;  // e.g., ["roles", "roles.roleAuthorizations", "roles.roleAuthorizations.authorization"]
        private final String fullPath;            // e.g., "roles.roleAuthorizations.authorization.menuIcon"
        
        public NestedPathInfo(List<String> nestedPaths, String fullPath) {
            this.nestedPaths = nestedPaths;
            this.fullPath = fullPath;
        }
        
        public boolean hasNestedPath() {
            return nestedPaths != null && !nestedPaths.isEmpty();
        }
    }
}

