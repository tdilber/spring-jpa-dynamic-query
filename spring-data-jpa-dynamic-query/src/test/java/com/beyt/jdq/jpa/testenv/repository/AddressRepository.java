package com.beyt.jdq.jpa.testenv.repository;

import com.beyt.jdq.jpa.repository.JpaDynamicQueryRepository;
import com.beyt.jdq.jpa.testenv.entity.school.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long>, JpaDynamicQueryRepository<Address, Long> {
}
