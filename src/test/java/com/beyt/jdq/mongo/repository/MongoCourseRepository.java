package com.beyt.jdq.mongo.repository;

import com.beyt.jdq.mongo.JpaDynamicQueryMongoRepository;
import com.beyt.jdq.mongo.entity.Course;
import org.springframework.stereotype.Repository;

/**
 * MongoDB repository for Course entity with dynamic query support.
 */
@Repository
public interface MongoCourseRepository extends JpaDynamicQueryMongoRepository<Course, Long> {
}

