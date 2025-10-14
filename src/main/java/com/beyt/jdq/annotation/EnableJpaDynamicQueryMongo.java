package com.beyt.jdq.annotation;

import com.beyt.jdq.config.DeserializerConfig;
import com.beyt.jdq.config.MongoSearchQueryTemplateConfig;
import com.beyt.jdq.mongo.JpaDynamicQueryMongoRepositoryFactoryBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to enable JPA Dynamic Query support for MongoDB repositories.
 * 
 * Usage:
 * <pre>
 * @Configuration
 * @EnableJpaDynamicQueryMongo
 * public class MongoConfig {
 *     // Your MongoDB configuration
 * }
 * </pre>
 * 
 * Then your repositories can extend JpaDynamicQueryMongoRepository:
 * <pre>
 * public interface UserRepository extends JpaDynamicQueryMongoRepository&lt;User, String&gt; {
 *     // Your custom methods
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Import({DeserializerConfig.class, MongoSearchQueryTemplateConfig.class})
@EnableMongoRepositories(repositoryFactoryBeanClass = JpaDynamicQueryMongoRepositoryFactoryBean.class)
public @interface EnableJpaDynamicQueryMongo {
}

