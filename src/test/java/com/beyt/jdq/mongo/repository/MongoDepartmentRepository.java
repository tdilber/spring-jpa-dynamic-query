package com.beyt.jdq.mongo.repository;

import com.beyt.jdq.mongo.JpaDynamicQueryMongoRepository;
import com.beyt.jdq.mongo.entity.Department;
import org.springframework.stereotype.Repository;

/**
 * MongoDB repository for Department entity.
 * Extends JpaDynamicQueryMongoRepository for dynamic query support.
 * The findAll(CriteriaList) method is inherited from JpaDynamicQueryMongoRepository.
 */
@Repository
public interface MongoDepartmentRepository extends JpaDynamicQueryMongoRepository<Department, Long> {
    // No need to declare findAll(CriteriaList) - it's inherited from parent interface
}

