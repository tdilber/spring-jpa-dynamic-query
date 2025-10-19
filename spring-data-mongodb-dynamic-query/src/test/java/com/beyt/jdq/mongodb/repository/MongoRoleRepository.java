package com.beyt.jdq.mongodb.repository;

import com.beyt.jdq.mongodb.entity.Role;
import org.springframework.stereotype.Repository;

/**
 * MongoDB repository for Role entity.
 */
@Repository
public interface MongoRoleRepository extends MongoDynamicQueryRepository<Role, Long> {
}


