package ru;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.net.spi.InetAddressResolverProvider;
import java.sql.*;
import java.util.UUID;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static final String URL = "jdbc:mysql://localhost:3306/GB_students";
    private static final String USER = "root";
    private static final String PASSWORD = "12345678";

    public static void main(String[] args) throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)){
            prepareTables(connection);
            run(connection);
        }
    }
    private static void prepareTables(Connection connection) throws SQLException {
//        try (Statement st = connection.createStatement()){
//            st.execute("""
//                    DROP TABLE `students2`;
//                    """);
//        }
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS `groups2`(
                    `id` bigint PRIMARY KEY,
                    `name` VARCHAR(128) NOT NULL);
                    """);
        }
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    INSERT INTO `groups2`(`id`,`name`)
                    VALUES
                    (1,'22'),
                    (2, '33'),
                    (3, '44');
                    """);
        }
        try (Statement st = connection.createStatement()){
            st.execute("""
                    CREATE TABLE IF NOT EXISTS `students2`(
                    `id` bigint PRIMARY KEY,
                    `first_name` VARCHAR(256) NOT NULL,
                    `second_name` VARCHAR(256) NOT NULL,
                    `group_id` bigint NOT NULL,
                     FOREIGN KEY (group_id)
                       REFERENCES `groups2` (id)
                    );
                    """);
        }

        try (Statement st = connection.createStatement()){
            st.execute("""
                    INSERT INTO `students2`(`id`,`first_name`, `second_name`,`group_id`) VALUES
                    (1, 'Ivan', 'Ivanov',1),
                    (2, 'Petr', 'Petrov',2),
                    (3, 'Fedor', 'Fedorov',3);
                    """);
        }



    }
    private static void run(Connection connection) throws SQLException {
        Configuration configuration = new Configuration().configure();
        try (SessionFactory sessionFactory = configuration.buildSessionFactory()) {
            try(Session session = sessionFactory.openSession()){
                Students2 students = session.find(Students2.class, 1L);
                System.out.println(students);
            }

            Students2 newStudent = new Students2();
            newStudent.setId(5L);
            newStudent.setFirst_name("Kolya");
            newStudent.setSecond_name("Frolov");
            newStudent.setGroup_id(1L);
            System.out.println(newStudent);
            try(Session session = sessionFactory.openSession()){
                Transaction tx = session.beginTransaction();
                session.persist(newStudent);
                tx.commit();
            }

            try(Session session = sessionFactory.openSession()){
                newStudent.setFirst_name("Artem");
                Transaction tx = session.beginTransaction();
                session.merge(newStudent);
                tx.commit();
            }

            try(Session session = sessionFactory.openSession()){
                Transaction tx = session.beginTransaction();
                session.remove(newStudent);
                tx.commit();
            }

            try(Session session = sessionFactory.openSession()){
                Students2 students = session.find(Students2.class, 4L);
                System.out.println(students);
            }
        }

    }
}