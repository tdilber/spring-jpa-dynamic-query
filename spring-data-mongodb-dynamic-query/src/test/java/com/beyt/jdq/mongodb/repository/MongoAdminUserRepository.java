package com.beyt.jdq.mongodb.repository;

import com.beyt.jdq.mongodb.entity.AdminUser;
import org.springframework.stereotype.Repository;

/**
 * MongoDB repository for AdminUser entity.
 * Extends JpaDynamicQueryMongoRepository for dynamic query support.
 * The findAll(CriteriaList) method is inherited from JpaDynamicQueryMongoRepository.
 */
@Repository
public interface MongoAdminUserRepository extends MongoDynamicQueryRepository<AdminUser, Long> {
    // No need to declare findAll(CriteriaList) - it's inherited from parent interface
}

