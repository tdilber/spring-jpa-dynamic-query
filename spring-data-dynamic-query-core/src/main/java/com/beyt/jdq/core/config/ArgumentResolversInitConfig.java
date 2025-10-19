package com.beyt.jdq.core.config;

import com.beyt.jdq.core.resolver.CriteriaListArgumentResolver;
import com.beyt.jdq.core.resolver.DynamicQueryArgumentResolver;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class ArgumentResolversInitConfig implements WebMvcConfigurer {

    private final CriteriaListArgumentResolver criteriaListArgumentResolver;
    private final DynamicQueryArgumentResolver dynamicQueryArgumentResolver;

    public ArgumentResolversInitConfig(CriteriaListArgumentResolver criteriaListArgumentResolver, DynamicQueryArgumentResolver dynamicQueryArgumentResolver) {
        this.criteriaListArgumentResolver = criteriaListArgumentResolver;
        this.dynamicQueryArgumentResolver = dynamicQueryArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(criteriaListArgumentResolver);
        resolvers.add(dynamicQueryArgumentResolver);
    }
}
