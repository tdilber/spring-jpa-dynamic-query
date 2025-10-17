package com.beyt.jdq.jpa.testenv.repository;

import com.beyt.jdq.jpa.repository.JpaDynamicQueryRepository;
import com.beyt.jdq.jpa.testenv.entity.authorization.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long>, JpaDynamicQueryRepository<Role, Long> {}
