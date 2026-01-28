package com.upb.agripos.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.upb.agripos.config.DatabaseConnection;
import com.upb.agripos.model.Transaction;

public class TransactionDAO {
    private static TransactionDAO instance;
    private final DataSource dataSource;

    private TransactionDAO() {
        this.dataSource = DatabaseConnection.getInstance().getDataSource();
        System.out.println("✓ TransactionDAO initialized (using DataSource)");
    }

    public static synchronized TransactionDAO getInstance() {
        if (instance == null) {
            instance = new TransactionDAO();
        }
        return instance;
    }

    public void save(Transaction transaction) {
        String sql = "INSERT INTO transactions (cart_id, user_id, total_amount, payment_method, status, transaction_date, paid_amount, change, cashier_name) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, transaction.getCartId());
            ps.setInt(2, transaction.getUserId());
            ps.setDouble(3, transaction.getTotalAmount());
            ps.setString(4, transaction.getPaymentMethod());
            ps.setString(5, transaction.getStatus());
            ps.setTimestamp(6, Timestamp.valueOf(transaction.getTransactionDate()));
            ps.setDouble(7, transaction.getPaidAmount());
            ps.setDouble(8, transaction.getChange());
            ps.setString(9, transaction.getCashierName());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    // transaction.setId(rs.getInt(1)); // if needed
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving transaction: " + e.getMessage());
        }
    }

    public List<Transaction> findAll() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT id, cart_id, user_id, total_amount, payment_method, status, transaction_date, paid_amount, change, cashier_name FROM transactions ORDER BY transaction_date DESC";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                transactions.add(new Transaction(
                    rs.getInt("id"),
                    rs.getInt("cart_id"),
                    rs.getInt("user_id"),
                    rs.getDouble("total_amount"),
                    rs.getString("payment_method"),
                    rs.getString("status"),
                    rs.getTimestamp("transaction_date").toLocalDateTime(),
                    rs.getDouble("paid_amount"),
                    rs.getDouble("change"),
                    rs.getString("cashier_name")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching transactions: " + e.getMessage());
        }
        return transactions;
    }

    public List<Transaction> findByUserId(int userId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT id, cart_id, user_id, total_amount, payment_method, status, transaction_date, paid_amount, change, cashier_name FROM transactions WHERE user_id = ? ORDER BY transaction_date DESC";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transactions.add(new Transaction(
                        rs.getInt("id"),
                        rs.getInt("cart_id"),
                        rs.getInt("user_id"),
                        rs.getDouble("total_amount"),
                        rs.getString("payment_method"),
                        rs.getString("status"),
                        rs.getTimestamp("transaction_date").toLocalDateTime(),
                        rs.getDouble("paid_amount"),
                        rs.getDouble("change"),
                        rs.getString("cashier_name")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching transactions by user: " + e.getMessage());
        }
        return transactions;
    }

    public List<Transaction> findByDateRange(LocalDateTime start, LocalDateTime end) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT id, cart_id, user_id, total_amount, payment_method, status, transaction_date, paid_amount, change, cashier_name FROM transactions WHERE transaction_date BETWEEN ? AND ? ORDER BY transaction_date DESC";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(end));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transactions.add(new Transaction(
                        rs.getInt("id"),
                        rs.getInt("cart_id"),
                        rs.getInt("user_id"),
                        rs.getDouble("total_amount"),
                        rs.getString("payment_method"),
                        rs.getString("status"),
                        rs.getTimestamp("transaction_date").toLocalDateTime(),
                        rs.getDouble("paid_amount"),
                        rs.getDouble("change"),
                        rs.getString("cashier_name")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching transactions by date range: " + e.getMessage());
        }
        return transactions;
    }
}
