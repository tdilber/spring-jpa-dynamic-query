package com.beyt.jdq.mongodb.repository;

import com.beyt.jdq.mongodb.entity.Customer;
import org.springframework.stereotype.Repository;

/**
 * MongoDB repository for Customer entity with dynamic query support.
 */
@Repository
public interface MongoCustomerRepository extends MongoDynamicQueryRepository<Customer, Long> {
}

