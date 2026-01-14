package com.upb.agripos.model;

public class Product {
    private String code;
    private String name;
    private double price;
    private int stock;

    public Product(String code, String name, double price, int stock) {
        this.code = code;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }

    // Kurangi stok saat checkout
    public void reduceStock(int qty) {
        if (qty <= 0) throw new IllegalArgumentException("Quantity harus lebih dari 0.");
        if (qty > this.stock) throw new IllegalArgumentException("Stok tidak cukup untuk produk " + name + ".");
        this.stock -= qty;
    }
}