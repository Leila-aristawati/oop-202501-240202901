package com.upb.agripos;

import com.upb.agripos.model.Cart;
import com.upb.agripos.model.Product;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CartServiceTest {

    @Test
    void testCartTotalPrice() {
        Cart cart = new Cart();
        Product product1 = new Product("P001", "Product 1", 50.0, 10);
        Product product2 = new Product("P002", "Product 2", 30.0, 10);

        cart.addItem(product1, 2);
        cart.addItem(product2, 3);

        assertEquals(2 * 50.0 + 3 * 30.0, cart.getTotalPrice());
    }

    @Test
    void testCartAddItem() {
        Cart cart = new Cart();
        Product product = new Product("P001", "Product 1", 50.0, 10);

        cart.addItem(product, 2);
        assertEquals(1, cart.getItems().size());
        assertEquals(2, cart.getItems().get(0).getQuantity());

        cart.addItem(product, 3);
        assertEquals(1, cart.getItems().size());
        assertEquals(5, cart.getItems().get(0).getQuantity());
    }
}
