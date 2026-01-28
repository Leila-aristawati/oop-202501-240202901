package com.upb.agripos.model;

import com.upb.agripos.model.Cart;
import com.upb.agripos.model.Product;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CartModelMoreTest {

    @Test
    public void cartClearRemovesAll() {
        Cart cart = new Cart();
        Product p = new Product("Z", "Z", 1.0, 5);
        cart.addItem(p, 2);
        cart.clear();
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    public void cartGetTotalWithMultipleItems() {
        Cart cart = new Cart();
        Product p1 = new Product("X1", "X", 2.0, 10);
        Product p2 = new Product("X2", "Y", 3.0, 10);
        cart.addItem(p1, 1);
        cart.addItem(p2, 2);
        assertEquals(8.0, cart.getTotalPrice(), 0.001);
    }
}

