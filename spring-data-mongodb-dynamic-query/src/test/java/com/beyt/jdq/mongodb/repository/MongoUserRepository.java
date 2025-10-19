package com.beyt.jdq.mongodb.repository;

import com.beyt.jdq.mongodb.entity.User;
import org.springframework.stereotype.Repository;

/**
 * MongoDB repository for User entity with dynamic query support.
 */
@Repository
public interface MongoUserRepository extends MongoDynamicQueryRepository<User, Long> {
}

