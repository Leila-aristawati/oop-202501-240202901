package com.upb.agripos.service;

import com.upb.agripos.dao.ProductDAO;
import com.upb.agripos.model.Product;
import java.util.List;

public class ProductService {
    private final ProductDAO productDAO;

    public ProductService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public void insert(Product product) throws Exception {
        // Validation
        if (product.getCode() == null || product.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Kode produk tidak boleh kosong");
        }
        if (product.getPrice() < 0) {
            throw new IllegalArgumentException("Harga tidak boleh negatif");
        }
        if (product.getStock() < 0) {
            throw new IllegalArgumentException("Stok tidak boleh negatif");
        }
        productDAO.insert(product);
    }

    public Product findByCode(String code) throws Exception {
        return productDAO.findByCode(code);
    }

    public List<Product> findAll() throws Exception {
        return productDAO.findAll();
    }

    public void update(Product product) throws Exception {
        productDAO.update(product);
    }

    public void delete(String code) throws Exception {
        productDAO.delete(code);
    }

    public void decreaseStock(String code, int quantity) throws Exception {
        productDAO.decreaseStock(code, quantity);
    }

    public void updateStock(String code, int newStock) throws Exception {
        if (newStock < 0) {
            throw new IllegalArgumentException("Stok tidak boleh negatif");
        }
        Product product = productDAO.findByCode(code);
        if (product == null) {
            throw new IllegalArgumentException("Produk tidak ditemukan");
        }
        product.setStock(newStock);
        productDAO.update(product);
    }
}
