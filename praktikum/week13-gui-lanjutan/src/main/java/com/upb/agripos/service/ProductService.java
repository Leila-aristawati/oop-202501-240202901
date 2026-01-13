package com.upb.agripos.service;

import java.util.List;

import com.upb.agripos.dao.ProductDAO;
import com.upb.agripos.model.Product;

public class ProductService {
    private final ProductDAO dao = new ProductDAO();

    public void insert(Product p) {
        dao.insert(p);
    }

    public void delete(String code) {
        dao.delete(code);
    }

    public List<Product> findAll() {
        return dao.findAll();
    }
}