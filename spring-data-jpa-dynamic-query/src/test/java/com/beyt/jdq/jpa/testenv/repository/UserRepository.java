package com.beyt.jdq.jpa.testenv.repository;

import com.beyt.jdq.jpa.repository.JpaDynamicQueryRepository;
import com.beyt.jdq.jpa.testenv.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaDynamicQueryRepository<User, Long> {
}

