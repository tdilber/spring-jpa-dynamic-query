package com.beyt.jdq.mongodb.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Objects;

/**
 * MongoDB Student entity for testing.
 * Uses embedded Address and DBRef for Department and Courses.
 */
@Document(collection = "students")
public class Student {
    
    @Id
    private Long id;
    
    private String name;
    
    // Embedded address - stored as part of student document
    private Address address;
    
    // Reference to department
    @DBRef
    private Department department;
    
    // Reference to courses (many-to-many)
    @DBRef
    private List<Course> courses;

    public Student() {
    }

    public Student(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Student(Long id, String name, Address address, Department department, List<Course> courses) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.department = department;
        this.courses = courses;
    }

    public static Student of(String name) {
        Student student = new Student();
        student.setName(name);
        return student;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student student = (Student) o;
        return Objects.equals(id, student.id) && Objects.equals(name, student.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address=" + address +
                ", department=" + department +
                ", courses=" + courses +
                '}';
    }
}


