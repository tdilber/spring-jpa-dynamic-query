package com.beyt.jdq.mongo.repository;

import com.beyt.jdq.mongo.JpaDynamicQueryMongoRepository;
import com.beyt.jdq.mongo.entity.AdminUser;
import org.springframework.stereotype.Repository;

/**
 * MongoDB repository for AdminUser entity.
 * Extends JpaDynamicQueryMongoRepository for dynamic query support.
 * The findAll(CriteriaList) method is inherited from JpaDynamicQueryMongoRepository.
 */
@Repository
public interface MongoAdminUserRepository extends JpaDynamicQueryMongoRepository<AdminUser, Long> {
    // No need to declare findAll(CriteriaList) - it's inherited from parent interface
}

