package com.upb.agripos.controller;

import java.util.List;

import com.upb.agripos.model.Product;
import com.upb.agripos.service.ProductService;

public class ProductController {

    private final ProductService service = new ProductService();

    public void addProduct(Product p) {
        service.insert(p);
    }

    public void deleteProduct(String code) {
        service.delete(code);
    }

    public List<Product> getAllProducts() {
        return service.findAll();
    }
}