package com.beyt.jdq.mongo;

import com.beyt.jdq.annotation.EnableJpaDynamicQueryMongo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * Test application for MongoDB dynamic query tests.
 * Excludes JPA auto-configuration since we're using MongoDB.
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@EnableJpaDynamicQueryMongo
@ComponentScan(basePackages = {"com.beyt.jdq.mongo", "com.beyt.jdq.deserializer"})
public class MongoTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(MongoTestApplication.class, args);
    }
}

