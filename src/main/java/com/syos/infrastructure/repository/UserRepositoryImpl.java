package com.syos.infrastructure.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.syos.infrastructure.db.DatabaseManager;
import com.syos.domain.model.Employee;
import com.syos.domain.model.Customer;
import com.syos.domain.model.User;
import com.syos.domain.enums.UserType;
import org.mindrot.jbcrypt.BCrypt;

public class UserRepositoryImpl implements UserRepository {

	@Override
	public User findByEmail(String email) {
		String sql = "SELECT id, email, password_hash, first_name, last_name, role, created_date FROM users WHERE email = ?";
		try (Connection connection = DatabaseManager.getInstance().getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

			preparedStatement.setString(1, email.toLowerCase());
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				String email1 = resultSet.getString("email");
				String hashedPassword = resultSet.getString("password_hash");
				String firstName = resultSet.getString("first_name");
				String lastName = resultSet.getString("last_name");
				String roleString = resultSet.getString("role");
				Timestamp createdDate = resultSet.getTimestamp("created_date");

				UserType role = UserType.valueOf(roleString.toUpperCase());

				if (role == UserType.CUSTOMER) {
					return new Customer(email1, hashedPassword, firstName, lastName, role, createdDate);
				} else {
					return new Employee(email1, hashedPassword, firstName, lastName, role, createdDate);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Error finding user", e);
		}
		return null;
	}

	@Override
	public boolean existsByEmail(String email) {
		String sql = "SELECT 1 FROM users WHERE email = ?";
		try (Connection connection = DatabaseManager.getInstance().getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setString(1, email.toLowerCase());
			System.out.println("Executing query: " + sql + " with email: " + email.toLowerCase());
			ResultSet resultSet = preparedStatement.executeQuery();
			boolean exists = resultSet.next();
			System.out.println("User existence check result for '" + email + "': " + exists);
			return exists;
		} catch (SQLException e) {
			System.err.println("SQLException in existsByemail: " + e.getMessage());
			System.err.println("SQL State: " + e.getSQLState());
			System.err.println("Error Code: " + e.getErrorCode());
			throw new RuntimeException("Error checking user existence", e);
		}
	}

	@Override
	public void save(Employee employee) {
		String sql = "INSERT INTO users (email, first_name, last_name, password_hash, role, created_date) VALUES (?, ?, ?, ?, ?, ?)";
		try (Connection connection = DatabaseManager.getInstance().getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

			String hashedPassword = BCrypt.hashpw(employee.getPassword(), BCrypt.gensalt());
			preparedStatement.setString(1, employee.getEmail());
			preparedStatement.setString(2, employee.getFirstName());
			preparedStatement.setString(3, employee.getLastName());
			preparedStatement.setString(4, hashedPassword);
			preparedStatement.setString(5, employee.getRole().name().toUpperCase());
			preparedStatement.setTimestamp(6, employee.getCreatedDate());

			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("Error saving user", e);
		}
	}

}