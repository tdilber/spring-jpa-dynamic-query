package com.beyt.jdq.jpa;

import com.beyt.jdq.jpa.annotation.EnableJpaDynamicQuery;
import com.beyt.jdq.core.annotation.EnableDynamicQueryArgumentResolvers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableJpaDynamicQuery
@EnableDynamicQueryArgumentResolvers
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

}
