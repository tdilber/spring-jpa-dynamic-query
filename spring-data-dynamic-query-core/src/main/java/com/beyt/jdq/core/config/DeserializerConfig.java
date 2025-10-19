package com.beyt.jdq.core.config;

import com.beyt.jdq.core.deserializer.BasicDeserializer;
import com.beyt.jdq.core.deserializer.IDeserializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeserializerConfig {

    @Bean
    @ConditionalOnMissingBean
    public IDeserializer basicDeserializer() {
        return new BasicDeserializer();
//        return new JacksonDeserializer();// TODO
    }
}
