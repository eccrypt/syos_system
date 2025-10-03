package com.syos.infrastructure.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.syos.infrastructure.db.DatabaseManager;
import com.syos.domain.model.Bill;
import com.syos.domain.model.BillItem;
import com.syos.domain.model.Product;

public class BillingRepositoryImpl implements BillingRepository {

	public void save(Bill bill) {
		String insertBill = "INSERT INTO bill " +
				"(serial_number, bill_date, total_amount, cash_tendered, change_returned, transaction_type) " +
				"VALUES (?, ?, ?, ?, ?, ?) " +
				"RETURNING id";

		String insertItem = "INSERT INTO bill_item (bill_id, product_code, quantity, total_price, discount_amount) " +
				"VALUES (?, ?, ?, ?, ?)";

		try (Connection connection = DatabaseManager.getInstance().getConnection()) {
			connection.setAutoCommit(false);

			int generatedBillId;
			try (PreparedStatement preparedStatement = connection.prepareStatement(insertBill)) {
				preparedStatement.setInt(1, bill.getSerialNumber());
				preparedStatement.setTimestamp(2, new Timestamp(bill.getBillDate().getTime()));
				preparedStatement.setDouble(3, bill.getTotalAmount());
				preparedStatement.setDouble(4, bill.getCashTendered());
				preparedStatement.setDouble(5, bill.getChangeReturned());
				preparedStatement.setString(6, bill.getTransactionType());

				ResultSet rs = preparedStatement.executeQuery();
				if (!rs.next()) {
					throw new RuntimeException("Failed to retrieve generated bill ID.");
				}
				generatedBillId = rs.getInt(1);

				bill.setId(generatedBillId);
			}

			try (PreparedStatement preparedStatement = connection.prepareStatement(insertItem)) {
				for (BillItem item : bill.getItems()) {
					preparedStatement.setInt(1, generatedBillId);
					preparedStatement.setString(2, item.getProduct().getCode());
					preparedStatement.setInt(3, item.getQuantity());
					preparedStatement.setDouble(4, item.getTotalPrice());
					preparedStatement.setDouble(5, item.getDiscountAmount());
					preparedStatement.addBatch();
				}
				preparedStatement.executeBatch();
			}

			connection.commit();
		} catch (SQLException e) {
			throw new RuntimeException("Error saving bill & items", e);
		}
	}

	public int nextSerial() {
		String sql = "SELECT COALESCE(MAX(serial_number), 0) + 1 " +
				"FROM bill " +
				"WHERE DATE(bill_date) = CURRENT_DATE";
		try (Connection connection = DatabaseManager.getInstance().getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(sql);
				ResultSet result = preparedStatement.executeQuery()) {
			if (result.next()) {
				return result.getInt(1);
			}
		} catch (SQLException e) {
			throw new RuntimeException("Error generating daily serial", e);
		}
		return 1;
	}

	@Override
	public List<Bill> findAll() {
		String sql = "SELECT id, serial_number, bill_date, total_amount, cash_tendered, change_returned, transaction_type FROM bill ORDER BY bill_date DESC";
		List<Bill> bills = new ArrayList<>();

		try (Connection connection = DatabaseManager.getInstance().getConnection();
			 PreparedStatement preparedStatement = connection.prepareStatement(sql);
			 ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				int billId = resultSet.getInt("id");
				List<BillItem> items = findBillItemsByBillId(billId);

				Bill bill = new Bill(billId, resultSet.getInt("serial_number"),
					new Date(resultSet.getTimestamp("bill_date").getTime()),
					resultSet.getDouble("total_amount"), resultSet.getDouble("cash_tendered"),
					resultSet.getDouble("change_returned"), resultSet.getString("transaction_type"));
				bill.setItems(items);

				bills.add(bill);
			}
		} catch (SQLException e) {
			throw new RuntimeException("Error retrieving bills", e);
		}

		return bills;
	}

	@Override
	public Bill findBySerial(int serialNumber) {
		String sql = "SELECT id, serial_number, bill_date, total_amount, cash_tendered, change_returned, transaction_type FROM bill WHERE serial_number = ?";
		Bill bill = null;

		try (Connection connection = DatabaseManager.getInstance().getConnection();
			 PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

			preparedStatement.setInt(1, serialNumber);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					int billId = resultSet.getInt("id");
					List<BillItem> items = findBillItemsByBillId(billId);

					bill = new Bill(billId, resultSet.getInt("serial_number"),
						new Date(resultSet.getTimestamp("bill_date").getTime()),
						resultSet.getDouble("total_amount"), resultSet.getDouble("cash_tendered"),
						resultSet.getDouble("change_returned"), resultSet.getString("transaction_type"));
					bill.setItems(items);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Error retrieving bill by serial", e);
		}

		return bill;
	}

	private List<BillItem> findBillItemsByBillId(int billId) {
		String sql = "SELECT id, product_code, quantity, total_price, discount_amount FROM bill_item WHERE bill_id = ?";
		List<BillItem> items = new ArrayList<>();
		ProductRepository productRepository = new ProductRepositoryImpl();

		try (Connection connection = DatabaseManager.getInstance().getConnection();
			 PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

			preparedStatement.setInt(1, billId);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {
					String productCode = resultSet.getString("product_code");
					Product product = productRepository.findByCode(productCode);
					if (product != null) {
						BillItem item = new BillItem(resultSet.getInt("id"), billId, product,
							resultSet.getInt("quantity"), resultSet.getDouble("total_price"),
							resultSet.getDouble("discount_amount"));
						items.add(item);
					}
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Error retrieving bill items", e);
		}

		return items;
	}
}
