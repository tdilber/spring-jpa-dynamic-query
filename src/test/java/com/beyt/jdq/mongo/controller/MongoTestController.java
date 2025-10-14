package com.beyt.jdq.mongo.controller;

import com.beyt.jdq.dto.CriteriaList;
import com.beyt.jdq.dto.DynamicQuery;
import com.beyt.jdq.mongo.S9_Query_Builder;
import com.beyt.jdq.mongo.entity.Course;
import com.beyt.jdq.mongo.repository.MongoAdminUserRepository;
import com.beyt.jdq.mongo.repository.MongoCourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * MongoDB test controller for testing argument resolvers.
 * Provides REST endpoints that use CriteriaList and DynamicQuery as method parameters.
 * The argument resolvers automatically convert query parameters to these types.
 */
@RestController
@RequestMapping("/mongo-test-api")
public class MongoTestController {
    @Autowired
    private MongoCourseRepository courseRepository;
    
    @Autowired
    private MongoAdminUserRepository adminUserRepository;

    /**
     * Test CriteriaList argument resolver
     * Example: /mongo-test-api/course?key0=name&operation0=CONTAIN&values0=Calculus
     */
    @GetMapping("/course")
    public ResponseEntity<List<Course>> getCourseWithCriteria(CriteriaList criteriaList) {
        List<Course> courseList = courseRepository.findAll(criteriaList);
        return ResponseEntity.ok().body(courseList);
    }

    /**
     * Test DynamicQuery argument resolver for pagination
     * Example: /mongo-test-api/course/as-page?key0=name&operation0=START_WITH&values0=Physics&page=0&pageSize=10
     */
    @GetMapping("/course/as-page")
    public ResponseEntity<Page<Course>> getCourseWithSearchFilterAsPage(DynamicQuery dynamicQuery) {
        Page<Course> coursePage = courseRepository.findAllAsPage(dynamicQuery);
        return ResponseEntity.ok().body(coursePage);
    }

    /**
     * Test DynamicQuery argument resolver with projection
     * Example: /mongo-test-api/course/as-list?select0=id&select1=username&selectAs0=adminId&selectAs1=adminUsername
     */
    @GetMapping("/course/as-list")
    public ResponseEntity<List<S9_Query_Builder.AuthorizationSummary>> getCourseWithSearchFilterAsList(DynamicQuery dynamicQuery) {
        List<S9_Query_Builder.AuthorizationSummary> summaryList = adminUserRepository.findAll(dynamicQuery, S9_Query_Builder.AuthorizationSummary.class);
        return ResponseEntity.ok().body(summaryList);
    }
}

