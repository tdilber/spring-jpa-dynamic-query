package com.beyt.jdq.jpa.presetation;

import com.beyt.jdq.core.model.Criteria;
import com.beyt.jdq.core.model.CriteriaList;
import com.beyt.jdq.core.model.DynamicQuery;
import com.beyt.jdq.core.model.annotation.JdqField;
import com.beyt.jdq.core.model.annotation.JdqModel;
import com.beyt.jdq.core.model.enums.CriteriaOperator;
import com.beyt.jdq.core.model.enums.Order;
import com.beyt.jdq.jpa.BaseTestInstance;
import com.beyt.jdq.jpa.TestApplication;
import com.beyt.jdq.jpa.testenv.repository.CourseRepository;
import com.beyt.jdq.jpa.testenv.repository.DepartmentRepository;
import com.beyt.jdq.jpa.testenv.repository.StudentRepository;
import com.beyt.jdq.jpa.util.PresentationUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import org.springframework.test.annotation.DirtiesContext;

import javax.persistence.Tuple;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for aggregate expressions with GROUP BY and HAVING clauses
 * Tests all 6 aggregate functions: COUNT, COUNT_DISTINCT, SUM, AVG, MAX, MIN
 * Includes tests with joined fields and complex scenarios
 */
@SpringBootTest(classes = TestApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class S13_GroupBy_Having_Aggregate extends BaseTestInstance {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    /**
     * Test simple COUNT aggregate with GROUP BY using custom class projection
     * Groups students by city and counts students in each city
     */
    @Test
    public void testCountAggregate() {
        System.out.println("\n=== Test COUNT Aggregate with Custom Class ===");
        DynamicQuery dynamicQuery = new DynamicQuery();
        dynamicQuery.getSelect().add(Pair.of("address.city", "city"));
        dynamicQuery.getSelect().add(Pair.of("[Count]id", "studentCount"));
        dynamicQuery.setGroupBy(List.of("address.city"));
        dynamicQuery.setOrderBy(List.of(Pair.of("address.city", Order.ASC)));
        dynamicQuery.setWhere(CriteriaList.of(Criteria.of("address.city", CriteriaOperator.SPECIFIED, true)));
        
        PresentationUtil.prettyPrint(dynamicQuery);
        List<CityCount> results = studentRepository.findAll(dynamicQuery, CityCount.class);
        PresentationUtil.prettyPrint(results);
        
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        // Verify each city has exactly 1 student
        for (CityCount cityCount : results) {
            System.out.println("City: " + cityCount.getCity() + ", Count: " + cityCount.getStudentCount());
            assertNotNull(cityCount.getCity());
            assertEquals(1L, cityCount.getStudentCount());
        }
    }

    /**
     * Test COUNT DISTINCT aggregate
     * Counts distinct states in addresses
     */
    @Test
    public void testCountDistinctAggregate() {
        System.out.println("\n=== Test COUNT DISTINCT Aggregate ===");
        DynamicQuery dynamicQuery = new DynamicQuery();
        dynamicQuery.getSelect().add(Pair.of("[CountDistinct]address.state", "distinctStates"));
        dynamicQuery.setWhere(CriteriaList.of(Criteria.of("address.state", CriteriaOperator.SPECIFIED, true)));
        
        PresentationUtil.prettyPrint(dynamicQuery);
        List<Tuple> results = studentRepository.findAllAsTuple(dynamicQuery);
        
        assertNotNull(results);
        assertEquals(1, results.size());
        
        Long distinctCount = (Long) results.get(0).get(0);
        System.out.println("Distinct States Count: " + distinctCount);
        assertTrue(distinctCount > 0);
    }

    /**
     * Test SUM aggregate with joined field
     * Sums maxStudentCount grouped by course activity status
     */
    @Test
    public void testSumAggregate() {
        System.out.println("\n=== Test SUM Aggregate ===");
        DynamicQuery dynamicQuery = new DynamicQuery();
        dynamicQuery.getSelect().add(Pair.of("active", "isActive"));
        dynamicQuery.getSelect().add(Pair.of("[Sum]maxStudentCount", "totalCapacity"));
        dynamicQuery.setGroupBy(List.of("active"));
        dynamicQuery.setOrderBy(List.of(Pair.of("active", Order.ASC)));
        
        PresentationUtil.prettyPrint(dynamicQuery);
        List<Tuple> results = courseRepository.findAllAsTuple(dynamicQuery);
        
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        for (Tuple tuple : results) {
            Boolean active = (Boolean) tuple.get(0);
            Number sum = (Number) tuple.get(1);
            System.out.println("Active: " + active + ", Total Capacity: " + sum);
            if (sum != null) {
                assertTrue(sum.longValue() > 0);
            }
        }
    }

    /**
     * Test AVG aggregate
     * Calculates average maxStudentCount per course active status
     */
    @Test
    public void testAvgAggregate() {
        System.out.println("\n=== Test AVG Aggregate ===");
        DynamicQuery dynamicQuery = new DynamicQuery();
        dynamicQuery.getSelect().add(Pair.of("active", "isActive"));
        dynamicQuery.getSelect().add(Pair.of("[Avg]maxStudentCount", "avgCapacity"));
        dynamicQuery.setGroupBy(List.of("active"));
        dynamicQuery.setOrderBy(List.of(Pair.of("active", Order.ASC)));
        
        PresentationUtil.prettyPrint(dynamicQuery);
        List<Tuple> results = courseRepository.findAllAsTuple(dynamicQuery);
        
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        for (Tuple tuple : results) {
            Boolean active = (Boolean) tuple.get(0);
            Number avg = (Number) tuple.get(1);
            System.out.println("Active: " + active + ", Avg Capacity: " + avg);
            if (avg != null) {
                assertTrue(avg.doubleValue() > 0);
            }
        }
    }

    /**
     * Test MAX aggregate
     * Finds maximum maxStudentCount grouped by active status
     */
    @Test
    public void testMaxAggregate() {
        System.out.println("\n=== Test MAX Aggregate ===");
        DynamicQuery dynamicQuery = new DynamicQuery();
        dynamicQuery.getSelect().add(Pair.of("active", "isActive"));
        dynamicQuery.getSelect().add(Pair.of("[Max]maxStudentCount", "maxCapacity"));
        dynamicQuery.setGroupBy(List.of("active"));
        dynamicQuery.setOrderBy(List.of(Pair.of("active", Order.ASC)));
        
        PresentationUtil.prettyPrint(dynamicQuery);
        List<Tuple> results = courseRepository.findAllAsTuple(dynamicQuery);
        
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        for (Tuple tuple : results) {
            Boolean active = (Boolean) tuple.get(0);
            Number max = (Number) tuple.get(1);
            System.out.println("Active: " + active + ", Max Capacity: " + max);
            if (max != null) {
                assertTrue(max.intValue() > 0);
            }
        }
    }

    /**
     * Test MIN aggregate
     * Finds minimum maxStudentCount grouped by active status
     */
    @Test
    public void testMinAggregate() {
        System.out.println("\n=== Test MIN Aggregate ===");
        DynamicQuery dynamicQuery = new DynamicQuery();
        dynamicQuery.getSelect().add(Pair.of("active", "isActive"));
        dynamicQuery.getSelect().add(Pair.of("[Min]maxStudentCount", "minCapacity"));
        dynamicQuery.setGroupBy(List.of("active"));
        dynamicQuery.setOrderBy(List.of(Pair.of("active", Order.ASC)));
        
        PresentationUtil.prettyPrint(dynamicQuery);
        List<Tuple> results = courseRepository.findAllAsTuple(dynamicQuery);
        
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        for (Tuple tuple : results) {
            Boolean active = (Boolean) tuple.get(0);
            Number min = (Number) tuple.get(1);
            System.out.println("Active: " + active + ", Min Capacity: " + min);
            if (min != null) {
                assertTrue(min.intValue() > 0);
            }
        }
    }

    /**
     * Test HAVING clause with COUNT aggregate using custom class projection
     * Groups by department and filters groups having more than 0 students
     */
    @Test
    public void testHavingWithCount() {
        System.out.println("\n=== Test HAVING with COUNT and Custom Class ===");
        DynamicQuery dynamicQuery = new DynamicQuery();
        dynamicQuery.getSelect().add(Pair.of("name", "departmentName"));
        dynamicQuery.getSelect().add(Pair.of("[Count]students.id", "studentCount"));
        dynamicQuery.setGroupBy(List.of("name"));
        dynamicQuery.setHaving(CriteriaList.of(
            Criteria.of("[Count]students.id", CriteriaOperator.GREATER_THAN, 0L)
        ));
        dynamicQuery.setOrderBy(List.of(Pair.of("name", Order.ASC)));
        
        PresentationUtil.prettyPrint(dynamicQuery);
        List<DepartmentStudentCount> results = departmentRepository.findAll(dynamicQuery, DepartmentStudentCount.class);
        PresentationUtil.prettyPrint(results);
        
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        for (DepartmentStudentCount deptCount : results) {
            System.out.println("Department: " + deptCount.getDepartmentName() + ", Student Count: " + deptCount.getStudentCount());
            assertTrue(deptCount.getStudentCount() > 0);
        }
    }

    /**
     * Test HAVING clause with SUM aggregate
     * Filters courses where total capacity is greater than a threshold
     */
    @Test
    public void testHavingWithSum() {
        System.out.println("\n=== Test HAVING with SUM ===");
        DynamicQuery dynamicQuery = new DynamicQuery();
        dynamicQuery.getSelect().add(Pair.of("active", "isActive"));
        dynamicQuery.getSelect().add(Pair.of("[Sum]maxStudentCount", "totalCapacity"));
        dynamicQuery.setGroupBy(List.of("active"));
        dynamicQuery.setHaving(CriteriaList.of(
            Criteria.of("[Sum]maxStudentCount", CriteriaOperator.GREATER_THAN, 100)
        ));
        
        PresentationUtil.prettyPrint(dynamicQuery);
        List<Tuple> results = courseRepository.findAllAsTuple(dynamicQuery);
        
        assertNotNull(results);
        
        for (Tuple tuple : results) {
            Boolean active = (Boolean) tuple.get(0);
            Number sum = (Number) tuple.get(1);
            System.out.println("Active: " + active + ", Total Capacity: " + sum);
            assertTrue(sum.longValue() > 100);
        }
    }

    /**
     * Test HAVING clause with AVG aggregate
     * Filters groups where average is greater than threshold
     */
    @Test
    public void testHavingWithAvg() {
        System.out.println("\n=== Test HAVING with AVG ===");
        DynamicQuery dynamicQuery = new DynamicQuery();
        dynamicQuery.getSelect().add(Pair.of("active", "isActive"));
        dynamicQuery.getSelect().add(Pair.of("[Avg]maxStudentCount", "avgCapacity"));
        dynamicQuery.setGroupBy(List.of("active"));
        dynamicQuery.setHaving(CriteriaList.of(
            Criteria.of("[Avg]maxStudentCount", CriteriaOperator.GREATER_THAN, 50.0)
        ));
        
        PresentationUtil.prettyPrint(dynamicQuery);
        List<Tuple> results = courseRepository.findAllAsTuple(dynamicQuery);
        
        assertNotNull(results);
        
        for (Tuple tuple : results) {
            Boolean active = (Boolean) tuple.get(0);
            Number avg = (Number) tuple.get(1);
            System.out.println("Active: " + active + ", Avg Capacity: " + avg);
            assertTrue(avg.doubleValue() > 50.0);
        }
    }

    /**
     * Test multiple aggregates in single query with custom class projection
     * Combines COUNT, SUM, AVG, MIN, MAX in one query
     */
    @Test
    public void testMultipleAggregates() {
        System.out.println("\n=== Test Multiple Aggregates with Custom Class ===");
        DynamicQuery dynamicQuery = new DynamicQuery();
        dynamicQuery.getSelect().add(Pair.of("active", "isActive"));
        dynamicQuery.getSelect().add(Pair.of("[Count]id", "courseCount"));
        dynamicQuery.getSelect().add(Pair.of("[Sum]maxStudentCount", "totalCapacity"));
        dynamicQuery.getSelect().add(Pair.of("[Avg]maxStudentCount", "avgCapacity"));
        dynamicQuery.getSelect().add(Pair.of("[Min]maxStudentCount", "minCapacity"));
        dynamicQuery.getSelect().add(Pair.of("[Max]maxStudentCount", "maxCapacity"));
        dynamicQuery.setGroupBy(List.of("active"));
        dynamicQuery.setOrderBy(List.of(Pair.of("active", Order.ASC)));
        
        PresentationUtil.prettyPrint(dynamicQuery);
        List<CourseStats> results = courseRepository.findAll(dynamicQuery, CourseStats.class);
        PresentationUtil.prettyPrint(results);
        
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        for (CourseStats stats : results) {
            System.out.println("Active: " + stats.getIsActive());
            System.out.println("  Count: " + stats.getCourseCount());
            System.out.println("  Sum: " + stats.getTotalCapacity());
            System.out.println("  Avg: " + stats.getAvgCapacity());
            System.out.println("  Min: " + stats.getMinCapacity());
            System.out.println("  Max: " + stats.getMaxCapacity());
            
            assertNotNull(stats.getCourseCount());
            assertTrue(stats.getCourseCount() > 0);
        }
    }

    /**
     * Test aggregate with joined field in GROUP BY
     * Groups students by department name and counts students
     */
    @Test
    public void testAggregateWithJoinedField() {
        System.out.println("\n=== Test Aggregate with Joined Field ===");
        DynamicQuery dynamicQuery = new DynamicQuery();
        dynamicQuery.getSelect().add(Pair.of("department.name", "departmentName"));
        dynamicQuery.getSelect().add(Pair.of("[Count]id", "studentCount"));
        dynamicQuery.setGroupBy(List.of("department.name"));
        dynamicQuery.setWhere(CriteriaList.of(
            Criteria.of("department.name", CriteriaOperator.SPECIFIED, true)
        ));
        dynamicQuery.setOrderBy(List.of(Pair.of("department.name", Order.ASC)));
        
        PresentationUtil.prettyPrint(dynamicQuery);
        List<Tuple> results = studentRepository.findAllAsTuple(dynamicQuery);

        assertNotNull(results);
        assertFalse(results.isEmpty());

        for (Tuple tuple : results) {
            String deptName = (String) tuple.get(0);
            Long count = (Long) tuple.get(1);
            System.out.println("Department: " + deptName + ", Student Count: " + count);
            assertNotNull(deptName);
            assertEquals(1L, count);
        }
    }

    /**
     * Test COUNT aggregate on joined collection
     * Counts courses per student
     */
    @Test
    public void testCountAggregateOnJoinedCollection() {
        System.out.println("\n=== Test COUNT on Joined Collection ===");
        DynamicQuery dynamicQuery = new DynamicQuery();
        dynamicQuery.getSelect().add(Pair.of("name", "studentName"));
        dynamicQuery.getSelect().add(Pair.of("[Count]courses.id", "courseCount"));
        dynamicQuery.setGroupBy(List.of("name"));
        dynamicQuery.setOrderBy(List.of(Pair.of("name", Order.ASC)));
        
        PresentationUtil.prettyPrint(dynamicQuery);
        List<Tuple> results = studentRepository.findAllAsTuple(dynamicQuery);
        
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        for (Tuple tuple : results) {
            String studentName = (String) tuple.get(0);
            Long courseCount = (Long) tuple.get(1);
            System.out.println("Student: " + studentName + ", Course Count: " + courseCount);
            assertNotNull(studentName);
            assertTrue(courseCount >= 0);
        }
    }

    /**
     * Test complex scenario: GROUP BY with WHERE, HAVING, and ORDER BY
     * Finds departments with specific criteria
     */
    @Test
    public void testComplexAggregateQuery() {
        System.out.println("\n=== Test Complex Aggregate Query ===");
        DynamicQuery dynamicQuery = new DynamicQuery();
        dynamicQuery.getSelect().add(Pair.of("name", "departmentName"));
        dynamicQuery.getSelect().add(Pair.of("[Count]students.id", "studentCount"));
        dynamicQuery.setGroupBy(List.of("name"));
        dynamicQuery.setWhere(CriteriaList.of(
            Criteria.of("id", CriteriaOperator.LESS_THAN_OR_EQUAL, 10)
        ));
        dynamicQuery.setHaving(CriteriaList.of(
            Criteria.of("[Count]students.id", CriteriaOperator.GREATER_THAN_OR_EQUAL, 0L)
        ));
        dynamicQuery.setOrderBy(List.of(Pair.of("name", Order.ASC)));
        
        PresentationUtil.prettyPrint(dynamicQuery);
        List<Tuple> results = departmentRepository.findAllAsTuple(dynamicQuery);
        
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        for (Tuple tuple : results) {
            String deptName = (String) tuple.get(0);
            Long count = (Long) tuple.get(1);
            System.out.println("Department: " + deptName + ", Student Count: " + count);
            assertNotNull(deptName);
            assertTrue(count >= 0);
        }
    }

    /**
     * Test aggregate with nested joined field using record projection
     * Counts students grouped by address state
     */
    @Test
    public void testAggregateWithNestedJoinedField() {
        System.out.println("\n=== Test Aggregate with Nested Joined Field and Record ===");
        DynamicQuery dynamicQuery = new DynamicQuery();
        dynamicQuery.getSelect().add(Pair.of("address.state", "state"));
        dynamicQuery.getSelect().add(Pair.of("[Count]id", "studentCount"));
        dynamicQuery.setGroupBy(List.of("address.state"));
        dynamicQuery.setWhere(CriteriaList.of(
            Criteria.of("address.state", CriteriaOperator.SPECIFIED, true)
        ));
        dynamicQuery.setOrderBy(List.of(Pair.of("address.state", Order.ASC)));
        
        PresentationUtil.prettyPrint(dynamicQuery);
        List<StateCountRecord> results = studentRepository.findAll(dynamicQuery, StateCountRecord.class);
        PresentationUtil.prettyPrint(results);
        
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        for (StateCountRecord stateCount : results) {
            System.out.println("State: " + stateCount.state() + ", Student Count: " + stateCount.studentCount());
            assertNotNull(stateCount.state());
            assertTrue(stateCount.studentCount() > 0);
        }
    }

    /**
     * Test HAVING with multiple conditions
     * Combines multiple HAVING criteria
     */
    @Test
    public void testHavingWithMultipleConditions() {
        System.out.println("\n=== Test HAVING with Multiple Conditions ===");
        DynamicQuery dynamicQuery = new DynamicQuery();
        dynamicQuery.getSelect().add(Pair.of("active", "isActive"));
        dynamicQuery.getSelect().add(Pair.of("[Count]id", "courseCount"));
        dynamicQuery.getSelect().add(Pair.of("[Avg]maxStudentCount", "avgCapacity"));
        dynamicQuery.setGroupBy(List.of("active"));
        dynamicQuery.setHaving(CriteriaList.of(
            Criteria.of("[Count]id", CriteriaOperator.GREATER_THAN, 0L),
            Criteria.of("[Avg]maxStudentCount", CriteriaOperator.GREATER_THAN, 0.0)
        ));
        
        PresentationUtil.prettyPrint(dynamicQuery);
        List<Tuple> results = courseRepository.findAllAsTuple(dynamicQuery);
        
        assertNotNull(results);
        
        for (Tuple tuple : results) {
            Boolean active = (Boolean) tuple.get(0);
            Long count = (Long) tuple.get(1);
            Number avg = (Number) tuple.get(2);
            System.out.println("Active: " + active + ", Count: " + count + ", Avg: " + avg);
            assertTrue(count > 0);
            assertTrue(avg.doubleValue() > 0.0);
        }
    }

    /**
     * Custom classes and records for projection testing
     */
    public static class CityCount {
        private String city;
        private Long studentCount;

        public CityCount() {
        }

        public CityCount(String city, Long studentCount) {
            this.city = city;
            this.studentCount = studentCount;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public Long getStudentCount() {
            return studentCount;
        }

        public void setStudentCount(Long studentCount) {
            this.studentCount = studentCount;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CityCount that)) return false;
            return Objects.equals(city, that.city) && Objects.equals(studentCount, that.studentCount);
        }

        @Override
        public int hashCode() {
            return Objects.hash(city, studentCount);
        }

        @Override
        public String toString() {
            return "CityCount{city='" + city + "', studentCount=" + studentCount + '}';
        }
    }

    public record StateCountRecord(String state, Long studentCount) {
    }

    public static class CourseStats {
        private Boolean isActive;
        private Long courseCount;
        private Long totalCapacity;
        private Double avgCapacity;
        private Integer minCapacity;
        private Integer maxCapacity;

        public CourseStats() {
        }

        public Boolean getIsActive() {
            return isActive;
        }

        public void setIsActive(Boolean isActive) {
            this.isActive = isActive;
        }

        public Long getCourseCount() {
            return courseCount;
        }

        public void setCourseCount(Long courseCount) {
            this.courseCount = courseCount;
        }

        public Long getTotalCapacity() {
            return totalCapacity;
        }

        public void setTotalCapacity(Long totalCapacity) {
            this.totalCapacity = totalCapacity;
        }

        public Double getAvgCapacity() {
            return avgCapacity;
        }

        public void setAvgCapacity(Double avgCapacity) {
            this.avgCapacity = avgCapacity;
        }

        public Integer getMinCapacity() {
            return minCapacity;
        }

        public void setMinCapacity(Integer minCapacity) {
            this.minCapacity = minCapacity;
        }

        public Integer getMaxCapacity() {
            return maxCapacity;
        }

        public void setMaxCapacity(Integer maxCapacity) {
            this.maxCapacity = maxCapacity;
        }

        @Override
        public String toString() {
            return "CourseStats{" +
                    "isActive=" + isActive +
                    ", courseCount=" + courseCount +
                    ", totalCapacity=" + totalCapacity +
                    ", avgCapacity=" + avgCapacity +
                    ", minCapacity=" + minCapacity +
                    ", maxCapacity=" + maxCapacity +
                    '}';
        }
    }

    public static class DepartmentStudentCount {
        private String departmentName;
        private Long studentCount;

        public DepartmentStudentCount() {
        }

        public String getDepartmentName() {
            return departmentName;
        }

        public void setDepartmentName(String departmentName) {
            this.departmentName = departmentName;
        }

        public Long getStudentCount() {
            return studentCount;
        }

        public void setStudentCount(Long studentCount) {
            this.studentCount = studentCount;
        }

        @Override
        public String toString() {
            return "DepartmentStudentCount{departmentName='" + departmentName + "', studentCount=" + studentCount + '}';
        }
    }

    public static class AggregateResult {
        private String category;
        private Long count;

        public AggregateResult() {
        }

        public AggregateResult(String category, Long count) {
            this.category = category;
            this.count = count;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AggregateResult that)) return false;
            return Objects.equals(category, that.category) && Objects.equals(count, that.count);
        }

        @Override
        public int hashCode() {
            return Objects.hash(category, count);
        }

        @Override
        public String toString() {
            return "AggregateResult{category='" + category + "', count=" + count + '}';
        }
    }

    /**
     * Test mapping aggregate results to custom object
     */
    @Test
    public void testAggregateResultMapping() {
        System.out.println("\n=== Test Aggregate Result Mapping ===");
        DynamicQuery dynamicQuery = new DynamicQuery();
        dynamicQuery.getSelect().add(Pair.of("address.state", "category"));
        dynamicQuery.getSelect().add(Pair.of("[Count]id", "count"));
        dynamicQuery.setGroupBy(List.of("address.state"));
        dynamicQuery.setWhere(CriteriaList.of(
            Criteria.of("address.state", CriteriaOperator.SPECIFIED, true)
        ));
        dynamicQuery.setOrderBy(List.of(Pair.of("address.state", Order.ASC)));
        
        PresentationUtil.prettyPrint(dynamicQuery);
        List<AggregateResult> results = studentRepository.findAll(dynamicQuery, AggregateResult.class);
        PresentationUtil.prettyPrint(results);
        
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        for (AggregateResult result : results) {
            System.out.println(result);
            assertNotNull(result.getCategory());
            assertNotNull(result.getCount());
            assertTrue(result.getCount() > 0);
        }
    }

    /**
     * Test COUNT aggregate with Tuple return type
     * Demonstrates using Tuple for dynamic result handling
     */
    @Test
    public void testCountAggregateWithTuple() {
        System.out.println("\n=== Test COUNT Aggregate with Tuple ===");
        DynamicQuery dynamicQuery = new DynamicQuery();
        dynamicQuery.getSelect().add(Pair.of("address.city", "city"));
        dynamicQuery.getSelect().add(Pair.of("[Count]id", "studentCount"));
        dynamicQuery.setGroupBy(List.of("address.city"));
        dynamicQuery.setOrderBy(List.of(Pair.of("address.city", Order.ASC)));
        dynamicQuery.setWhere(CriteriaList.of(Criteria.of("address.city", CriteriaOperator.SPECIFIED, true)));
        
        PresentationUtil.prettyPrint(dynamicQuery);
        List<Tuple> results = studentRepository.findAllAsTuple(dynamicQuery);
        
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        for (Tuple tuple : results) {
            String city = tuple.get("city", String.class);
            Long count = tuple.get("studentCount", Long.class);
            System.out.println("City: " + city + ", Count: " + count);
            assertNotNull(city);
            assertEquals(1L, count);
        }
    }

    /**
     * Test SUM aggregate with different projection types
     * Shows same query returning different result types
     */
    @Test
    public void testSumAggregateWithDifferentProjections() {
        System.out.println("\n=== Test SUM Aggregate with Different Projections ===");
        DynamicQuery dynamicQuery = new DynamicQuery();
        dynamicQuery.getSelect().add(Pair.of("name", "category"));
        dynamicQuery.getSelect().add(Pair.of("[Sum]maxStudentCount", "count"));
        dynamicQuery.setGroupBy(List.of("name"));
        dynamicQuery.setOrderBy(List.of(Pair.of("name", Order.ASC)));
        dynamicQuery.setWhere(CriteriaList.of(Criteria.of("id", CriteriaOperator.LESS_THAN_OR_EQUAL, 3)));
        
        // Test with Tuple
        System.out.println("--- Using Tuple ---");
        PresentationUtil.prettyPrint(dynamicQuery);
        List<Tuple> tupleResults = courseRepository.findAllAsTuple(dynamicQuery);

        assertNotNull(tupleResults);
        assertFalse(tupleResults.isEmpty());
        
        // Test with custom class
        System.out.println("--- Using Custom Class ---");
        List<AggregateResult> classResults = courseRepository.findAll(dynamicQuery, AggregateResult.class);
        PresentationUtil.prettyPrint(classResults);
        assertNotNull(classResults);
        assertFalse(classResults.isEmpty());
        
        // Verify both return same number of results
        assertEquals(tupleResults.size(), classResults.size());
        
        // Verify aggregated counts match for each result
        for (int i = 0; i < tupleResults.size(); i++) {
            String tupleName = tupleResults.get(i).get("category", String.class);
            Long tupleCount = tupleResults.get(i).get("count", Long.class);
            String className = classResults.get(i).getCategory();
            Long classCount = classResults.get(i).getCount();
            
            System.out.println("Tuple: " + tupleName + " -> " + tupleCount + ", Class: " + className + " -> " + classCount);
            assertEquals(tupleName, className);
            assertEquals(tupleCount, classCount);
        }
    }

    /**
     * Test AVG aggregate with record projection
     */
    @Test
    public void testAvgAggregateWithRecord() {
        System.out.println("\n=== Test AVG Aggregate with Record ===");
        DynamicQuery dynamicQuery = new DynamicQuery();
        dynamicQuery.getSelect().add(Pair.of("address.state", "state"));
        dynamicQuery.getSelect().add(Pair.of("[Count]id", "studentCount"));
        dynamicQuery.setGroupBy(List.of("address.state"));
        dynamicQuery.setWhere(CriteriaList.of(
            Criteria.of("address.state", CriteriaOperator.SPECIFIED, true)
        ));
        dynamicQuery.setOrderBy(List.of(Pair.of("address.state", Order.ASC)));
        
        PresentationUtil.prettyPrint(dynamicQuery);
        List<StateCountRecord> results = studentRepository.findAll(dynamicQuery, StateCountRecord.class);
        PresentationUtil.prettyPrint(results);
        
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        for (StateCountRecord record : results) {
            System.out.println("Record: " + record);
            assertNotNull(record.state());
            assertTrue(record.studentCount() > 0);
        }
    }

    /**
     * Test MAX and MIN aggregates with Tuple
     */
    @Test
    public void testMaxMinAggregatesWithTuple() {
        System.out.println("\n=== Test MAX and MIN Aggregates with Tuple ===");
        DynamicQuery dynamicQuery = new DynamicQuery();
        dynamicQuery.getSelect().add(Pair.of("active", "isActive"));
        dynamicQuery.getSelect().add(Pair.of("[Min]maxStudentCount", "minCapacity"));
        dynamicQuery.getSelect().add(Pair.of("[Max]maxStudentCount", "maxCapacity"));
        dynamicQuery.setGroupBy(List.of("active"));
        dynamicQuery.setOrderBy(List.of(Pair.of("active", Order.ASC)));
        
        PresentationUtil.prettyPrint(dynamicQuery);
        List<Tuple> results = courseRepository.findAllAsTuple(dynamicQuery);
        
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        for (Tuple tuple : results) {
            Boolean active = tuple.get("isActive", Boolean.class);
            Integer min = tuple.get("minCapacity", Integer.class);
            Integer max = tuple.get("maxCapacity", Integer.class);
            
            System.out.println("Active: " + active + ", Min: " + min + ", Max: " + max);
            assertNotNull(min);
            assertNotNull(max);
            assertTrue(min <= max);
        }
    }

    // ==================== JdqModel Tests ====================

    /**
     * JdqModel for COUNT aggregate with joined field
     */
    @JdqModel
    public static class StudentCityCountModel {
        @JdqField("address.city")
        private String city;

        @JdqField("[Count]id")
        private Long studentCount;

        public StudentCityCountModel() {
        }

        public StudentCityCountModel(String city, Long studentCount) {
            this.city = city;
            this.studentCount = studentCount;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public Long getStudentCount() {
            return studentCount;
        }

        public void setStudentCount(Long studentCount) {
            this.studentCount = studentCount;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof StudentCityCountModel)) return false;
            StudentCityCountModel that = (StudentCityCountModel) o;
            return Objects.equals(city, that.city) && Objects.equals(studentCount, that.studentCount);
        }

        @Override
        public int hashCode() {
            return Objects.hash(city, studentCount);
        }

        @Override
        public String toString() {
            return "StudentCityCountModel{city='" + city + "', studentCount=" + studentCount + '}';
        }
    }

    /**
     * JdqModel Record for COUNT aggregate
     */
    @JdqModel
    public record StudentStateCountModelRecord(
            @JdqField("address.state") String state,
            @JdqField("[Count]id") Long studentCount
    ) {
    }

    /**
     * JdqModel for multiple aggregates
     */
    @JdqModel
    public static class CourseAggregateModel {
        @JdqField("active")
        private Boolean isActive;

        @JdqField("[Count]id")
        private Long courseCount;

        @JdqField("[Sum]maxStudentCount")
        private Long totalCapacity;

        @JdqField("[Avg]maxStudentCount")
        private Double avgCapacity;

        @JdqField("[Min]maxStudentCount")
        private Integer minCapacity;

        @JdqField("[Max]maxStudentCount")
        private Integer maxCapacity;

        public CourseAggregateModel() {
        }

        public Boolean getIsActive() {
            return isActive;
        }

        public void setIsActive(Boolean isActive) {
            this.isActive = isActive;
        }

        public Long getCourseCount() {
            return courseCount;
        }

        public void setCourseCount(Long courseCount) {
            this.courseCount = courseCount;
        }

        public Long getTotalCapacity() {
            return totalCapacity;
        }

        public void setTotalCapacity(Long totalCapacity) {
            this.totalCapacity = totalCapacity;
        }

        public Double getAvgCapacity() {
            return avgCapacity;
        }

        public void setAvgCapacity(Double avgCapacity) {
            this.avgCapacity = avgCapacity;
        }

        public Integer getMinCapacity() {
            return minCapacity;
        }

        public void setMinCapacity(Integer minCapacity) {
            this.minCapacity = minCapacity;
        }

        public Integer getMaxCapacity() {
            return maxCapacity;
        }

        public void setMaxCapacity(Integer maxCapacity) {
            this.maxCapacity = maxCapacity;
        }

        @Override
        public String toString() {
            return "CourseAggregateModel{" +
                    "isActive=" + isActive +
                    ", courseCount=" + courseCount +
                    ", totalCapacity=" + totalCapacity +
                    ", avgCapacity=" + avgCapacity +
                    ", minCapacity=" + minCapacity +
                    ", maxCapacity=" + maxCapacity +
                    '}';
        }
    }

    /**
     * JdqModel for department with student count (joined collection aggregate)
     */
    @JdqModel
    public static class DepartmentAggregateModel {
        @JdqField("name")
        private String departmentName;

        @JdqField("[Count]students.id")
        private Long studentCount;

        public DepartmentAggregateModel() {
        }

        public String getDepartmentName() {
            return departmentName;
        }

        public void setDepartmentName(String departmentName) {
            this.departmentName = departmentName;
        }

        public Long getStudentCount() {
            return studentCount;
        }

        public void setStudentCount(Long studentCount) {
            this.studentCount = studentCount;
        }

        @Override
        public String toString() {
            return "DepartmentAggregateModel{departmentName='" + departmentName + "', studentCount=" + studentCount + '}';
        }
    }

    /**
     * JdqModel Record for COUNT DISTINCT
     */
    @JdqModel
    public record DistinctStateCountModelRecord(
            @JdqField("[CountDistinct]address.state") Long distinctStates
    ) {
    }

    /**
     * Test COUNT aggregate using JdqModel class
     */
    @Test
    public void testCountAggregateWithJdqModel() {
        System.out.println("\n=== Test COUNT Aggregate with JdqModel ===");
        DynamicQuery dynamicQuery = new DynamicQuery();
        dynamicQuery.setGroupBy(List.of("address.city"));
        dynamicQuery.setWhere(CriteriaList.of(Criteria.of("address.city", CriteriaOperator.SPECIFIED, true)));
        dynamicQuery.setOrderBy(List.of(Pair.of("address.city", Order.ASC)));

        PresentationUtil.prettyPrint(dynamicQuery);
        List<StudentCityCountModel> results = studentRepository.findAll(dynamicQuery, StudentCityCountModel.class);
        PresentationUtil.prettyPrint(results);

        assertNotNull(results);
        assertFalse(results.isEmpty());

        // Verify each city has exactly 1 student
        for (StudentCityCountModel model : results) {
            System.out.println(model);
            assertNotNull(model.getCity());
            assertEquals(1L, model.getStudentCount());
        }
    }

    /**
     * Test COUNT aggregate using JdqModel record
     */
    @Test
    public void testCountAggregateWithJdqModelRecord() {
        System.out.println("\n=== Test COUNT Aggregate with JdqModel Record ===");
        DynamicQuery dynamicQuery = new DynamicQuery();
        dynamicQuery.setGroupBy(List.of("address.state"));
        dynamicQuery.setWhere(CriteriaList.of(Criteria.of("address.state", CriteriaOperator.SPECIFIED, true)));
        dynamicQuery.setOrderBy(List.of(Pair.of("address.state", Order.ASC)));

        PresentationUtil.prettyPrint(dynamicQuery);
        List<StudentStateCountModelRecord> results = studentRepository.findAll(dynamicQuery, StudentStateCountModelRecord.class);
        PresentationUtil.prettyPrint(results);

        assertNotNull(results);
        assertFalse(results.isEmpty());

        for (StudentStateCountModelRecord record : results) {
            System.out.println("State: " + record.state() + ", Count: " + record.studentCount());
            assertNotNull(record.state());
            assertTrue(record.studentCount() > 0);
        }
    }

    /**
     * Test multiple aggregates using JdqModel
     */
    @Test
    public void testMultipleAggregatesWithJdqModel() {
        System.out.println("\n=== Test Multiple Aggregates with JdqModel ===");
        DynamicQuery dynamicQuery = new DynamicQuery();
        dynamicQuery.setGroupBy(List.of("active"));
        dynamicQuery.setOrderBy(List.of(Pair.of("active", Order.ASC)));

        PresentationUtil.prettyPrint(dynamicQuery);
        List<CourseAggregateModel> results = courseRepository.findAll(dynamicQuery, CourseAggregateModel.class);
        PresentationUtil.prettyPrint(results);

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(3, results.size()); // null, false, true

        for (CourseAggregateModel model : results) {
            System.out.println(model);
            assertNotNull(model.getCourseCount());
            assertTrue(model.getCourseCount() > 0);
            assertNotNull(model.getTotalCapacity());
            assertNotNull(model.getAvgCapacity());
            assertNotNull(model.getMinCapacity());
            assertNotNull(model.getMaxCapacity());
            
            // Verify logical consistency
            assertTrue(model.getMinCapacity() <= model.getMaxCapacity());
        }
    }

    /**
     * Test COUNT on joined collection using JdqModel
     */
    @Test
    public void testJoinedCollectionCountWithJdqModel() {
        System.out.println("\n=== Test Joined Collection COUNT with JdqModel ===");
        DynamicQuery dynamicQuery = new DynamicQuery();
        dynamicQuery.setGroupBy(List.of("name"));
        dynamicQuery.setOrderBy(List.of(Pair.of("name", Order.ASC)));

        PresentationUtil.prettyPrint(dynamicQuery);
        List<DepartmentAggregateModel> results = departmentRepository.findAll(dynamicQuery, DepartmentAggregateModel.class);
        PresentationUtil.prettyPrint(results);

        assertNotNull(results);
        assertFalse(results.isEmpty());

        for (DepartmentAggregateModel model : results) {
            System.out.println(model);
            assertNotNull(model.getDepartmentName());
            assertNotNull(model.getStudentCount());
            assertTrue(model.getStudentCount() >= 0);
        }
    }

    /**
     * Test COUNT DISTINCT using JdqModel record
     */
    @Test
    public void testCountDistinctWithJdqModel() {
        System.out.println("\n=== Test COUNT DISTINCT with JdqModel ===");
        DynamicQuery dynamicQuery = new DynamicQuery();
        dynamicQuery.setWhere(CriteriaList.of(Criteria.of("address.state", CriteriaOperator.SPECIFIED, true)));

        PresentationUtil.prettyPrint(dynamicQuery);
        List<DistinctStateCountModelRecord> results = studentRepository.findAll(dynamicQuery, DistinctStateCountModelRecord.class);
        PresentationUtil.prettyPrint(results);

        assertNotNull(results);
        assertEquals(1, results.size());
        
        DistinctStateCountModelRecord record = results.get(0);
        System.out.println("Distinct States: " + record.distinctStates());
        assertTrue(record.distinctStates() > 0);
        assertEquals(8L, record.distinctStates()); // We have 8 distinct states in test data
    }

    /**
     * Test JdqModel with HAVING clause
     */
    @Test
    public void testJdqModelWithHaving() {
        System.out.println("\n=== Test JdqModel with HAVING Clause ===");
        DynamicQuery dynamicQuery = new DynamicQuery();
        dynamicQuery.setGroupBy(List.of("name"));
        dynamicQuery.setHaving(CriteriaList.of(
                Criteria.of("[Count]students.id", CriteriaOperator.GREATER_THAN, 0L)
        ));
        dynamicQuery.setOrderBy(List.of(Pair.of("name", Order.ASC)));

        PresentationUtil.prettyPrint(dynamicQuery);
        List<DepartmentAggregateModel> results = departmentRepository.findAll(dynamicQuery, DepartmentAggregateModel.class);
        PresentationUtil.prettyPrint(results);

        assertNotNull(results);
        assertFalse(results.isEmpty());

        // All results should have student count > 0 due to HAVING clause
        for (DepartmentAggregateModel model : results) {
            System.out.println(model);
            assertTrue(model.getStudentCount() > 0);
        }
    }

    /**
     * Test JdqModel with complex query (WHERE + GROUP BY + HAVING + ORDER BY)
     */
    @Test
    public void testJdqModelComplexAggregateQuery() {
        System.out.println("\n=== Test JdqModel Complex Aggregate Query ===");
        DynamicQuery dynamicQuery = new DynamicQuery();
        dynamicQuery.setWhere(CriteriaList.of(
                Criteria.of("address.state", CriteriaOperator.SPECIFIED, true)
        ));
        dynamicQuery.setGroupBy(List.of("address.city"));
        dynamicQuery.setHaving(CriteriaList.of(
                Criteria.of("[Count]id", CriteriaOperator.GREATER_THAN_OR_EQUAL, 1L)
        ));
        dynamicQuery.setOrderBy(List.of(Pair.of("address.city", Order.DESC)));

        PresentationUtil.prettyPrint(dynamicQuery);
        List<StudentCityCountModel> results = studentRepository.findAll(dynamicQuery, StudentCityCountModel.class);
        PresentationUtil.prettyPrint(results);

        assertNotNull(results);
        assertFalse(results.isEmpty());

        // Verify results are ordered descending
        String previousCity = null;
        for (StudentCityCountModel model : results) {
            System.out.println(model);
            assertNotNull(model.getCity());
            assertTrue(model.getStudentCount() >= 1);
            
            if (previousCity != null) {
                assertTrue(previousCity.compareTo(model.getCity()) >= 0, 
                        "Results should be ordered by city DESC");
            }
            previousCity = model.getCity();
        }
    }
}

