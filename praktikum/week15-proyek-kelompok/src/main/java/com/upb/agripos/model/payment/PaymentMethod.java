package com.upb.agripos.model.payment;

/**
 * Enum untuk metode pembayaran yang didukung oleh sistem AGRIPOS
 */
public enum PaymentMethod {
    CASH("TUNAI"),
    E_WALLET("E-WALLET");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PaymentMethod fromString(String value) {
        for (PaymentMethod method : PaymentMethod.values()) {
            if (method.name().equalsIgnoreCase(value) || method.displayName.equalsIgnoreCase(value)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Metode pembayaran tidak dikenali: " + value);
    }

    @Override
    public String toString() {
        return displayName;
    }
}
