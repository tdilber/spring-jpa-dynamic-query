package com.beyt.jdq.mongo;

import com.beyt.jdq.annotation.EnableJpaDynamicQueryMongo;
import com.beyt.jdq.dto.Criteria;
import com.beyt.jdq.dto.CriteriaList;
import com.beyt.jdq.dto.DynamicQuery;
import com.beyt.jdq.dto.enums.CriteriaOperator;
import com.beyt.jdq.dto.enums.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.util.Pair;

import java.util.List;

/**
 * Example usage of JPA Dynamic Query with MongoDB.
 * This is a demonstration file showing how to use the MongoDB implementation.
 */
public class EXAMPLE_USAGE {

    // ============================================
    // 1. CONFIGURATION
    // ============================================
    
    @Configuration
    @EnableJpaDynamicQueryMongo
    public static class MongoConfig {
        // Your MongoDB configuration
        // MongoTemplate bean should be configured automatically by Spring Boot
    }

    // ============================================
    // 2. ENTITY DEFINITION
    // ============================================
    
    @Document(collection = "users")
    public static class User {
        private String id;
        private String name;
        private String email;
        private Integer age;
        private Boolean active;
        private String status;
        
        // Getters and setters...
    }

    // ============================================
    // 3. REPOSITORY DEFINITION
    // ============================================
    
    public interface UserRepository extends JpaDynamicQueryMongoRepository<User, String> {
        // That's it! All dynamic query methods are now available
        // You can add custom methods here if needed
    }

    // ============================================
    // 4. SERVICE USAGE EXAMPLES
    // ============================================
    
    public static class UserService {
        
        @Autowired
        private UserRepository userRepository;

        // Example 1: Simple EQUAL query
        public List<User> findUsersByName(String name) {
            return userRepository.findAll(
                CriteriaList.of(
                    Criteria.of("name", CriteriaOperator.EQUAL, name)
                )
            );
        }

        // Example 2: CONTAIN query (case-insensitive)
        public List<User> searchUsersByEmail(String emailPart) {
            return userRepository.findAll(
                CriteriaList.of(
                    Criteria.of("email", CriteriaOperator.CONTAIN, emailPart)
                )
            );
        }

        // Example 3: Multiple criteria (AND)
        public List<User> findActiveUsersAboveAge(int minAge) {
            return userRepository.findAll(
                CriteriaList.of(
                    Criteria.of("active", CriteriaOperator.EQUAL, true),
                    Criteria.of("age", CriteriaOperator.GREATER_THAN, minAge)
                )
            );
        }

        // Example 4: OR operation
        public List<User> findUsersByNameOrEmail(String name, String email) {
            return userRepository.findAll(
                CriteriaList.of(
                    Criteria.of("name", CriteriaOperator.EQUAL, name),
                    Criteria.OR(),
                    Criteria.of("email", CriteriaOperator.EQUAL, email)
                )
            );
        }

        // Example 5: Range query
        public List<User> findUsersByAgeRange(int minAge, int maxAge) {
            return userRepository.findAll(
                CriteriaList.of(
                    Criteria.of("age", CriteriaOperator.GREATER_THAN_OR_EQUAL, minAge),
                    Criteria.of("age", CriteriaOperator.LESS_THAN_OR_EQUAL, maxAge)
                )
            );
        }

        // Example 6: SPECIFIED operator (check null/not null)
        public List<User> findUsersWithEmail() {
            return userRepository.findAll(
                CriteriaList.of(
                    Criteria.of("email", CriteriaOperator.SPECIFIED, "true")
                )
            );
        }

        public List<User> findUsersWithoutEmail() {
            return userRepository.findAll(
                CriteriaList.of(
                    Criteria.of("email", CriteriaOperator.SPECIFIED, "false")
                )
            );
        }

        // Example 7: String operations
        public List<User> findUsersByEmailDomain(String domain) {
            return userRepository.findAll(
                CriteriaList.of(
                    Criteria.of("email", CriteriaOperator.END_WITH, "@" + domain)
                )
            );
        }

        public List<User> findAdminUsers() {
            return userRepository.findAll(
                CriteriaList.of(
                    Criteria.of("email", CriteriaOperator.START_WITH, "admin")
                )
            );
        }

        // Example 8: Pagination with DynamicQuery
        public Page<User> findActiveUsersPaginated(int pageNumber, int pageSize) {
            DynamicQuery query = new DynamicQuery();
            query.getWhere().add(Criteria.of("active", CriteriaOperator.EQUAL, true));
            query.setPageNumber(pageNumber);
            query.setPageSize(pageSize);
            
            return userRepository.findAllAsPage(query);
        }

        // Example 9: Sorting with pagination
        public Page<User> findUsersSortedByAge(int pageNumber, int pageSize) {
            DynamicQuery query = new DynamicQuery();
            query.getWhere().add(Criteria.of("active", CriteriaOperator.EQUAL, true));
            query.getOrderBy().add(Pair.of("age", Order.DESC));
            query.setPageNumber(pageNumber);
            query.setPageSize(pageSize);
            
            return userRepository.findAllAsPage(query);
        }

        // Example 10: Count operations
        public long countActiveUsers() {
            return userRepository.count(
                CriteriaList.of(
                    Criteria.of("active", CriteriaOperator.EQUAL, true)
                )
            );
        }

        // Example 11: Batch processing
        public void processAllUsersInBatches() {
            userRepository.consumePartially(users -> {
                // Process each batch of 100 users
                users.forEach(user -> {
                    System.out.println("Processing user: " + user.name);
                    // Your processing logic here
                });
            }, 100);
        }

        // Example 12: Batch processing with criteria
        public void processActiveUsersInBatches() {
            userRepository.consumePartially(
                CriteriaList.of(
                    Criteria.of("active", CriteriaOperator.EQUAL, true)
                ),
                users -> {
                    // Process each batch of active users
                    users.forEach(user -> {
                        System.out.println("Processing active user: " + user.name);
                    });
                },
                100
            );
        }

        // Example 13: Complex query with multiple conditions
        public List<User> complexSearch(String namePattern, Integer minAge, String status) {
            CriteriaList criteria = new CriteriaList();
            
            if (namePattern != null) {
                criteria.add(Criteria.of("name", CriteriaOperator.CONTAIN, namePattern));
            }
            
            if (minAge != null) {
                criteria.add(Criteria.of("age", CriteriaOperator.GREATER_THAN_OR_EQUAL, minAge));
            }
            
            if (status != null) {
                criteria.add(Criteria.of("status", CriteriaOperator.EQUAL, status));
            }
            
            return userRepository.findAll(criteria);
        }

        // Example 14: NOT_EQUAL operation
        public List<User> findUsersExceptStatus(String excludeStatus) {
            return userRepository.findAll(
                CriteriaList.of(
                    Criteria.of("status", CriteriaOperator.NOT_EQUAL, excludeStatus)
                )
            );
        }

        // Example 15: DOES_NOT_CONTAIN operation
        public List<User> findUsersWithoutTestEmail() {
            return userRepository.findAll(
                CriteriaList.of(
                    Criteria.of("email", CriteriaOperator.DOES_NOT_CONTAIN, "test")
                )
            );
        }

        // Example 16: Using Pageable directly
        public Page<User> findActiveUsersPage(int pageNumber, int pageSize) {
            return userRepository.findAll(
                CriteriaList.of(
                    Criteria.of("active", CriteriaOperator.EQUAL, true)
                ),
                PageRequest.of(pageNumber, pageSize)
            );
        }

        // Example 17: Multiple OR groups
        public List<User> complexOrQuery() {
            return userRepository.findAll(
                CriteriaList.of(
                    // First group: name = "John" OR age > 30
                    Criteria.of("name", CriteriaOperator.EQUAL, "John"),
                    Criteria.OR(),
                    Criteria.of("age", CriteriaOperator.GREATER_THAN, 30),
                    // AND
                    // Second condition: active = true
                    Criteria.of("active", CriteriaOperator.EQUAL, true)
                )
            );
        }
    }

    // ============================================
    // 5. TESTING EXAMPLE
    // ============================================
    
    public static class UserServiceTest {
        
        @Autowired
        private UserRepository userRepository;
        
        public void testFindUsers() {
            // Test simple query
            List<User> users = userRepository.findAll(
                CriteriaList.of(
                    Criteria.of("name", CriteriaOperator.EQUAL, "John Doe")
                )
            );
            
            System.out.println("Found " + users.size() + " users");
            
            // Test with pagination
            DynamicQuery query = new DynamicQuery();
            query.getWhere().add(Criteria.of("active", CriteriaOperator.EQUAL, true));
            query.setPageNumber(0);
            query.setPageSize(10);
            
            Page<User> page = userRepository.findAllAsPage(query);
            System.out.println("Page " + page.getNumber() + " of " + page.getTotalPages());
            System.out.println("Total elements: " + page.getTotalElements());
        }
    }
}

