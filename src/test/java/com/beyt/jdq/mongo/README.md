# MongoDB Tests for JPA Dynamic Query

This directory contains MongoDB tests for the JPA Dynamic Query library.

## Structure

```
mongo/
├── README.md                    # This file
├── MongoTestApplication.java    # Spring Boot test application
├── BaseMongoTestInstance.java  # Base test class with test data
├── config/
│   └── MongoTestConfig.java     # MongoDB test configuration
├── entity/
│   └── Course.java              # MongoDB Course entity
├── repository/
│   └── MongoCourseRepository.java # MongoDB repository with dynamic query support
└── [Test Files]
    ├── S1_Operators.java        # Basic operator tests
    ├── S2_Multi_Value_Operators.java # Multi-value operator tests
    └── S3_AND_OR_Operator.java  # AND/OR logic tests
```

## Running the Tests

### Prerequisites

Before running the tests, you need a MongoDB instance running. You have several options:

#### Option 1: Local MongoDB
```bash
# Start MongoDB locally
mongod --dbpath /path/to/data
```

#### Option 2: Docker
```bash
# Run MongoDB in Docker
docker run -d -p 27017:27017 --name mongodb mongo:latest
```

#### Option 3: Testcontainers (Recommended)
Add Testcontainers dependency to your `pom.xml`:
```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>mongodb</artifactId>
    <scope>test</scope>
</dependency>
```

Then MongoDB will automatically start in a container for tests.

### Running Tests

```bash
# Run all MongoDB tests
mvn test -Dtest="com.beyt.jdq.mongo.*"

# Run specific test class
mvn test -Dtest="com.beyt.jdq.mongo.S1_Operators"

# Run from IDE
# Just run the test classes directly
```

## Test Classes

### S1_Operators
Tests basic operators:
- `CONTAIN` - Case-insensitive string contains
- `DOES_NOT_CONTAIN` - String does not contain
- `START_WITH` - String starts with
- `END_WITH` - String ends with
- `SPECIFIED` - Field is not null (true) or is null (false)
- `EQUAL` - Exact match or IN query for multiple values
- `NOT_EQUAL` - Not equal or NOT IN for multiple values
- `GREATER_THAN` - Numeric/Date greater than
- `GREATER_THAN_OR_EQUAL` - Numeric/Date >=
- `LESS_THAN` - Numeric/Date less than
- `LESS_THAN_OR_EQUAL` - Numeric/Date <=

### S2_Multi_Value_Operators
Tests operators with multiple values:
- `EQUAL` with multiple values (IN query)
- `NOT_EQUAL` with multiple values (NOT IN query)
- Works with strings, integers, and dates

### S3_AND_OR_Operator
Tests logical combinations:
- **AND**: Multiple criteria combined with AND (default behavior)
- **OR**: Using `Criteria.OR()` to create OR groups
- Complex queries with multiple OR groups
- Mixed AND/OR logic

## Test Data

The tests use 10 pre-defined Course entities:

| ID | Name | Start Date | Max Students | Active | Description |
|----|------|------------|--------------|--------|-------------|
| 1 | Introduction to Computer Science | 2016-06-18 | 50 | true | ... |
| 2 | Calculus I | 2017-06-18 | 60 | true | ... |
| 3 | Calculus II | 2018-06-18 | 250 | null | ... |
| 4 | Physics I | 2019-06-18 | 250 | null | ... |
| 5 | Physics II | 2020-06-18 | 250 | null | ... |
| 6 | Chemistry I | 2021-06-18 | 40 | null | ... |
| 7 | Chemistry II | 2022-06-18 | 30 | null | ... |
| 8 | Biology I | 2015-06-18 | 20 | true | ... |
| 9 | Biology II | 2013-06-18 | 54 | true | ... |
| 10 | English Literature I | 2025-06-18 | 10 | false | ... |

## Configuration

### application-mongotest.yml
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/jdq_test
      database: jdq_test
```

You can override this by:
1. Setting environment variables
2. Creating application-local.yml
3. Using test-specific properties

## Notes

### Date Handling
MongoDB stores dates differently than JPA. The tests use `java.util.Date` instead of `java.sql.Timestamp`.

### Type Conversion
The MongoDB implementation automatically converts numeric strings to appropriate types (Integer, Long, Double).

### Case Sensitivity
All string operations (CONTAIN, START_WITH, END_WITH) are **case-insensitive** by default.

### Null Handling
Use `SPECIFIED` operator:
- `SPECIFIED "true"` - Field is not null
- `SPECIFIED "false"` - Field is null

## Troubleshooting

### MongoDB Connection Error
```
Error: Unable to connect to MongoDB
```
**Solution**: Ensure MongoDB is running on `localhost:27017` or update the connection string.

### Tests Fail with "Repository Not Found"
```
Error: No bean of type MongoCourseRepository found
```
**Solution**: Ensure `@EnableJpaDynamicQueryMongo` is present on `MongoTestApplication`.

### Date Comparison Issues
```
Error: Expected course but got none
```
**Solution**: MongoDB date comparison requires proper date format. Check date string format in tests.

## Comparison with JPA Tests

| Feature | JPA Tests | MongoDB Tests |
|---------|-----------|---------------|
| Entity | `@Entity` | `@Document` |
| ID Type | `Long` with `@GeneratedValue` | `Long` (manual) |
| Date Type | `java.sql.Timestamp` | `java.util.Date` |
| Repository | `JpaDynamicQueryRepository` | `JpaDynamicQueryMongoRepository` |
| Transactions | Yes | No (MongoDB doesn't require for single ops) |
| Flush | Required | Not needed |

## Adding More Tests

To add new tests:

1. Create a new test class extending `BaseMongoTestInstance`
2. Add `@SpringBootTest(classes = MongoTestApplication.class)`
3. Use `courseRepository.findAll(criteriaList)` for queries
4. Follow the naming convention: `S{number}_{TestName}.java`

Example:
```java
@SpringBootTest(classes = MongoTestApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class S4_CustomTest extends BaseMongoTestInstance {
    
    @Test
    public void myTest() {
        var criteriaList = CriteriaList.of(
            Criteria.of("name", CriteriaOperator.CONTAIN, "Physics")
        );
        List<Course> courseList = courseRepository.findAll(criteriaList);
        assertEquals(2, courseList.size());
    }
}
```

## CI/CD Integration

For CI/CD pipelines, use Testcontainers which will automatically:
1. Start MongoDB in a Docker container
2. Run tests
3. Clean up after tests

No manual MongoDB setup required!

