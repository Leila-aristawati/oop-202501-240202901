package com.upb.agripos.service;

import com.upb.agripos.model.Cart;
import com.upb.agripos.model.CartItem;
import com.upb.agripos.model.Product;
import com.upb.agripos.exception.InsufficientStockException;
import com.upb.agripos.exception.ProductNotFoundException;

import java.util.List;

public class CartService {
    private final Cart cart = new Cart();

    public void addToCart(Product p, int qty) {
        if (qty <= 0) throw new IllegalArgumentException("Qty tidak valid");
        cart.addItem(p, qty);
    }

    public void removeFromCart(Product p) {
        List<CartItem> items = cart.getItems();
        boolean removed = items.removeIf(i -> i.getProduct().equals(p));
        if (!removed) throw new ProductNotFoundException("Produk tidak ditemukan dalam keranjang.");
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

    // Checkout: periksa stok, kurangi stok, kosongkan keranjang, kembalikan total
    public double checkout() {
        for (CartItem item : cart.getItems()) {
            Product p = item.getProduct();
            int qty = item.getQuantity();
            if (p.getStock() < qty) {
                throw new InsufficientStockException("Stok tidak cukup untuk " + p.getName());
            }
        }

        double total = cart.getTotal();
        for (CartItem item : cart.getItems()) {
            item.getProduct().reduceStock(item.getQuantity());
        }
        cart.clear();
        return total;
    }
}