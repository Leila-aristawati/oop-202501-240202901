package com.upb.agripos.model;

import java.time.LocalDate;

public class SalesReportData {
    private final LocalDate date;
    private final double totalSales;
    private final int transactionCount;
    private final String cashierName;
    private final String paymentMethod;

    public SalesReportData(LocalDate date, double totalSales, int transactionCount, String cashierName, String paymentMethod) {
        this.date = date;
        this.totalSales = totalSales;
        this.transactionCount = transactionCount;
        this.cashierName = cashierName;
        this.paymentMethod = paymentMethod;
    }

    public LocalDate getDate() { return date; }
    public double getTotalSales() { return totalSales; }
    public int getTransactionCount() { return transactionCount; }
    public String getCashierName() { return cashierName; }
    public String getPaymentMethod() { return paymentMethod; }
}