package com.beyt.jdq.jpa.annotation;

import com.beyt.jdq.core.config.DeserializerConfig;
import com.beyt.jdq.jpa.repository.JpaDynamicQueryRepositoryFactoryBean;
import com.beyt.jdq.jpa.repository.JpaDynamicQueryRepositoryImpl;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Import({DeserializerConfig.class})
@EnableJpaRepositories(repositoryBaseClass = JpaDynamicQueryRepositoryImpl.class, repositoryFactoryBeanClass = JpaDynamicQueryRepositoryFactoryBean.class)
public @interface EnableJpaDynamicQuery {
}
