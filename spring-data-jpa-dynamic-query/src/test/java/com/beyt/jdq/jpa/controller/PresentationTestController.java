package com.beyt.jdq.jpa.controller;

import com.beyt.jdq.core.model.CriteriaList;
import com.beyt.jdq.core.model.DynamicQuery;
import com.beyt.jdq.jpa.presetation.S9_Query_Builder;
import com.beyt.jdq.jpa.testenv.entity.school.Course;
import com.beyt.jdq.jpa.testenv.repository.AdminUserRepository;
import com.beyt.jdq.jpa.testenv.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test-api")
public class PresentationTestController {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private AdminUserRepository adminUserRepository;


    @GetMapping("/course")
    public ResponseEntity<List<Course>> getCourseWithCriteria(CriteriaList criteriaList) {
        List<Course> customerList = courseRepository.findAll(criteriaList);
        return ResponseEntity.ok().body(customerList);
    }

    @GetMapping("/course/as-page")
    public ResponseEntity<Page<Course>> getCourseWithSearchFilterAsPage(DynamicQuery dynamicQuery) {
        Page<Course> customerList = courseRepository.findAllAsPage(dynamicQuery);
        return ResponseEntity.ok().body(customerList);
    }

    @GetMapping("/course/as-list")
    public ResponseEntity<List<S9_Query_Builder.AuthorizationSummary>> getCourseWithSearchFilterAsList(DynamicQuery dynamicQuery) {
        List<S9_Query_Builder.AuthorizationSummary> customerList = adminUserRepository.findAll(dynamicQuery, S9_Query_Builder.AuthorizationSummary.class);
        return ResponseEntity.ok().body(customerList);
    }
}
