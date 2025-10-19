CREATE TABLE address (
                         id INT PRIMARY KEY,
                         street VARCHAR(255),
                         city VARCHAR(255),
                         state VARCHAR(2),
                         zip VARCHAR(5)
);

CREATE TABLE department (
                            id INT PRIMARY KEY,
                            name VARCHAR(255)
);

CREATE TABLE course (
                        id INT PRIMARY KEY,
                        name VARCHAR(255),
                        start_date DATETIME,
                        max_student_count INT,
                        active BIT,
                        description VARCHAR(255)
);

CREATE TABLE student (
                         id INT PRIMARY KEY,
                         name VARCHAR(255),
                         address_id INT,
                         department_id INT,
                         FOREIGN KEY (address_id) REFERENCES address(id),
                         FOREIGN KEY (department_id) REFERENCES department(id)
);

CREATE TABLE student_course (
                               student_id INT,
                               course_id INT,
                               PRIMARY KEY (student_id, course_id),
                               FOREIGN KEY (student_id) REFERENCES student(id),
                               FOREIGN KEY (course_id) REFERENCES course(id)
);

CREATE TABLE admin_user (
                            id INT PRIMARY KEY,
                            username VARCHAR(255),
                            password VARCHAR(255)
);

CREATE TABLE role (
                      id INT PRIMARY KEY,
                      name VARCHAR(255),
                      description VARCHAR(255)
);

CREATE TABLE my_authorization (
                               id INT PRIMARY KEY,
                               name VARCHAR(255),
                               menu_url VARCHAR(255),
                               menu_icon VARCHAR(255)
);

CREATE TABLE admin_user_role (
                                 admin_user_id INT,
                                 role_id INT,
                                 PRIMARY KEY (admin_user_id, role_id),
                                 FOREIGN KEY (admin_user_id) REFERENCES admin_user(id),
                                 FOREIGN KEY (role_id) REFERENCES role(id)
);

CREATE TABLE role_authorization (
                                    id INT PRIMARY KEY,
                                    role_id INT,
                                    authorization_id INT,
                                    FOREIGN KEY (role_id) REFERENCES role(id),
                                    FOREIGN KEY (authorization_id) REFERENCES my_authorization(id)
);
