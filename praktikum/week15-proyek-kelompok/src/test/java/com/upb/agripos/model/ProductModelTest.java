package com.upb.agripos.model;

import com.upb.agripos.model.Product;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ProductModelTest {

    @Test
    public void productProperties() {
        Product p = new Product("X1", "Name", 123.45, 10);
        assertEquals("X1", p.getCode());
        assertEquals("Name", p.getName());
        assertEquals(123.45, p.getPrice());
        assertEquals(10, p.getStock());
    }

    @Test
    public void productSetters() {
        Product p = new Product("X2", "Name2", 10.0, 1);
        p.setName("New");
        p.setPrice(20.0);
        p.setStock(5);
        assertEquals("New", p.getName());
        assertEquals(20.0, p.getPrice());
        assertEquals(5, p.getStock());
    }

    @Test
    public void productNegativePriceAllowedInModel() {
        Product p = new Product("NX", "Neg", -5.0, 0);
        assertEquals(-5.0, p.getPrice());
    }

    @Test
    public void productStockProperty() {
        Product p = new Product("S1", "S", 1.0, 100);
        assertEquals(100, p.getStock());
    }

    @Test
    public void productCodeNonEmpty() {
        Product p = new Product("C1", "C", 1.0, 1);
        assertFalse(p.getCode().trim().isEmpty());
    }
}

