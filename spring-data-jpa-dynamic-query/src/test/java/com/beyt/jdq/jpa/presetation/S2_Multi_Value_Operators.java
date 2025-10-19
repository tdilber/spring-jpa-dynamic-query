package com.beyt.jdq.jpa.presetation;

import com.beyt.jdq.jpa.BaseTestInstance;
import com.beyt.jdq.jpa.TestApplication;
import com.beyt.jdq.core.model.Criteria;
import com.beyt.jdq.core.model.CriteriaList;
import com.beyt.jdq.core.model.enums.CriteriaOperator;
import com.beyt.jdq.jpa.testenv.entity.school.Course;
import com.beyt.jdq.jpa.testenv.repository.CourseRepository;
import com.beyt.jdq.jpa.testenv.repository.DepartmentRepository;
import com.beyt.jdq.jpa.util.PresentationUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = TestApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class S2_Multi_Value_Operators extends BaseTestInstance {
    private @Autowired CourseRepository courseRepository;
    private @Autowired DepartmentRepository departmentRepository;


    @Test
    public void equal() {
        var criteriaList = CriteriaList.of(Criteria.of("name", CriteriaOperator.EQUAL, "Calculus I", "Calculus II"));
        PresentationUtil.prettyPrint(criteriaList);
        List<Course> courseList = courseRepository.findAll(criteriaList);
        PresentationUtil.prettyPrint(courseList); //2,3
        assertEquals(List.of(course2, course3), courseList);
    }

    @Test
    public void equalInteger() {
        var criteriaList = CriteriaList.of(Criteria.of("maxStudentCount", CriteriaOperator.EQUAL, 40, 50));
        PresentationUtil.prettyPrint(criteriaList);
        List<Course> courseList = courseRepository.findAll(criteriaList);
        PresentationUtil.prettyPrint(courseList);//1,6
        assertEquals(List.of(course1, course6), courseList);
    }

    @Test
    public void notEqual() {
        var criteriaList = CriteriaList.of(Criteria.of("name", CriteriaOperator.NOT_EQUAL, "Calculus I", "Calculus II"));
        PresentationUtil.prettyPrint(criteriaList);
        List<Course> courseList = courseRepository.findAll(criteriaList);
        PresentationUtil.prettyPrint(courseList);//1,4,5,6,7,8,9,10
        assertEquals(Arrays.asList(course1, course4, course5, course6, course7, course8, course9, course10), courseList);
    }

    @Test
    public void notEqualDate() {
        var criteriaList = CriteriaList.of(Criteria.of("startDate", CriteriaOperator.NOT_EQUAL, "2013-06-18", "2015-06-18", "2016-06-18"));
        PresentationUtil.prettyPrint(criteriaList);
        List<Course> courseList = courseRepository.findAll(criteriaList);
        PresentationUtil.prettyPrint(courseList);
    }
}
