package com.syos.infrastructure.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.syos.infrastructure.db.DatabaseManager;
import com.syos.domain.model.Employee;
import com.syos.domain.enums.UserType;

public class UserRepositoryImpl implements UserRepository {

    @Override
    public Employee findByUsername(String username) {
        String sql = "SELECT id, username, password_hash, role FROM users WHERE username = ?";
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username.toLowerCase());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String fetchedUsername = resultSet.getString("username");
                String hashedPassword = resultSet.getString("password_hash");
                String roleString = resultSet.getString("role");

                UserType role = UserType.valueOf(roleString.toUpperCase());

                return new Employee(fetchedUsername, hashedPassword, role);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding user", e);
        }
        return null;
    }

    @Override
    public boolean existsByUsername(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection connection = DatabaseManager.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username.toLowerCase());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException("Error checking user existence", e);
        }
    }

    @Override
    public void clear() {
        // Implementation for clearing users, if needed
    }
}