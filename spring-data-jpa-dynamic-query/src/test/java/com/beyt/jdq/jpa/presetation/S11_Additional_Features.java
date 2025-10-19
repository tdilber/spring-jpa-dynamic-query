package com.beyt.jdq.jpa.presetation;

import com.beyt.jdq.jpa.BaseTestInstance;
import com.beyt.jdq.jpa.TestApplication;
import com.beyt.jdq.core.model.Criteria;
import com.beyt.jdq.core.model.CriteriaList;
import com.beyt.jdq.core.model.enums.CriteriaOperator;
import com.beyt.jdq.jpa.testenv.entity.school.Course;
import com.beyt.jdq.jpa.testenv.repository.CourseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = TestApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class S11_Additional_Features extends BaseTestInstance {
    private @Autowired CourseRepository courseRepository;


    @Test
    public void consumePartially() {
        List<Course> courses = new ArrayList<>();
        courseRepository.consumePartially((courseList) -> {
            courseList.forEach(course -> {
                System.out.println(course.getName());
            });
            System.out.println("Consumed partially");
            courses.addAll(courseList);
        }, 2);

        assertEquals(List.of(course1, course2, course3, course4, course5, course6, course7, course8, course9, course10), courses);
    }

    @Test
    public void consumePartially2() {
        List<Course> courses = new ArrayList<>();
        CriteriaList criteriaList = CriteriaList.of(Criteria.of("id", CriteriaOperator.GREATER_THAN, 5));
        courseRepository.consumePartially(criteriaList, (courseList) -> {
            courseList.forEach(course -> {
                System.out.println(course.getName());
            });
            System.out.println("Consumed partially");
            courses.addAll(courseList);
        }, 2);
        assertEquals(List.of(course6, course7, course8, course9, course10), courses);
    }
}
