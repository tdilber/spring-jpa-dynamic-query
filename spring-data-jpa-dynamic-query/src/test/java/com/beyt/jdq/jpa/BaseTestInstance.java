package com.beyt.jdq.jpa;

import com.beyt.jdq.jpa.testenv.entity.Customer;
import com.beyt.jdq.jpa.testenv.entity.User;
import com.beyt.jdq.jpa.testenv.entity.authorization.*;
import com.beyt.jdq.jpa.testenv.entity.school.Address;
import com.beyt.jdq.jpa.testenv.entity.school.Course;
import com.beyt.jdq.jpa.testenv.entity.school.Department;
import com.beyt.jdq.jpa.testenv.entity.school.Student;
import com.beyt.jdq.jpa.testenv.repository.*;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.Calendar;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

public abstract class BaseTestInstance {

    @Autowired
    protected CustomerRepository customerRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected AddressRepository addressRepository;

    @Autowired
    protected RoleRepository roleRepository;

    @Autowired
    protected DepartmentRepository departmentRepository;

    @Autowired
    protected CourseRepository courseRepository;

    @Autowired
    protected StudentRepository studentRepository;

    @Autowired
    protected AuthorizationRepository authorizationRepository;

    @Autowired
    protected RoleAuthorizationRepository roleAuthorizationRepository;

    @Autowired
    protected AdminUserRepository adminUserRepository;


    @Autowired
    protected EntityManager entityManager;

    public final User user1;
    public final User user2;
    public final User user3;
    public final User user4;
    public final User user5;
    public final User user6;
    public final User user7;
    public final User user8;

    public final Customer customer1;
    public final Customer customer2;
    public final Customer customer3;
    public final Customer customer4;
    public final Customer customer5;
    public final Customer customer6;
    public final Customer customer7;
    public final Customer customer8;
    public static final Calendar INSTANCE = Calendar.getInstance();

    protected Address address1 = new Address(1L, "123 Main St", "New York", "NY", "10001");
    protected Address address2 = new Address(2L, "456 Park Ave", "Chicago", "IL", "60605");
    protected Address address3 = new Address(3L, "789 Broadway", "Los Angeles", "CA", "90001");
    protected Address address4 = new Address(4L, "321 Market St", "San Francisco", "CA", "94105");
    protected Address address5 = new Address(5L, "654 Elm St", "Dallas", "TX", "75001");
    protected Address address6 = new Address(6L, "987 Oak St", "Houston", "TX", "77002");
    protected Address address7 = new Address(7L, "345 Pine St", "Philadelphia", "PA", "19019");
    protected Address address8 = new Address(8L, "678 Maple St", "Phoenix", "AZ", "85001");
    protected Address address9 = new Address(9L, "102 Beach St", "Miami", "FL", "33101");
    protected Address address10 = new Address(10L, "567 Hill St", "Atlanta", "GA", "30301");


    protected Department department1 = new Department(1L, "Computer Science");
    protected Department department2 = new Department(2L, "Mathematics");
    protected Department department3 = new Department(3L, "Physics");
    protected Department department4 = new Department(4L, "Chemistry");
    protected Department department5 = new Department(5L, "Biology");
    protected Department department6 = new Department(6L, "English Literature");
    protected Department department7 = new Department(7L, "History");
    protected Department department8 = new Department(8L, "Geography");
    protected Department department9 = new Department(9L, "Political Science");
    protected Department department10 = new Department(10L, "Economics");

    protected Course course1 = new Course(1L, "Introduction to Computer Science", Timestamp.valueOf("2016-06-18 00:00:00"), 50, true, "Introduction to fundamental concepts of computer science.");
    protected Course course2 = new Course(2L, "Calculus I", Timestamp.valueOf("2017-06-18 00:00:00"), 60, true, "Introduction to fundamental concepts of calculus.");
    protected Course course3 = new Course(3L, "Calculus II", Timestamp.valueOf("2018-06-18 00:00:00"), 250, null, "Advanced topics in calculus including integrals and series.");
    protected Course course4 = new Course(4L, "Physics I", Timestamp.valueOf("2019-06-18 00:00:00"), 250, null, "Introduction to classical mechanics and Newtonian physics.");
    protected Course course5 = new Course(5L, "Physics II", Timestamp.valueOf("2020-06-18 00:00:00"), 250, null, "Advanced topics in physics including electromagnetism and thermodynamics.");
    protected Course course6 = new Course(6L, "Chemistry I", Timestamp.valueOf("2021-06-18 00:00:00"), 40, null, "Basic principles of chemistry including atomic structure and chemical bonding.");
    protected Course course7 = new Course(7L, "Chemistry II", Timestamp.valueOf("2022-06-18 00:00:00"), 30, null, "Continuation of chemistry studies covering topics like kinetics and equilibrium.");
    protected Course course8 = new Course(8L, "Biology I", Timestamp.valueOf("2015-06-18 00:00:00"), 20, true, "Introduction to cellular biology and genetics.");
    protected Course course9 = new Course(9L, "Biology II", Timestamp.valueOf("2013-06-18 00:00:00"), 54, true, "Advanced topics in biology including evolution and ecology.");
    protected Course course10 = new Course(10L, "English Literature I", Timestamp.valueOf("2025-06-18 00:00:00"), 10, false, "Exploration of classic works of English literature and literary analysis.");

    protected Student student1 = new Student(1L, "John Doe", address1, department1, List.of(course1, course2));
    protected Student student2 = new Student(2L, "Jane Smith", address2, department2, List.of(course2, course4));
    protected Student student3 = new Student(3L, "Robert Johnson", address3, department3, List.of(course3));
    protected Student student4 = new Student(4L, "Emily Davis", address4, department4, List.of(course4));
    protected Student student5 = new Student(5L, "Michael Miller", address5, department5, List.of(course5));
    protected Student student6 = new Student(6L, "Sarah Wilson", address6, department6, List.of(course6));
    protected Student student7 = new Student(7L, "David Moore", address7, department7, List.of(course7));
    protected Student student8 = new Student(8L, "Jessica Taylor", address8, department8, List.of(course8));
    protected Student student9 = new Student(9L, "Daniel Anderson", address9, department9, List.of(course9));
    protected Student student10 = new Student(10L, "Jennifer Thomas", address10, department10, List.of(course10));
    protected Student student11 = new Student(11L, "Talha Dilber", null, null, List.of());

    protected Authorization authorization1 = new Authorization(1L, "auth1", "/url1", "icon1");
    protected Authorization authorization2 = new Authorization(2L, "auth2", "/url2", "icon2");
    protected Authorization authorization3 = new Authorization(3L, "auth3", "/url3", "icon3");
    protected Authorization authorization4 = new Authorization(4L, "auth4", "/url4", "icon4");
    protected Authorization authorization5 = new Authorization(5L, "auth5", "/url5", "icon5");

    protected Role role1 = new Role(1L, "role1", "description1");
    protected Role role2 = new Role(2L, "role2", "description2");
    protected Role role3 = new Role(3L, "role3", "description3");
    protected Role role4 = new Role(4L, "role4", "description4");
    protected Role role5 = new Role(5L, "role5", "description5");

    protected RoleAuthorization roleAuthorization1 = new RoleAuthorization(1L, role1, authorization1);
    protected RoleAuthorization roleAuthorization2 = new RoleAuthorization(2L, role2, authorization2);
    protected RoleAuthorization roleAuthorization3 = new RoleAuthorization(3L, role3, authorization3);
    protected RoleAuthorization roleAuthorization4 = new RoleAuthorization(4L, role4, authorization4);
    protected RoleAuthorization roleAuthorization5 = new RoleAuthorization(5L, role5, authorization5);


    protected AdminUser adminUser1 = new AdminUser(1L, "admin1", "password1", List.of(role1));
    protected AdminUser adminUser2 = new AdminUser(2L, "admin2", "password2", List.of(role2));
    protected AdminUser adminUser3 = new AdminUser(3L, "admin3", "password3", List.of(role3));
    protected AdminUser adminUser4 = new AdminUser(4L, "admin4", "password4", List.of(role4));
    protected AdminUser adminUser5 = new AdminUser(5L, "admin5", "password5", List.of(role5));

    public BaseTestInstance() {
        user1 = new User(null, "Name 1", "Surname 1", 35, INSTANCE.toInstant(), User.Status.PASSIVE, User.Type.USER);
        customer1 = new Customer(null, "Customer 1", 20, INSTANCE.toInstant(), user1);
        INSTANCE.add(Calendar.MONTH, -1);
        user2 = new User(null, "Name 2", "Surname 1", 36, INSTANCE.toInstant(), User.Status.ACTIVE, User.Type.ADMIN);
        customer2 = new Customer(null, "Customer 2", 21, INSTANCE.toInstant(), user2);
        INSTANCE.add(Calendar.MONTH, -1);
        user3 = new User(null, "Name 3", "Surname 1", 37, INSTANCE.toInstant(), User.Status.PASSIVE, User.Type.USER);
        customer3 = new Customer(null, "Customer 3", 22, INSTANCE.toInstant(), user3);
        INSTANCE.add(Calendar.MONTH, -1);
        user4 = new User(null, "Name 4", "Surname 1", 38, INSTANCE.toInstant(), User.Status.ACTIVE, User.Type.USER);
        customer4 = new Customer(null, "Customer 4", 23, INSTANCE.toInstant(), user4);
        INSTANCE.add(Calendar.MONTH, -1);
        user5 = new User(null, "Name 5", "Surname 1", 39, INSTANCE.toInstant(), User.Status.PASSIVE, User.Type.ADMIN);
        customer5 = new Customer(null, "Customer 5", 24, INSTANCE.toInstant(), user5);
        INSTANCE.add(Calendar.MONTH, -1);
        user6 = new User(null, "Name 6", "Surname 1", 40, INSTANCE.toInstant(), User.Status.ACTIVE, User.Type.ADMIN);
        customer6 = new Customer(null, "Customer 6", 25, INSTANCE.toInstant(), user6);
        INSTANCE.add(Calendar.MONTH, -1);
        user7 = new User(null, "Name 7", "Surname 1", 41, INSTANCE.toInstant(), User.Status.ACTIVE, User.Type.USER);
        customer7 = new Customer(null, "Customer 7", 26, INSTANCE.toInstant(), user7);
        INSTANCE.add(Calendar.MONTH, -1);
        user8 = new User(null, "Name 8", "Surname 1", 42, INSTANCE.toInstant(), User.Status.PASSIVE, User.Type.ADMIN);
        customer8 = new Customer(null, null, 27, INSTANCE.toInstant(), user8);

    }


    @BeforeAll
    public void init() {
        if (userRepository.count() != 0) {
            return;
        }
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
        userRepository.save(user5);
        userRepository.save(user6);
        userRepository.save(user7);
        userRepository.save(user8);

        if (customerRepository.count() != 0) {
            return;
        }
        customerRepository.save(customer1);
        customerRepository.save(customer2);
        customerRepository.save(customer3);
        customerRepository.save(customer4);
        customerRepository.save(customer5);
        customerRepository.save(customer6);
        customerRepository.save(customer7);
        customerRepository.save(customer8);

        if (addressRepository.count() != 0) {
            return;
        }
        addressRepository.saveAllAndFlush(List.of(address1, address2, address3, address4, address5, address6, address7, address8, address9, address10));

        departmentRepository.saveAllAndFlush(List.of(department1, department2, department3, department4, department5, department6, department7, department8, department9, department10));

        courseRepository.saveAllAndFlush(List.of(course1, course2, course3, course4, course5, course6, course7, course8, course9, course10));

        studentRepository.saveAllAndFlush(List.of(student1, student2, student3, student4, student5, student6, student7, student8, student9, student10, student11));

        if (authorizationRepository.count() != 0) {
            return;
        }
        authorizationRepository.saveAllAndFlush(List.of(authorization1, authorization2, authorization3, authorization4, authorization5));

        roleRepository.saveAllAndFlush(List.of(role1, role2, role3, role4, role5));

        roleAuthorizationRepository.saveAllAndFlush(List.of(roleAuthorization1, roleAuthorization2, roleAuthorization3, roleAuthorization4, roleAuthorization5));

        adminUserRepository.saveAllAndFlush(List.of(adminUser1, adminUser2, adminUser3, adminUser4, adminUser5));
    }

}
