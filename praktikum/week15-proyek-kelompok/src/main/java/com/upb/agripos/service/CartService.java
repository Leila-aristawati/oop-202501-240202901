package com.upb.agripos.service;

import com.upb.agripos.dao.CartDAO;
import com.upb.agripos.model.Cart;
import com.upb.agripos.model.Product;

public class CartService {
    private final Cart cart;
    private final ProductService productService;
    private final CartDAO cartDAO;

    public CartService(ProductService productService, CartDAO cartDAO) {
        this.cart = new Cart();
        this.productService = productService;
        this.cartDAO = cartDAO;
    }

    public void addItem(String productCode, int quantity) throws Exception {
        Product product = productService.findByCode(productCode);
        if (product == null) {
            throw new IllegalArgumentException("Produk tidak ditemukan");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Jumlah harus lebih dari 0");
        }
        if (product.getStock() < quantity) {
            throw new IllegalArgumentException("Stok tidak cukup");
        }
        cart.addItem(product, quantity);
    }

    public void removeItem(String productCode) {
        cart.removeItem(productCode);
    }

    public void updateQuantity(String productCode, int quantity) throws Exception {
        if (quantity < 0) {
            throw new IllegalArgumentException("Jumlah tidak boleh negatif");
        }
        Product product = productService.findByCode(productCode);
        if (product != null && product.getStock() < quantity) {
            throw new IllegalArgumentException("Stok tidak cukup");
        }
        cart.updateQuantity(productCode, quantity);
    }

    public Cart getCart() {
        return cart;
    }

    public double getTotalPrice() {
        return cart.getTotalPrice();
    }

    public void clearCart() {
        cart.clear();
    }

    public int saveCart(int userId) {
        return cartDAO.saveCart(cart, userId);
    }

    public java.util.List<com.upb.agripos.model.SoldProduct> getSoldProductsByDateRange(java.time.LocalDateTime start, java.time.LocalDateTime end) {
        return CartDAO.getInstance().findSoldProductsByDateRange(start, end);
    }
}
