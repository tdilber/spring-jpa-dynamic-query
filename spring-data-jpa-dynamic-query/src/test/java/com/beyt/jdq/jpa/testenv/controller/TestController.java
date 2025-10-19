package com.beyt.jdq.jpa.testenv.controller;

import com.beyt.jdq.core.model.CriteriaList;
import com.beyt.jdq.core.model.DynamicQuery;
import com.beyt.jdq.jpa.testenv.entity.Customer;
import com.beyt.jdq.jpa.testenv.entity.User;
import com.beyt.jdq.jpa.testenv.repository.CustomerRepository;
import com.beyt.jdq.jpa.testenv.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test-api")
public class TestController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;


    @GetMapping("/user")
    public ResponseEntity<List<User>> getUserWithCriteria(CriteriaList criteriaList) {
        List<User> userList = userRepository.findAll(criteriaList);
        return ResponseEntity.ok().body(userList);
    }

    @GetMapping("/customer")
    public ResponseEntity<List<Customer>> getCustomerWithCriteria(CriteriaList criteriaList) {
        List<Customer> customerList = customerRepository.findAll(criteriaList);
        return ResponseEntity.ok().body(customerList);
    }

    @GetMapping("/user/as-page")
    public ResponseEntity<Page<User>> getUserWithSearchFilterAsPage(DynamicQuery dynamicQuery) {
        Page<User> userList = userRepository.findAllAsPage(dynamicQuery);
        return ResponseEntity.ok().body(userList);
    }

    @GetMapping("/user/as-list")
    public ResponseEntity<List<User>> getUserWithSearchFilterAsList(DynamicQuery dynamicQuery) {
        List<User> userList = userRepository.findAll(dynamicQuery);
        return ResponseEntity.ok().body(userList);
    }

    @GetMapping("/customer/as-page")
    public ResponseEntity<Page<Customer>> getCustomerWithSearchFilterAsPage(DynamicQuery dynamicQuery) {
        Page<Customer> customerList = customerRepository.findAllAsPage(dynamicQuery);
        return ResponseEntity.ok().body(customerList);
    }

    @GetMapping("/customer/as-list")
    public ResponseEntity<List<Customer>> getCustomerWithSearchFilterAsList(DynamicQuery dynamicQuery) {
        List<Customer> customerList = customerRepository.findAll(dynamicQuery);
        return ResponseEntity.ok().body(customerList);
    }
}
