package com.beyt.jdq.mongo.repository;

import com.beyt.jdq.mongo.JpaDynamicQueryMongoRepository;
import com.beyt.jdq.mongo.entity.Student;
import org.springframework.stereotype.Repository;

/**
 * MongoDB repository for Student entity.
 * Extends JpaDynamicQueryMongoRepository for dynamic query support.
 * The findAll(CriteriaList) method is inherited from JpaDynamicQueryMongoRepository.
 */
@Repository
public interface MongoStudentRepository extends JpaDynamicQueryMongoRepository<Student, Long> {
    // No need to declare findAll(CriteriaList) - it's inherited from parent interface
}

