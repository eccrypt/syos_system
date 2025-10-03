package com.syos.infrastructure.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.syos.infrastructure.db.DatabaseManager;
import com.syos.domain.model.ShelfStock;

public class ShelfStockRepositoryImpl implements ShelfStockRepository {

    public ShelfStockRepositoryImpl(ProductRepository productRepository) {
    }

    @Override
    public int getQuantity(String productCode) {
        String sql = "SELECT quantity_on_shelf FROM shelf_stock WHERE product_code = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, productCode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    @Override
    public void upsertQuantity(String productCode, int qty) {
        String sql = "INSERT INTO shelf_stock(product_code,quantity_on_shelf) VALUES(?,?) ON CONFLICT(product_code) DO UPDATE SET quantity_on_shelf = shelf_stock.quantity_on_shelf + EXCLUDED.quantity_on_shelf";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, productCode);
            ps.setInt(2, qty);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deductQuantity(String productCode, int qty) {
        String sql = "UPDATE shelf_stock SET quantity_on_shelf = quantity_on_shelf - ? WHERE product_code = ?";
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, qty);
            ps.setString(2, productCode);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> getAllProductCodes() {
        String sql = "SELECT DISTINCT product_code FROM shelf_stock";
        List<String> productCodes = new ArrayList<>();
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                productCodes.add(rs.getString("product_code"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all product codes", e);
        }
        return productCodes;
    }

    // Additional methods needed by InventoryManager
    public List<ShelfStock> getBatchesOnShelf(String productCode) {
        // This would need to be implemented based on your database schema
        // For now, return empty list to allow compilation
        return new ArrayList<>();
    }

    public void updateBatchQuantityOnShelf(String productCode, int batchId, int quantity, LocalDate expiryDate) {
        // Implementation needed
    }

    public void deductQuantityFromBatchOnShelf(String productCode, int batchId, int quantity) {
        // Implementation needed
    }

    public void removeBatchFromShelf(String productCode, int batchId) {
        // Implementation needed
    }

    @Override
    public ShelfStock findByCode(String productCode) {
        // This would need to be implemented based on your database schema
        // For now, return null to allow compilation
        return null;
    }
}
