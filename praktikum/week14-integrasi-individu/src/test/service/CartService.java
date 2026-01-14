package com.upb.agripos.service;

import java.util.List;

import com.upb.agripos.model.Cart;
import com.upb.agripos.model.CartItem;
import com.upb.agripos.model.Product;

public class CartService {
    private final Cart cart = new Cart();

    public void addToCart(Product p, int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("Qty tidak valid");
        }
        cart.addItem(p, qty);
    }

    public double getTotal() {
        return cart.getTotal();
    }

    public List<CartItem> getCartItems() {
        return cart.getItems();
    }

    public void clearCart() {
        cart.clear();
    }

    public Cart getCart() {
        return cart;
    }
}