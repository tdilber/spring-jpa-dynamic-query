package com.beyt.jdq.mongodb.config;

import com.beyt.jdq.core.deserializer.IDeserializer;
import com.beyt.jdq.mongodb.core.MongoSearchQueryTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Configuration class for MongoSearchQueryTemplate.
 * This is automatically imported when using @EnableMongoDbDynamicQuery.
 */
@Configuration
public class MongoSearchQueryTemplateConfig {

    /**
     * Creates a MongoSearchQueryTemplate bean.
     * 
     * @param mongoTemplate the MongoTemplate to use for queries
     * @param deserializer the deserializer to use for converting query values
     * @return configured MongoSearchQueryTemplate
     */
    @Bean
    public MongoSearchQueryTemplate mongoSearchQueryTemplate(MongoTemplate mongoTemplate, IDeserializer deserializer) {
        return new MongoSearchQueryTemplate(mongoTemplate, deserializer);
    }
}

