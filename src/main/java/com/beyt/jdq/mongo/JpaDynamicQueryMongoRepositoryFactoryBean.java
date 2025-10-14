package com.beyt.jdq.mongo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.lang.NonNull;

import java.io.Serializable;

/**
 * Factory bean for creating JpaDynamicQueryMongoRepository instances.
 * Similar to JpaDynamicQueryRepositoryFactoryBean for JPA but for MongoDB.
 * 
 * To enable this factory, use:
 * @EnableMongoRepositories(repositoryFactoryBeanClass = JpaDynamicQueryMongoRepositoryFactoryBean.class)
 */
public class JpaDynamicQueryMongoRepositoryFactoryBean<R extends Repository<T, ID>, T, ID extends Serializable>
        extends MongoRepositoryFactoryBean<R, T, ID> {

    @Autowired
    private MongoSearchQueryTemplate mongoSearchQueryTemplate;

    public JpaDynamicQueryMongoRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    @NonNull
    protected RepositoryFactorySupport getFactoryInstance(@NonNull MongoOperations operations) {
        return new JpaDynamicQueryMongoRepositoryFactory(operations, mongoSearchQueryTemplate);
    }

    /**
     * Custom factory that creates JpaDynamicQueryMongoRepositoryImpl instances
     */
    private static class JpaDynamicQueryMongoRepositoryFactory extends MongoRepositoryFactory {

        private final MongoOperations mongoOperations;
        private final MongoSearchQueryTemplate mongoSearchQueryTemplate;

        public JpaDynamicQueryMongoRepositoryFactory(
                MongoOperations mongoOperations,
                MongoSearchQueryTemplate mongoSearchQueryTemplate) {
            super(mongoOperations);
            this.mongoOperations = mongoOperations;
            this.mongoSearchQueryTemplate = mongoSearchQueryTemplate;
        }

        @Override
        @NonNull
        protected Object getTargetRepository(@NonNull RepositoryInformation information) {
            MongoEntityInformation<?, Serializable> entityInformation = 
                getEntityInformation(information.getDomainType());
            
            return new JpaDynamicQueryMongoRepositoryImpl<>(
                entityInformation,
                mongoOperations,
                mongoSearchQueryTemplate
            );
        }

        @Override
        @NonNull
        protected Class<?> getRepositoryBaseClass(@NonNull RepositoryMetadata metadata) {
            return JpaDynamicQueryMongoRepositoryImpl.class;
        }
    }
}

