package com.beyt.jdq.jpa.testenv.repository;

import com.beyt.jdq.jpa.repository.JpaDynamicQueryRepository;
import com.beyt.jdq.jpa.testenv.entity.authorization.RoleAuthorization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleAuthorizationRepository extends JpaRepository<RoleAuthorization, Long>, JpaDynamicQueryRepository<RoleAuthorization, Long> {}
