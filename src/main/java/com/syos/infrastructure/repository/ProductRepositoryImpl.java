package com.syos.infrastructure.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.syos.infrastructure.db.DatabaseManager;
import com.syos.domain.model.Product;

public class ProductRepositoryImpl implements ProductRepository {

    @Override
    public Product findByCode(String code) {
		String sql = "SELECT code, name, price FROM product WHERE code = ?";
		try (Connection connection = DatabaseManager.getInstance().getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

			preparedStatement.setString(1, code);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				return new Product(resultSet.getString("code"), resultSet.getString("name"),
						resultSet.getDouble("price"));
			}
		} catch (Exception e) {
			throw new RuntimeException("Error loading product by code", e);
		}
		return null;
	   }

	   @Override
	   public List<Product> findAll() {
		String sql = "SELECT code, name, price FROM product";
		List<Product> products = new ArrayList<>();
		try (Connection connection = DatabaseManager.getInstance().getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

			ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				products.add(new Product(resultSet.getString("code"), resultSet.getString("name"),
						resultSet.getDouble("price")));
			}
		} catch (Exception e) {
			throw new RuntimeException("Error loading all products", e);
		}
		return products;
	}

	@Override
	public void add(Product product) {
		if (findByCode(product.getCode()) != null) {
			throw new RuntimeException("Product code already exists: " + product.getCode());
		}

		String sql = "INSERT INTO product(code, name, price) VALUES (?, ?, ?)";
		try (Connection conn = DatabaseManager.getInstance().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, product.getCode());
			ps.setString(2, product.getName());
			ps.setDouble(3, product.getPrice());
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("Error adding new product", e);
		}
	}

	@Override
	public void update(Product product) {
		String sql = "UPDATE product SET name = ?, price = ? WHERE code = ?";
		try (Connection conn = DatabaseManager.getInstance().getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, product.getName());
			ps.setDouble(2, product.getPrice());
			ps.setString(3, product.getCode());
			int affectedRows = ps.executeUpdate();
			if (affectedRows == 0) {
				throw new RuntimeException("Product with code " + product.getCode() + " not found for update.");
			}
		} catch (SQLException e) {
			throw new RuntimeException("Error updating product", e);
		}
	}

	   @Override
	   public void clear() {
	       String sql = "DELETE FROM product";
	       try (Connection conn = DatabaseManager.getInstance().getConnection();
	            PreparedStatement ps = conn.prepareStatement(sql)) {
	           ps.executeUpdate();
	       } catch (SQLException e) {
	           throw new RuntimeException("Error clearing products", e);
	       }
	   }
}
