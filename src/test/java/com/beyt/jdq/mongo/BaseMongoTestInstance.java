package com.beyt.jdq.mongo;

import com.beyt.jdq.mongo.entity.Course;
import com.beyt.jdq.mongo.repository.MongoCourseRepository;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;

/**
 * Base test instance for MongoDB tests.
 * Provides test data setup and common test entities.
 */
public abstract class BaseMongoTestInstance {

    @Autowired
    protected MongoCourseRepository courseRepository;

    public static final Calendar INSTANCE = Calendar.getInstance();

    // Test course data - similar to JPA tests
    protected Course course1;
    protected Course course2;
    protected Course course3;
    protected Course course4;
    protected Course course5;
    protected Course course6;
    protected Course course7;
    protected Course course8;
    protected Course course9;
    protected Course course10;

    public BaseMongoTestInstance() {
        // Initialize test data
        course1 = new Course(1L, "Introduction to Computer Science", createDate(2016, 6, 18), 50, true, "Introduction to fundamental concepts of computer science.");
        course2 = new Course(2L, "Calculus I", createDate(2017, 6, 18), 60, true, "Introduction to fundamental concepts of calculus.");
        course3 = new Course(3L, "Calculus II", createDate(2018, 6, 18), 250, null, "Advanced topics in calculus including integrals and series.");
        course4 = new Course(4L, "Physics I", createDate(2019, 6, 18), 250, null, "Introduction to classical mechanics and Newtonian physics.");
        course5 = new Course(5L, "Physics II", createDate(2020, 6, 18), 250, null, "Advanced topics in physics including electromagnetism and thermodynamics.");
        course6 = new Course(6L, "Chemistry I", createDate(2021, 6, 18), 40, null, "Basic principles of chemistry including atomic structure and chemical bonding.");
        course7 = new Course(7L, "Chemistry II", createDate(2022, 6, 18), 30, null, "Continuation of chemistry studies covering topics like kinetics and equilibrium.");
        course8 = new Course(8L, "Biology I", createDate(2015, 6, 18), 20, true, "Introduction to cellular biology and genetics.");
        course9 = new Course(9L, "Biology II", createDate(2013, 6, 18), 54, true, "Advanced topics in biology including evolution and ecology.");
        course10 = new Course(10L, "English Literature I", createDate(2025, 6, 18), 10, false, "Exploration of classic works of English literature and literary analysis.");
    }

    @BeforeAll
    public void init() {
        // Clear existing data
        courseRepository.deleteAll();
        
        // Insert test data
        courseRepository.save(course1);
        courseRepository.save(course2);
        courseRepository.save(course3);
        courseRepository.save(course4);
        courseRepository.save(course5);
        courseRepository.save(course6);
        courseRepository.save(course7);
        courseRepository.save(course8);
        courseRepository.save(course9);
        courseRepository.save(course10);
    }

    /**
     * Helper method to create Date objects
     */
    protected Date createDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}

