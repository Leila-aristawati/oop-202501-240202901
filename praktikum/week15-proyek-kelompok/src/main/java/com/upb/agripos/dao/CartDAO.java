package com.upb.agripos.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import javax.sql.DataSource;

import com.upb.agripos.config.DatabaseConnection;
import com.upb.agripos.model.Cart;
import com.upb.agripos.model.CartItem;
import com.upb.agripos.model.Product;

public class CartDAO {
    private static CartDAO instance;
    private final DataSource dataSource;

    private CartDAO() {
        this.dataSource = DatabaseConnection.getInstance().getDataSource();
        System.out.println("✓ CartDAO initialized (using DataSource)");
    }

    public static synchronized CartDAO getInstance() {
        if (instance == null) {
            instance = new CartDAO();
        }
        return instance;
    }

    public int saveCart(Cart cart, int userId) {
        String sql = "INSERT INTO carts (user_id, total_price, status) VALUES (?, ?, ?) RETURNING id";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDouble(2, cart.getTotalPrice());
            ps.setString(3, "COMPLETED");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int cartId = rs.getInt(1);
                    saveCartItems(cartId, cart.getItems());
                    return cartId;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving cart: " + e.getMessage());
        }
        return -1;
    }

    private void saveCartItems(int cartId, List<CartItem> items) {
        String sql = "INSERT INTO cart_items (cart_id, product_code, quantity, subtotal) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (CartItem item : items) {
                ps.setInt(1, cartId);
                ps.setString(2, item.getProduct().getCode());
                ps.setInt(3, item.getQuantity());
                ps.setDouble(4, item.getTotalPrice());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            System.err.println("Error saving cart items: " + e.getMessage());
        }
    }

    public java.util.List<com.upb.agripos.model.SoldProduct> findSoldProductsByDateRange(java.time.LocalDateTime start, java.time.LocalDateTime end) {
        java.util.List<com.upb.agripos.model.SoldProduct> list = new java.util.ArrayList<>();
        String sql = "SELECT ci.product_code, p.name, SUM(ci.quantity) AS qty, SUM(ci.subtotal) AS value " +
                     "FROM cart_items ci " +
                     "JOIN carts c ON ci.cart_id = c.id " +
                     "JOIN transactions t ON t.cart_id = c.id " +
                     "JOIN products p ON p.code = ci.product_code " +
                     "WHERE t.transaction_date BETWEEN ? AND ? AND t.status = 'SUCCESS' " +
                     "GROUP BY ci.product_code, p.name " +
                     "ORDER BY qty DESC";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(end));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new com.upb.agripos.model.SoldProduct(
                        rs.getString("product_code"),
                        rs.getString("name"),
                        rs.getInt("qty"),
                        rs.getDouble("value")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching sold products by date range: " + e.getMessage());
        }
        return list;
    }

    public java.util.List<com.upb.agripos.model.SoldProduct> findSoldProductsByDateRangeAndUser(java.time.LocalDateTime start, java.time.LocalDateTime end, int userId) {
        java.util.List<com.upb.agripos.model.SoldProduct> list = new java.util.ArrayList<>();
        String sql = "SELECT ci.product_code, p.name, SUM(ci.quantity) AS qty, SUM(ci.subtotal) AS value " +
                     "FROM cart_items ci " +
                     "JOIN carts c ON ci.cart_id = c.id " +
                     "JOIN transactions t ON t.cart_id = c.id " +
                     "JOIN products p ON p.code = ci.product_code " +
                     "WHERE t.transaction_date BETWEEN ? AND ? AND t.status = 'SUCCESS' AND t.user_id = ? " +
                     "GROUP BY ci.product_code, p.name " +
                     "ORDER BY qty DESC";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(end));
            ps.setInt(3, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new com.upb.agripos.model.SoldProduct(
                        rs.getString("product_code"),
                        rs.getString("name"),
                        rs.getInt("qty"),
                        rs.getDouble("value")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching sold products by date range and user: " + e.getMessage());
        }
        return list;
    }

    // New: fetch cart items (with product details) for given cart id
    public java.util.List<CartItem> findCartItemsByCartId(int cartId) {
        java.util.List<CartItem> items = new java.util.ArrayList<>();
        String sql = "SELECT ci.product_code, ci.quantity, ci.subtotal, p.name, p.price, p.stock " +
                     "FROM cart_items ci " +
                     "JOIN products p ON p.code = ci.product_code " +
                     "WHERE ci.cart_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cartId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String code = rs.getString("product_code");
                    String name = rs.getString("name");
                    double price = rs.getDouble("price");
                    int stock = rs.getInt("stock");
                    int qty = rs.getInt("quantity");
                    // Create product and cart item
                    Product p = new Product(code, name, price, stock);
                    CartItem ci = new CartItem(p, qty);
                    items.add(ci);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching cart items for cart " + cartId + ": " + e.getMessage());
        }
        return items;
    }
}
