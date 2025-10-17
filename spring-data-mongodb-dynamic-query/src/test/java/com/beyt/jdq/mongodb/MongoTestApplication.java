package com.beyt.jdq.mongodb;

import com.beyt.jdq.mongodb.annotation.EnableMongoDbDynamicQuery;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Test application for MongoDB dynamic query tests.
 * Excludes JPA auto-configuration since we're using MongoDB.
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@EnableMongoDbDynamicQuery
public class MongoTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(MongoTestApplication.class, args);
    }
}

