package com.beyt.jdq.mongo;

import com.beyt.jdq.dto.Criteria;
import com.beyt.jdq.dto.CriteriaList;
import com.beyt.jdq.dto.enums.CriteriaOperator;
import com.beyt.jdq.mongo.entity.Department;
import com.beyt.jdq.mongo.entity.Student;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * MongoDB tests for JOIN operations.
 * 
 * NOTE: These tests are currently disabled because MongoSearchQueryTemplate does not yet support
 * querying nested/referenced documents (joins) with $lookup aggregation pipeline.
 * 
 * MongoDB handles relationships differently than SQL databases:
 * - Embedded documents: For one-to-one or one-to-few relationships (like address in student)
 * - References with $lookup: For one-to-many or many-to-many relationships
 * 
 * Current implementation status:
 * ✓ Entity structure created with proper relationships
 * ✓ Test data setup complete
 * ✗ MongoSearchQueryTemplate needs $lookup aggregation support for nested queries
 * ✗ Need to handle dot notation for embedded documents (department.name)
 * ✗ Need to handle $lookup for DBRef relationships
 * 
 * To enable these tests:
 * 1. Update MongoSearchQueryTemplate to detect nested field paths
 * 2. Build aggregation pipeline with $lookup stages for DBRef fields
 * 3. Handle embedded document queries (no $lookup needed, just dot notation)
 * 4. Support left join syntax with '<' operator
 */
@SpringBootTest(classes = MongoTestApplication.class)
@ActiveProfiles("mongotest")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class S5_Join extends BaseMongoJoinTestInstance {

    /**
     * Inner join with single entity criteria
     * MongoDB equivalent: $lookup to join students with departments, then filter
     * Example: Find students where department.name starts with "P"
     */
    @Test
    public void innerJoin() {
        var criteriaList = CriteriaList.of(Criteria.of("department.name", CriteriaOperator.START_WITH, "P"));
        System.out.println("INNER JOIN: department.name starts with 'P'");
        List<Student> students = studentRepository.findAll(criteriaList);
        System.out.println("Result: " + students);
        
        // Students 3 (Physics) and 9 (Political Science) have departments starting with "P"
        assertEquals(List.of(student3, student9), students);
    }

    /**
     * Inner join with multi joined entity criteria
     * MongoDB equivalent: $lookup with multiple filter conditions on joined collection
     * Example: Find students where department.name starts with "P" AND department.id > 3
     */
    @Test
    public void innerJoin2() {
        var criteriaList = CriteriaList.of(
                Criteria.of("department.name", CriteriaOperator.START_WITH, "P"),
                Criteria.of("department.id", CriteriaOperator.GREATER_THAN, 3)
        );
        System.out.println("INNER JOIN 2: department.name starts with 'P' AND department.id > 3");
        List<Student> students = studentRepository.findAll(criteriaList);
        System.out.println("Result: " + students);
        
        // Only student9 (Political Science, id=9) matches both criteria
        assertEquals(List.of(student9), students);
    }

    /**
     * Inner join with different entity criteria
     * MongoDB equivalent: Combine $lookup filter with parent document filter
     * Example: Find students where department.name starts with "P" AND student.name starts with "Robert"
     */
    @Test
    public void innerJoin3() {
        var criteriaList = CriteriaList.of(
                Criteria.of("department.name", CriteriaOperator.START_WITH, "P"),
                Criteria.of("name", CriteriaOperator.START_WITH, "Robert")
        );
        System.out.println("INNER JOIN 3: department.name starts with 'P' AND name starts with 'Robert'");
        List<Student> students = studentRepository.findAll(criteriaList);
        System.out.println("Result: " + students);
        
        // Student3 (Robert Johnson, Physics) matches both criteria
        assertEquals(List.of(student3), students);
    }

    /**
     * One to many inner join
     * MongoDB equivalent: $lookup from departments to students, $unwind, then filter
     * Example: Find departments where any student.id > 3 AND department.id < 6
     */
    @Test
    public void innerJoin4() {
        var criteriaList = CriteriaList.of(
                Criteria.of("students.id", CriteriaOperator.GREATER_THAN, 3),
                Criteria.of("id", CriteriaOperator.LESS_THAN, 6)
        );
        System.out.println("INNER JOIN 4: students.id > 3 AND department.id < 6");
        List<Department> departments = departmentRepository.findAll(criteriaList);
        System.out.println("Result: " + departments);
        
        // Departments 4 (Chemistry, student4) and 5 (Biology, student5) match
        assertEquals(List.of(department4, department5), departments);
    }

    /**
     * Many to many inner join
     * MongoDB equivalent: $lookup with courses, $unwind, filter on course properties
     * Example: Find students where any course.maxStudentCount > 100 AND student.id > 3
     */
    @Test
    public void innerJoin5() {
        var criteriaList = CriteriaList.of(
                Criteria.of("courses.maxStudentCount", CriteriaOperator.GREATER_THAN, 100),
                Criteria.of("id", CriteriaOperator.GREATER_THAN, 3)
        );
        System.out.println("INNER JOIN 5: courses.maxStudentCount > 100 AND id > 3");
        List<Student> students = studentRepository.findAll(criteriaList);
        System.out.println("Result: " + students);
        
        // Students 4 (Physics I, 250) and 5 (Physics II, 250) have courses with maxStudentCount > 100
        assertEquals(List.of(student4, student5), students);
    }

    /**
     * Left join with is null check
     * MongoDB equivalent: Query with null check on DBRef field
     * Example: Find students where department is null AND student.id > 3
     * 
     * Note: In MongoDB with DBRef, we check if the reference field itself is null
     */
    @Test
    public void leftJoin() {
        var criteriaList = CriteriaList.of(
                Criteria.of("department<id", CriteriaOperator.SPECIFIED, false),
                Criteria.of("id", CriteriaOperator.GREATER_THAN, 3)
        );
        System.out.println("LEFT JOIN: department is null AND id > 3");
        List<Student> students = studentRepository.findAll(criteriaList);
        System.out.println("Result: " + students);
        
        // Student11 (Talha Dilber) has no department and id=11 > 3
        assertEquals(List.of(student11), students);
    }
    
    /**
     * Test embedded document query (address is embedded in student)
     * No $lookup needed - just dot notation
     */
    @Test
    public void embeddedDocumentQuery() {
        var criteriaList = CriteriaList.of(
                Criteria.of("address.city", CriteriaOperator.EQUAL, "Los Angeles")
        );
        System.out.println("EMBEDDED: address.city = 'Los Angeles'");
        List<Student> students = studentRepository.findAll(criteriaList);
        System.out.println("Result: " + students);
        
        // Student3 (Robert Johnson) lives in Los Angeles
        assertEquals(List.of(student3), students);
    }
}

