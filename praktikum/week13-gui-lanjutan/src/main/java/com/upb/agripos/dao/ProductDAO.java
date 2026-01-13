package com.upb.agripos.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.upb.agripos.model.Product;

public class ProductDAO {

    private final String URL = "jdbc:postgresql://localhost:5432/agripos";
    private final String USER = "postgres";
    private final String PASS = "leila230107"; 

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public void insert(Product p) {
        String sql = "INSERT INTO products(code,name,price,stock) VALUES (?,?,?,?)";

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, p.getCode());
            ps.setString(2, p.getName());
            ps.setDouble(3, p.getPrice());
            ps.setInt(4, p.getStock());

            ps.executeUpdate();
            System.out.println("INSERT BERHASIL");

        } catch (SQLException e) {
            System.out.println("INSERT GAGAL");
            e.printStackTrace();
        }
    }

    public List<Product> findAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products";

        try (Connection c = getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Product(
                    rs.getString("code"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("stock")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void delete(String code) {
        String sql = "DELETE FROM products WHERE code=?";

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, code);
            ps.executeUpdate();
            System.out.println("DELETE BERHASIL");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}