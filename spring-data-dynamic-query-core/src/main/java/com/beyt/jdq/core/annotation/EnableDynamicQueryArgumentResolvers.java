package com.beyt.jdq.core.annotation;

import com.beyt.jdq.core.config.ArgumentResolversInitConfig;
import com.beyt.jdq.core.resolver.CriteriaListArgumentResolver;
import com.beyt.jdq.core.resolver.DynamicQueryArgumentResolver;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Import({ArgumentResolversInitConfig.class, CriteriaListArgumentResolver.class, DynamicQueryArgumentResolver.class})
@Target({ElementType.TYPE})
public @interface EnableDynamicQueryArgumentResolvers {
}
