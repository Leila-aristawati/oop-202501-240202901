package com.upb.agripos.service;

import com.upb.agripos.dao.ProductDAO;
import com.upb.agripos.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FakeProductDAOForUnit2 implements ProductDAO {
    java.util.Map<String, Product> store = new java.util.HashMap<>();

    @Override
    public void insert(Product product) throws Exception { if (store.containsKey(product.getCode())) throw new IllegalArgumentException("exists"); store.put(product.getCode(), product); }

    @Override
    public Product findByCode(String code) throws Exception { return store.get(code); }

    @Override
    public List<Product> findAll() throws Exception { return new java.util.ArrayList<>(store.values()); }

    @Override
    public void update(Product product) throws Exception { if (!store.containsKey(product.getCode())) throw new IllegalArgumentException("not found"); store.put(product.getCode(), product); }

    @Override
    public void delete(String code) throws Exception { store.remove(code); }

    @Override
    public void decreaseStock(String code, int quantity) throws Exception { Product p = store.get(code); if (p==null) throw new IllegalArgumentException("not found"); p.setStock(p.getStock()-quantity); }
}

public class ProductServiceMoreTest {
    private ProductService productService;
    private FakeProductDAOForUnit2 dao;

    @BeforeEach
    public void setup() {
        dao = new FakeProductDAOForUnit2();
        productService = new ProductService(dao);
    }

    @Test
    public void productFindAllAfterInsert() throws Exception {
        Product p1 = new Product("A1", "AA", 1.0, 1);
        Product p2 = new Product("A2", "BB", 2.0, 2);
        dao.insert(p1);
        dao.insert(p2);
        List<Product> all = productService.findAll();
        assertEquals(2, all.size());
    }

    @Test
    public void productDecreaseStockWorks() throws Exception {
        Product p = new Product("D1", "Dec", 5.0, 10);
        dao.insert(p);
        productService.decreaseStock("D1", 3);
        assertEquals(7, dao.findByCode("D1").getStock());
    }

    @Test
    public void productDeleteRemoves() throws Exception {
        Product p = new Product("DEL1", "Del", 1.0, 1);
        dao.insert(p);
        productService.delete("DEL1");
        assertNull(dao.findByCode("DEL1"));
    }
}

