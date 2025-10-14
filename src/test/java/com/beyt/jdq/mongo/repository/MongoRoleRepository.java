package com.beyt.jdq.mongo.repository;

import com.beyt.jdq.mongo.JpaDynamicQueryMongoRepository;
import com.beyt.jdq.mongo.entity.Role;
import org.springframework.stereotype.Repository;

/**
 * MongoDB repository for Role entity.
 */
@Repository
public interface MongoRoleRepository extends JpaDynamicQueryMongoRepository<Role, Long> {
}


