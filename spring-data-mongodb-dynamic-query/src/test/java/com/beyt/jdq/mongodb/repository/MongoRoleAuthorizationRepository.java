package com.beyt.jdq.mongodb.repository;

import com.beyt.jdq.mongodb.entity.RoleAuthorization;
import org.springframework.stereotype.Repository;

/**
 * MongoDB repository for RoleAuthorization entity.
 */
@Repository
public interface MongoRoleAuthorizationRepository extends MongoDynamicQueryRepository<RoleAuthorization, Long> {
}


