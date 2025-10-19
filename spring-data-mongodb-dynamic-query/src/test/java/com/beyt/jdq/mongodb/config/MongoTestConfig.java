package com.beyt.jdq.mongodb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * MongoDB configuration for tests.
 * Enables MongoDB repositories for the test package.
 */
@Configuration
//@EnableMongoRepositories(basePackages = "com.beyt.jdq.mongodb.repository")
public class MongoTestConfig {
    // MongoTemplate bean will be auto-configured by Spring Boot
    // MongoSearchQueryTemplate will be configured by @EnableJpaDynamicQueryMongo
}

