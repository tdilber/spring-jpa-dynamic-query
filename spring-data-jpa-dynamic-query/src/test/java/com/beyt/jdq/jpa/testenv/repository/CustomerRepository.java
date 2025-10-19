package com.beyt.jdq.jpa.testenv.repository;

import com.beyt.jdq.jpa.repository.JpaDynamicQueryRepository;
import com.beyt.jdq.jpa.testenv.entity.Customer;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaDynamicQueryRepository<Customer, Long> {
}
