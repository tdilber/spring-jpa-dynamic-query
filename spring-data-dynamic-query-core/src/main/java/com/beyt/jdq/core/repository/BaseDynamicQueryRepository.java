package com.beyt.jdq.core.repository;

import com.beyt.jdq.core.model.Criteria;
import com.beyt.jdq.core.model.DynamicQuery;
import com.beyt.jdq.core.model.builder.BaseQueryBuilder;
import com.beyt.jdq.core.util.ListConsumer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface BaseDynamicQueryRepository<T, ID> {
    /**
     * Find all entities matching the criteria list
     */
    List<T> findAll(List<Criteria> criteriaList);

    /**
     * Find all entities matching the dynamic query
     */
    List<T> findAll(DynamicQuery dynamicQuery);

    /**
     * Find all entities with projection matching the dynamic query
     */
    <R> List<R> findAll(DynamicQuery dynamicQuery, Class<R> resultClass);

    /**
     * Find all entities as page matching the dynamic query
     */
    Page<T> findAllAsPage(DynamicQuery dynamicQuery);

    /**
     * Find all entities as page with projection matching the dynamic query
     */
    <R> Page<R> findAllAsPage(DynamicQuery dynamicQuery, Class<R> resultClass);

    /**
     * Find all entities as page matching the criteria list
     */
    Page<T> findAll(List<Criteria> criteriaList, Pageable pageable);

    /**
     * Count entities matching the criteria list
     */
    long count(List<Criteria> criteriaList);

    /**
     * Consume all entities matching no filter in batches
     */
    void consumePartially(ListConsumer<T> processor, int pageSize);

    /**
     * Consume all entities matching criteria list in batches
     */
    void consumePartially(List<Criteria> criteriaList, ListConsumer<T> processor, int pageSize);

    /**
     * Create a query builder for fluent query construction
     */
    BaseQueryBuilder<T, ID> queryBuilder();
}
