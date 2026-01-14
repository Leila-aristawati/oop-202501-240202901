package com.upb.agripos.controller;

import java.util.List;

import com.upb.agripos.model.CartItem;
import com.upb.agripos.model.Product;
import com.upb.agripos.service.CartService;
import com.upb.agripos.service.ProductService;

public class PosController {

    private final ProductService productService;
    private final CartService cartService;

    public PosController(ProductService ps, CartService cs) {
        this.productService = ps;
        this.cartService = cs;
    }

    public void addProduct(Product p) {
        productService.addProduct(p);
    }

    public void deleteProduct(String code) {
        productService.deleteProduct(code);
    }

    public List<Product> getProducts() {
        return productService.getAllProducts();
    }

    public void addToCart(Product p, int qty) {
        cartService.addToCart(p, qty);
    }

    public void removeFromCart(Product p) {
        cartService.removeFromCart(p);
    }

    public double getCartTotal() {
        return cartService.getTotal();
    }

    public List<CartItem> getCartItems() {
        return cartService.getCartItems();
    }

    public void clearCart() {
        cartService.clearCart();
    }

    public double checkoutCart() {
        return cartService.checkout();
    }
}