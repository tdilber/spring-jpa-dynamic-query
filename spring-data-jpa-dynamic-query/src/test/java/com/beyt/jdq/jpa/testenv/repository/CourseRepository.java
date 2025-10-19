package com.beyt.jdq.jpa.testenv.repository;

import com.beyt.jdq.jpa.repository.JpaDynamicQueryRepository;
import com.beyt.jdq.jpa.testenv.entity.school.Course;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaDynamicQueryRepository<Course, Long> {
}
