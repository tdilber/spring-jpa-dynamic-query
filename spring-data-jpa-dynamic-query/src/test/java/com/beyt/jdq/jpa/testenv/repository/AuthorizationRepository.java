package com.beyt.jdq.jpa.testenv.repository;

import com.beyt.jdq.jpa.repository.JpaDynamicQueryRepository;
import com.beyt.jdq.jpa.testenv.entity.authorization.Authorization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorizationRepository extends JpaRepository<Authorization, Long>, JpaDynamicQueryRepository<Authorization, Long> {}
