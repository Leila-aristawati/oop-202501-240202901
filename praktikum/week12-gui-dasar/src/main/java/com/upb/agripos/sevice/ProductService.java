package com.upb.agripos.service;

import java.util.List;

import com.upb.agripos.dao.ProductDAO;
import com.upb.agripos.model.Product;

public class ProductService {
    private final ProductDAO productDAO;

    public ProductService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public void addProduct(Product product) {
        try {
            productDAO.insert(product);
        } catch (Exception e) {
            throw new RuntimeException("Gagal menambah produk: " + e.getMessage());
        }
    }

    public List<Product> getAllProducts() {
        try {
            return productDAO.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Gagal mengambil data produk: " + e.getMessage());
        }
    }
}
