package com.beyt.jdq.mongo;

import com.beyt.jdq.dto.Criteria;
import com.beyt.jdq.dto.DynamicQuery;
import com.beyt.jdq.util.ListConsumer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

import java.io.Serializable;
import java.util.List;

/**
 * MongoDB implementation of JpaDynamicQueryMongoRepository.
 * Extends SimpleMongoRepository and provides dynamic query functionality.
 */
public class JpaDynamicQueryMongoRepositoryImpl<T, ID extends Serializable> 
        extends SimpleMongoRepository<T, ID> 
        implements JpaDynamicQueryMongoRepository<T, ID> {

    private final MongoSearchQueryTemplate mongoSearchQueryTemplate;
    private final Class<T> entityClass;

    public JpaDynamicQueryMongoRepositoryImpl(
            MongoEntityInformation<T, ID> metadata,
            MongoOperations mongoOperations,
            MongoSearchQueryTemplate mongoSearchQueryTemplate) {
        super(metadata, mongoOperations);
        this.mongoSearchQueryTemplate = mongoSearchQueryTemplate;
        this.entityClass = metadata.getJavaType();
    }

    @Override
    public MongoSearchQueryTemplate getMongoSearchQueryTemplate() {
        return mongoSearchQueryTemplate;
    }

    @Override
    public Class<T> getEntityClass() {
        return entityClass;
    }

    @Override
    public List<T> findAll(List<Criteria> criteriaList) {
        return mongoSearchQueryTemplate.findAll(entityClass, criteriaList);
    }

    @Override
    public List<T> findAll(DynamicQuery dynamicQuery) {
        return mongoSearchQueryTemplate.findAll(entityClass, dynamicQuery);
    }

    @Override
    public Page<T> findAllAsPage(DynamicQuery dynamicQuery) {
        return mongoSearchQueryTemplate.findAllAsPage(entityClass, dynamicQuery);
    }

    @Override
    public Page<T> findAll(List<Criteria> criteriaList, Pageable pageable) {
        return mongoSearchQueryTemplate.findAllAsPage(entityClass, criteriaList, pageable);
    }

    @Override
    public long count(List<Criteria> criteriaList) {
        return mongoSearchQueryTemplate.count(entityClass, criteriaList);
    }

    @Override
    public void consumePartially(ListConsumer<T> processor, int pageSize) {
        consumePartially((List<Criteria>) null, processor, pageSize);
    }

    @Override
    public void consumePartially(List<Criteria> criteriaList, ListConsumer<T> processor, int pageSize) {
        long totalElements = criteriaList != null ? 
            mongoSearchQueryTemplate.count(entityClass, criteriaList) : 
            count();

        for (int i = 0; (long) i * pageSize < totalElements; i++) {
            Page<T> page;
            if (criteriaList != null) {
                page = mongoSearchQueryTemplate.findAllAsPage(
                    entityClass, 
                    criteriaList, 
                    PageRequest.of(i, pageSize)
                );
            } else {
                page = findAll(PageRequest.of(i, pageSize));
            }
            processor.accept(page.getContent());
        }
    }

    @Override
    public void consumePartially(DynamicQuery dynamicQuery, ListConsumer<T> processor, int pageSize) {
        dynamicQuery.setPageSize(pageSize);
        long totalElements = mongoSearchQueryTemplate.count(entityClass, dynamicQuery.getWhere());

        for (int i = 0; (long) i * pageSize < totalElements; i++) {
            dynamicQuery.setPageNumber(i);
            List<T> content = mongoSearchQueryTemplate.findAll(entityClass, dynamicQuery);
            processor.accept(content);
        }
    }
}

