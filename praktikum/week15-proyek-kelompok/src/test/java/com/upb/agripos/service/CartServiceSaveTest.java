package com.upb.agripos.service;

import com.upb.agripos.model.Product;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FakeProdSvcSimple extends ProductService {
    java.util.Map<String, Product> map = new java.util.HashMap<>();
    public FakeProdSvcSimple() { super(null); }
    @Override public Product findByCode(String code) { return map.get(code); }
}

public class CartServiceSaveTest {

    @Test
    public void cartServiceSaveReturnsId() throws Exception {
        FakeProdSvcSimple fps = new FakeProdSvcSimple();
        fps.map.put("S1", new Product("S1", "S", 10.0, 10));
        CartService cs = new CartService(fps, com.upb.agripos.dao.CartDAO.getInstance());
        cs.addItem("S1", 2);
        int id = cs.saveCart(7);
        assertTrue(id >= -1);
    }
}

