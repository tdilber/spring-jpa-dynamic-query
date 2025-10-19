package com.beyt.jdq.mongodb.repository;

import com.beyt.jdq.mongodb.entity.Student;
import org.springframework.stereotype.Repository;

/**
 * MongoDB repository for Student entity.
 * Extends JpaDynamicQueryMongoRepository for dynamic query support.
 * The findAll(CriteriaList) method is inherited from JpaDynamicQueryMongoRepository.
 */
@Repository
public interface MongoStudentRepository extends MongoDynamicQueryRepository<Student, Long> {
    // No need to declare findAll(CriteriaList) - it's inherited from parent interface
}

