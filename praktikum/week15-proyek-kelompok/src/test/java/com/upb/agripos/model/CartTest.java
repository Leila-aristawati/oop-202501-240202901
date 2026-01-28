package com.upb.agripos.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CartTest {

    @Test
    public void testAddNewItem() {
        Product p = new Product("P1", "Prod 1", 10000.0, 10);
        Cart cart = new Cart();
        cart.addItem(p, 2);
        assertEquals(1, cart.getItems().size());
        assertEquals(2, cart.getItems().get(0).getQuantity());
    }

    @Test
    public void testAddMergeQuantity() {
        Product p = new Product("P1", "Prod 1", 10000.0, 10);
        Cart cart = new Cart();
        cart.addItem(p, 2);
        cart.addItem(p, 3);
        assertEquals(1, cart.getItems().size());
        assertEquals(5, cart.getItems().get(0).getQuantity());
    }

    @Test
    public void testRemoveItem() {
        Product p = new Product("P1", "Prod 1", 10000.0, 10);
        Cart cart = new Cart();
        cart.addItem(p, 2);
        cart.removeItem("P1");
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    public void testUpdateQuantity() {
        Product p = new Product("P1", "Prod 1", 10000.0, 10);
        Cart cart = new Cart();
        cart.addItem(p, 5);
        cart.updateQuantity("P1", 3);
        assertEquals(3, cart.getItems().get(0).getQuantity());
    }

    @Test
    public void testUpdateQuantityToZeroRemoves() {
        Product p = new Product("P1", "Prod 1", 10000.0, 10);
        Cart cart = new Cart();
        cart.addItem(p, 2);
        cart.updateQuantity("P1", 0);
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    public void testGetTotalPrice() {
        Product p1 = new Product("P1", "Prod 1", 10000.0, 10);
        Product p2 = new Product("P2", "Prod 2", 15000.0, 10);
        Cart cart = new Cart();
        cart.addItem(p1, 2); // 20000
        cart.addItem(p2, 1); // 15000
        assertEquals(35000.0, cart.getTotalPrice(), 0.001);
    }
}

