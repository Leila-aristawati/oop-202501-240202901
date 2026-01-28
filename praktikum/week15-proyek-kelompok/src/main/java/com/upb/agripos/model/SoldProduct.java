package com.upb.agripos.model;

public class SoldProduct {
    private final String productCode;
    private final String name;
    private final int quantitySold;
    private final double totalValue;

    public SoldProduct(String productCode, String name, int quantitySold, double totalValue) {
        this.productCode = productCode;
        this.name = name;
        this.quantitySold = quantitySold;
        this.totalValue = totalValue;
    }

    public String getProductCode() { return productCode; }
    public String getName() { return name; }
    public int getQuantitySold() { return quantitySold; }
    public double getTotalValue() { return totalValue; }
}
