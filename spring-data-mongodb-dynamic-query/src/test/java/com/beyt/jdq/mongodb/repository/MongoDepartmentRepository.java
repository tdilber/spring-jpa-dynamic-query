package com.beyt.jdq.mongodb.repository;

import com.beyt.jdq.mongodb.entity.Department;
import org.springframework.stereotype.Repository;

/**
 * MongoDB repository for Department entity.
 * Extends JpaDynamicQueryMongoRepository for dynamic query support.
 * The findAll(CriteriaList) method is inherited from JpaDynamicQueryMongoRepository.
 */
@Repository
public interface MongoDepartmentRepository extends MongoDynamicQueryRepository<Department, Long> {
    // No need to declare findAll(CriteriaList) - it's inherited from parent interface
}

