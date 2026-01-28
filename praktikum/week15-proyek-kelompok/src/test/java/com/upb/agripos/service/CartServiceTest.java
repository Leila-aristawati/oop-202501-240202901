package com.upb.agripos.service;

import com.upb.agripos.dao.CartDAO;
import com.upb.agripos.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FakeProductService extends ProductService {
    java.util.Map<String, Product> map = new java.util.HashMap<>();

    public FakeProductService() {
        super(null);
    }

    @Override
    public Product findByCode(String code) {
        return map.get(code);
    }
}

public class CartServiceTest {
    private CartService cartService;
    private FakeProductService productService;
    private CartDAO cartDAO;

    @BeforeEach
    public void setup() {
        productService = new FakeProductService();
        productService.map.put("P1", new Product("P1", "A", 1000.0, 10));
        productService.map.put("P2", new Product("P2", "B", 500.0, 1));
        cartDAO = com.upb.agripos.dao.CartDAO.getInstance();
        cartService = new CartService(productService, cartDAO);
    }

    @Test
    public void addItemValid() throws Exception {
        cartService.addItem("P1", 2);
        assertEquals(2000.0, cartService.getTotalPrice(), 0.001);
    }

    @Test
    public void addItemNotFound() {
        assertThrows(IllegalArgumentException.class, () -> cartService.addItem("PX", 1));
    }

    @Test
    public void addItemInsufficientStock() {
        assertThrows(IllegalArgumentException.class, () -> cartService.addItem("P2", 2));
    }

    @Test
    public void addItemInvalidQty() {
        assertThrows(IllegalArgumentException.class, () -> cartService.addItem("P1", 0));
    }

    @Test
    public void updateQuantityValid() throws Exception {
        cartService.addItem("P1", 2);
        cartService.updateQuantity("P1", 3);
        assertEquals(3000.0, cartService.getTotalPrice(), 0.001);
    }

    @Test
    public void updateQuantityNegative() {
        assertThrows(IllegalArgumentException.class, () -> cartService.updateQuantity("P1", -1));
    }

    @Test
    public void saveCartReturnsId() throws Exception {
        cartService.addItem("P1", 1);
        int id = cartService.saveCart(5);
        assertTrue(id >= -1);
    }

    @Test
    public void removeItemFromCartService() throws Exception {
        productService.map.put("R1", new Product("R1", "R", 10.0, 10));
        cartService.addItem("R1", 2);
        cartService.removeItem("R1");
        assertTrue(cartService.getCart().getItems().isEmpty());
    }

    @Test
    public void clearCartService() throws Exception {
        productService.map.put("C1", new Product("C1", "C", 5.0, 10));
        cartService.addItem("C1", 1);
        cartService.clearCart();
        assertTrue(cartService.getCart().getItems().isEmpty());
    }

    @Test
    public void getTotalPriceReflectsCart() throws Exception {
        productService.map.put("T1", new Product("T1", "T", 2.5, 10));
        cartService.addItem("T1", 4);
        assertEquals(10.0, cartService.getTotalPrice(), 0.001);
    }
}
