package com.beyt.jdq.jpa.testenv.repository;

import com.beyt.jdq.jpa.repository.JpaDynamicQueryRepository;
import com.beyt.jdq.jpa.testenv.entity.authorization.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminUserRepository extends JpaRepository<AdminUser, Long>, JpaDynamicQueryRepository<AdminUser, Long> {}
