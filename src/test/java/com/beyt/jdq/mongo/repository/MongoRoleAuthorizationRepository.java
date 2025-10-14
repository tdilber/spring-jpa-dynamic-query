package com.beyt.jdq.mongo.repository;

import com.beyt.jdq.mongo.JpaDynamicQueryMongoRepository;
import com.beyt.jdq.mongo.entity.RoleAuthorization;
import org.springframework.stereotype.Repository;

/**
 * MongoDB repository for RoleAuthorization entity.
 */
@Repository
public interface MongoRoleAuthorizationRepository extends JpaDynamicQueryMongoRepository<RoleAuthorization, Long> {
}


