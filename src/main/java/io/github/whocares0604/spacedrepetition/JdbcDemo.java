package io.github.whocares0604.spacedrepetition;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcDemo {
    public static void main(String[] args) {
        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected to PostgreSQL");
            System.out.println("Closed: " + connection.isClosed());

        } catch (SQLException e) {
            System.out.println("Connection failed");
            System.out.println(e.getMessage());
        }

    }
}