package com.upb.agripos.service;

import com.upb.agripos.dao.ProductDAO;
import com.upb.agripos.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FakeProductDAO implements ProductDAO {
    java.util.Map<String, Product> store = new java.util.HashMap<>();

    @Override
    public void insert(Product product) throws Exception {
        if (store.containsKey(product.getCode())) throw new IllegalArgumentException("exists");
        store.put(product.getCode(), product);
    }

    @Override
    public Product findByCode(String code) throws Exception {
        return store.get(code);
    }

    @Override
    public List<Product> findAll() throws Exception {
        return new java.util.ArrayList<>(store.values());
    }

    @Override
    public void update(Product product) throws Exception {
        if (!store.containsKey(product.getCode())) throw new IllegalArgumentException("not found");
        store.put(product.getCode(), product);
    }

    @Override
    public void delete(String code) throws Exception {
        store.remove(code);
    }

    @Override
    public void decreaseStock(String code, int quantity) throws Exception {
        Product p = store.get(code);
        if (p == null) throw new IllegalArgumentException("not found");
        p.setStock(p.getStock() - quantity);
    }
}

public class ProductServiceTest {
    private ProductService productService;
    private FakeProductDAO dao;

    @BeforeEach
    public void setup() {
        dao = new FakeProductDAO();
        productService = new ProductService(dao);
    }

    @Test
    public void insertValidProduct() throws Exception {
        Product p = new Product("P1", "A", 1000.0, 5);
        productService.insert(p);
        assertEquals(p, dao.findByCode("P1"));
    }

    @Test
    public void insertInvalidCode() {
        Product p = new Product("", "A", 1000.0, 5);
        assertThrows(IllegalArgumentException.class, () -> productService.insert(p));
    }

    @Test
    public void insertNegativePrice() {
        Product p = new Product("P1", "A", -1.0, 5);
        assertThrows(IllegalArgumentException.class, () -> productService.insert(p));
    }

    @Test
    public void updateStockValid() throws Exception {
        Product p = new Product("P1", "A", 1000.0, 5);
        dao.insert(p);
        productService.updateStock("P1", 10);
        assertEquals(10, dao.findByCode("P1").getStock());
    }

    @Test
    public void updateStockNegative() {
        assertThrows(IllegalArgumentException.class, () -> productService.updateStock("P1", -1));
    }
}

