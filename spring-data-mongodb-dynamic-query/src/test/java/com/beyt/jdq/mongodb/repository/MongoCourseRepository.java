package com.beyt.jdq.mongodb.repository;

import com.beyt.jdq.mongodb.entity.Course;
import org.springframework.stereotype.Repository;

/**
 * MongoDB repository for Course entity with dynamic query support.
 */
@Repository
public interface MongoCourseRepository extends MongoDynamicQueryRepository<Course, Long> {
}

