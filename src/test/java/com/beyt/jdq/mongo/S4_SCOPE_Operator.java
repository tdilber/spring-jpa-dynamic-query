package com.beyt.jdq.mongo;

import com.beyt.jdq.dto.Criteria;
import com.beyt.jdq.dto.CriteriaList;
import com.beyt.jdq.dto.enums.CriteriaOperator;
import com.beyt.jdq.mongo.entity.Course;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * MongoDB tests for SCOPE/PARENTHESES operators.
 * 
 * The PARENTHES operator allows complex nested query logic similar to SQL parentheses.
 * It recursively processes nested criteria lists to create proper MongoDB query structures.
 * 
 * Example MongoDB query for "(id=1 OR id=2) AND (id=2 OR id=3)":
 * {
 *   $and: [
 *     { $or: [{ id: 1 }, { id: 2 }] },
 *     { $or: [{ id: 2 }, { id: 3 }] }
 *   ]
 * }
 * Result should be: only id=2
 */
@SpringBootTest(classes = MongoTestApplication.class)
@ActiveProfiles("mongotest")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class S4_SCOPE_Operator extends BaseMongoTestInstance {

    /**
     * Test: (A OR B) AND (C OR D)
     * Tests basic scope with two OR groups combined with AND
     */
    @Test
    public void scope() {
        var criteriaList = CriteriaList.of(
                Criteria.of("", CriteriaOperator.PARENTHES,
                        CriteriaList.of(Criteria.of("id", CriteriaOperator.EQUAL, 1),
                                Criteria.OR(),
                                Criteria.of("id", CriteriaOperator.EQUAL, 2))),
                Criteria.of("", CriteriaOperator.PARENTHES,
                        CriteriaList.of(Criteria.of("id", CriteriaOperator.EQUAL, 2),
                                Criteria.OR(),
                                Criteria.of("id", CriteriaOperator.EQUAL, 3)))
        );
        System.out.println("SCOPE: (id=1 OR id=2) AND (id=2 OR id=3)");
        List<Course> courseList = courseRepository.findAll(criteriaList);
        System.out.println("Result: " + courseList);
        
        // Only course2 satisfies both groups: (1 OR 2) AND (2 OR 3) = 2
        assertEquals(List.of(course2), courseList);
    }

    /**
     * Test: Nested scopes - scope inside scope
     * Tests more complex nested parentheses logic
     */
    @Test
    public void scopeInsideScope() {
        var criteriaList = CriteriaList.of(
                Criteria.of("", CriteriaOperator.PARENTHES,
                        CriteriaList.of(
                                Criteria.of("id", CriteriaOperator.EQUAL, 1, 2, 3),
                                Criteria.of("id", CriteriaOperator.NOT_EQUAL, 2),
                                Criteria.OR(),
                                Criteria.of("", CriteriaOperator.PARENTHES,
                                        CriteriaList.of(
                                                Criteria.of("id", CriteriaOperator.EQUAL, 2),
                                                Criteria.of("id", CriteriaOperator.NOT_EQUAL, 3)))))
        );
        System.out.println("SCOPE INSIDE SCOPE: ((id IN (1,2,3) AND id!=2) OR (id=2 AND id!=3))");
        List<Course> courseList = courseRepository.findAll(criteriaList);
        System.out.println("Result: " + courseList);
        
        // First part: id IN (1,2,3) AND id!=2 => courses 1, 3
        // Second part: id=2 AND id!=3 => course 2
        // Combined with OR: courses 1, 2, 3
        // Sort by ID to ensure consistent ordering
        courseList.sort((a, b) -> Long.compare(a.getId(), b.getId()));
        assertEquals(List.of(course1, course2, course3), courseList);
    }

    /**
     * Test: Complex scope with string operations
     * Tests scopes with non-numeric criteria
     */
    @Test
    public void scopeWithStringOperations() {
        var criteriaList = CriteriaList.of(
                Criteria.of("", CriteriaOperator.PARENTHES,
                        CriteriaList.of(
                                Criteria.of("name", CriteriaOperator.CONTAIN, "I"),
                                Criteria.OR(),
                                Criteria.of("name", CriteriaOperator.CONTAIN, "II"))),
                Criteria.of("maxStudentCount", CriteriaOperator.GREATER_THAN, 50)
        );
        System.out.println("SCOPE WITH STRINGS: (name CONTAINS 'I' OR name CONTAINS 'II') AND maxStudentCount > 50");
        List<Course> courseList = courseRepository.findAll(criteriaList);
        System.out.println("Result: " + courseList);
        
        // Names with "I" or "II", AND maxStudentCount > 50:
        // course2 (Calculus I, 60), course3 (Calculus II, 250), 
        // course4 (Physics I, 250), course5 (Physics II, 250), course9 (Biology II, 54)
        assertEquals(List.of(course2, course3, course4, course5, course9), courseList);
    }

    /**
     * Test: Multiple independent scopes
     * Tests three separate OR groups combined with AND
     */
    @Test
    public void multipleScopes() {
        var criteriaList = CriteriaList.of(
                Criteria.of("", CriteriaOperator.PARENTHES,
                        CriteriaList.of(
                                Criteria.of("id", CriteriaOperator.EQUAL, 1),
                                Criteria.OR(),
                                Criteria.of("id", CriteriaOperator.EQUAL, 2),
                                Criteria.OR(),
                                Criteria.of("id", CriteriaOperator.EQUAL, 3))),
                Criteria.of("", CriteriaOperator.PARENTHES,
                        CriteriaList.of(
                                Criteria.of("active", CriteriaOperator.SPECIFIED, true),
                                Criteria.OR(),
                                Criteria.of("maxStudentCount", CriteriaOperator.LESS_THAN, 100)))
        );
        System.out.println("MULTIPLE SCOPES: (id IN (1,2,3)) AND (active IS NOT NULL OR maxStudentCount < 100)");
        List<Course> courseList = courseRepository.findAll(criteriaList);
        System.out.println("Result: " + courseList);
        
        // id IN (1,2,3): courses 1, 2, 3
        // active not null OR maxStudentCount < 100: course1 (true, 50), course2 (true, 60), course3 (null, 250) -> 1, 2 match
        assertEquals(List.of(course1, course2), courseList);
    }

    /**
     * Test: Deep nesting with multiple levels
     * Tests deeply nested parentheses
     */
    @Test
    public void deeplyNestedScope() {
        var criteriaList = CriteriaList.of(
                Criteria.of("", CriteriaOperator.PARENTHES,
                        CriteriaList.of(
                                Criteria.of("", CriteriaOperator.PARENTHES,
                                        CriteriaList.of(
                                                Criteria.of("id", CriteriaOperator.LESS_THAN, 3),
                                                Criteria.OR(),
                                                Criteria.of("id", CriteriaOperator.GREATER_THAN, 8))),
                                Criteria.of("active", CriteriaOperator.EQUAL, true)))
        );
        System.out.println("DEEPLY NESTED: ((id < 3 OR id > 8) AND active = true)");
        List<Course> courseList = courseRepository.findAll(criteriaList);
        System.out.println("Result: " + courseList);
        
        // (id < 3 OR id > 8): courses 1, 2, 9, 10
        // AND active = true: course1 (true), course2 (true), course9 (true), course10 (false)
        assertEquals(List.of(course1, course2, course9), courseList);
    }
}

