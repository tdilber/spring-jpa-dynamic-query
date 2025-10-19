-- Insert queries for Address
INSERT INTO address (id, street, city, state, zip) VALUES (1, '123 Main St', 'New York', 'NY', '10001');
INSERT INTO address (id, street, city, state, zip) VALUES (2, '456 Park Ave', 'Chicago', 'IL', '60605');
INSERT INTO address (id, street, city, state, zip) VALUES (3, '789 Broadway', 'Los Angeles', 'CA', '90001');
INSERT INTO address (id, street, city, state, zip) VALUES (4, '321 Market St', 'San Francisco', 'CA', '94105');
INSERT INTO address (id, street, city, state, zip) VALUES (5, '654 Elm St', 'Dallas', 'TX', '75001');
INSERT INTO address (id, street, city, state, zip) VALUES (6, '987 Oak St', 'Houston', 'TX', '77002');
INSERT INTO address (id, street, city, state, zip) VALUES (7, '345 Pine St', 'Philadelphia', 'PA', '19019');
INSERT INTO address (id, street, city, state, zip) VALUES (8, '678 Maple St', 'Phoenix', 'AZ', '85001');
INSERT INTO address (id, street, city, state, zip) VALUES (9, '102 Beach St', 'Miami', 'FL', '33101');
INSERT INTO address (id, street, city, state, zip) VALUES (10, '567 Hill St', 'Atlanta', 'GA', '30301');
-- ... repeat for 8 more addresses

-- Insert queries for Department
INSERT INTO department (id, name) VALUES (1, 'Computer Science');
INSERT INTO department (id, name) VALUES (2, 'Mathematics');
INSERT INTO department (id, name) VALUES (3, 'Physics');
INSERT INTO department (id, name) VALUES (4, 'Chemistry');
INSERT INTO department (id, name) VALUES (5, 'Biology');
INSERT INTO department (id, name) VALUES (6, 'English Literature');
INSERT INTO department (id, name) VALUES (7, 'History');
INSERT INTO department (id, name) VALUES (8, 'Geography');
INSERT INTO department (id, name) VALUES (9, 'Political Science');
INSERT INTO department (id, name) VALUES (10, 'Economics');
-- ... repeat for 8 more departments

-- Insert queries for Course
INSERT INTO course (id, name, start_date, max_student_count, active, description)
VALUES
    (1, 'Introduction to Computer Science', '2016-06-18', 50, 1, 'Introduction to fundamental concepts of computer science.'),
    (2, 'Calculus I', '2017-06-18', 60, 1, 'Introduction to fundamental concepts of calculus.'),
    (3, 'Calculus II', '2018-06-18', 250, null, 'Advanced topics in calculus including integrals and series.'),
    (4, 'Physics I', '2019-06-18', 250, null, 'Introduction to classical mechanics and Newtonian physics.'),
    (5, 'Physics II', '2020-06-18', 250, null, 'Advanced topics in physics including electromagnetism and thermodynamics.'),
    (6, 'Chemistry I', '2021-06-18', 40, null, 'Basic principles of chemistry including atomic structure and chemical bonding.'),
    (7, 'Chemistry II', '2022-06-18', 30, null, 'Continuation of chemistry studies covering topics like kinetics and equilibrium.'),
    (8, 'Biology I', '2015-06-18', 20, 1, 'Introduction to cellular biology and genetics.'),
    (9, 'Biology II', '2013-06-18', 54, 1, 'Advanced topics in biology including evolution and ecology.'),
    (10, 'English Literature I', '2025-06-18', 10, 0, 'Exploration of classic works of English literature and literary analysis.');

-- ... repeat for 8 more courses

-- Insert queries for Student
INSERT INTO student (id, name, address_id, department_id) VALUES (1, 'John Doe', 1, 1);
INSERT INTO student (id, name, address_id, department_id) VALUES (2, 'Jane Smith', 2, 2);
INSERT INTO student (id, name, address_id, department_id) VALUES (3, 'Robert Johnson', 3, 3);
INSERT INTO student (id, name, address_id, department_id) VALUES (4, 'Emily Davis', 4, 4);
INSERT INTO student (id, name, address_id, department_id) VALUES (5, 'Michael Miller', 5, 5);
INSERT INTO student (id, name, address_id, department_id) VALUES (6, 'Sarah Wilson', 6, 6);
INSERT INTO student (id, name, address_id, department_id) VALUES (7, 'David Moore', 7, 7);
INSERT INTO student (id, name, address_id, department_id) VALUES (8, 'Jessica Taylor', 8, 8);
INSERT INTO student (id, name, address_id, department_id) VALUES (9, 'Daniel Anderson', 9, 9);
INSERT INTO student (id, name, address_id, department_id) VALUES (10, 'Jennifer Thomas', 10, 10);
INSERT INTO student (id, name, address_id, department_id) VALUES (11, 'Talha Dilber', null, null);
-- ... repeat for 8 more students

-- Insert queries for Student_Course
INSERT INTO student_course (student_id, course_id) VALUES (1, 1);
INSERT INTO student_course (student_id, course_id) VALUES (1, 2);
INSERT INTO student_course (student_id, course_id) VALUES (2, 2);
INSERT INTO student_course (student_id, course_id) VALUES (2, 4);
INSERT INTO student_course (student_id, course_id) VALUES (3, 3);
INSERT INTO student_course (student_id, course_id) VALUES (4, 4);
INSERT INTO student_course (student_id, course_id) VALUES (5, 5);
INSERT INTO student_course (student_id, course_id) VALUES (6, 6);
INSERT INTO student_course (student_id, course_id) VALUES (7, 7);
INSERT INTO student_course (student_id, course_id) VALUES (8, 8);
INSERT INTO student_course (student_id, course_id) VALUES (9, 9);
INSERT INTO student_course (student_id, course_id) VALUES (10, 10);
-- ... repeat for 8 more student-course relationships

-- data.sql
-- Insert queries for AdminUser
INSERT INTO admin_user (id, username, password) VALUES (1, 'admin1', 'password1');
INSERT INTO admin_user (id, username, password) VALUES (2, 'admin2', 'password2');
INSERT INTO admin_user (id, username, password) VALUES (3, 'admin3', 'password3');
INSERT INTO admin_user (id, username, password) VALUES (4, 'admin4', 'password4');
INSERT INTO admin_user (id, username, password) VALUES (5, 'admin5', 'password5');
-- ... repeat for 9 more admin users

-- Insert queries for Role
INSERT INTO role (id, name, description) VALUES (1, 'role1', 'description1');
INSERT INTO role (id, name, description) VALUES (2, 'role2', 'description2');
INSERT INTO role (id, name, description) VALUES (3, 'role3', 'description3');
INSERT INTO role (id, name, description) VALUES (4, 'role4', 'description4');
INSERT INTO role (id, name, description) VALUES (5, 'role5', 'description5');

-- ... repeat for 9 more roles

-- Insert queries for Authorization
INSERT INTO my_authorization (id, name, menu_url, menu_icon) VALUES (1, 'auth1', '/url1', 'icon1');
INSERT INTO my_authorization (id, name, menu_url, menu_icon) VALUES (2, 'auth2', '/url2', 'icon2');
INSERT INTO my_authorization (id, name, menu_url, menu_icon) VALUES (3, 'auth3', '/url3', 'icon3');
INSERT INTO my_authorization (id, name, menu_url, menu_icon) VALUES (4, 'auth4', '/url4', 'icon4');
INSERT INTO my_authorization (id, name, menu_url, menu_icon) VALUES (5, 'auth5', '/url5', 'icon5');
-- ... repeat for 9 more authorizations

-- Insert queries for AdminUserRole
INSERT INTO admin_user_role (admin_user_id, role_id) VALUES (1, 1);
INSERT INTO admin_user_role (admin_user_id, role_id) VALUES (2, 2);
INSERT INTO admin_user_role (admin_user_id, role_id) VALUES (3, 3);
INSERT INTO admin_user_role (admin_user_id, role_id) VALUES (4, 4);
INSERT INTO admin_user_role (admin_user_id, role_id) VALUES (5, 5);
-- ... repeat for 9 more admin user-role relationships

-- Insert queries for RoleAuthorization
INSERT INTO role_authorization (id, role_id, authorization_id) VALUES (1, 1, 1);
INSERT INTO role_authorization (id, role_id, authorization_id) VALUES (2, 2, 2);
INSERT INTO role_authorization (id, role_id, authorization_id) VALUES (3, 3, 3);
INSERT INTO role_authorization (id, role_id, authorization_id) VALUES (4, 4, 4);
INSERT INTO role_authorization (id, role_id, authorization_id) VALUES (5, 5, 5);
