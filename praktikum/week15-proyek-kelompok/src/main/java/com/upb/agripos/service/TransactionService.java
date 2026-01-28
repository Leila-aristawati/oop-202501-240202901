package com.upb.agripos.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.upb.agripos.dao.CartDAO;
import com.upb.agripos.dao.TransactionDAO;
import com.upb.agripos.model.SoldProduct;
import com.upb.agripos.model.Transaction;
import com.upb.agripos.model.CartItem;

public class TransactionService {
    private final TransactionDAO transactionDAO;
    private final CartDAO cartDAO;

    public TransactionService() {
        this.transactionDAO = TransactionDAO.getInstance();
        this.cartDAO = CartDAO.getInstance();
    }

    public void saveTransaction(Transaction transaction) {
        transactionDAO.save(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactionDAO.findAll();
    }

    public List<Transaction> getTransactionsByUserId(int userId) {
        return transactionDAO.findByUserId(userId);
    }

    public List<Transaction> getTransactionsByDateRange(LocalDateTime start, LocalDateTime end) {
        return transactionDAO.findByDateRange(start, end);
    }

    public String generateDailyReport(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);
        List<Transaction> transactions = getTransactionsByDateRange(start, end);

        double totalSales = transactions.stream().mapToDouble(Transaction::getTotalAmount).sum();
        int totalTransactions = transactions.size();
        Map<String, Double> salesByCashier = transactions.stream()
            .collect(Collectors.groupingBy(Transaction::getCashierName, Collectors.summingDouble(Transaction::getTotalAmount)));
        Map<String, Long> transactionsByPaymentMethod = transactions.stream()
            .collect(Collectors.groupingBy(Transaction::getPaymentMethod, Collectors.counting()));

        StringBuilder report = new StringBuilder();
        report.append("================================\n");
        report.append("    LAPORAN PENJUALAN HARIAN\n");
        report.append("================================\n");
        report.append("Tanggal: ").append(date).append("\n");
        report.append("Total Transaksi: ").append(totalTransactions).append("\n");
        report.append("Total Penjualan: Rp ").append(String.format("%,.0f", totalSales)).append("\n");
        report.append("--------------------------------\n");
        report.append("Penjualan per Kasir:\n");
        salesByCashier.forEach((cashier, sales) ->
            report.append("- ").append(cashier).append(": Rp ").append(String.format("%,.0f", sales)).append("\n"));
        report.append("--------------------------------\n");
        report.append("Metode Pembayaran:\n");
        transactionsByPaymentMethod.forEach((method, count) ->
            report.append("- ").append(method).append(": ").append(count).append(" transaksi\n"));
        report.append("================================\n");

        return report.toString();
    }

    public String generateCashierReport(int userId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        List<Transaction> transactions = transactionDAO.findByUserId(userId).stream()
            .filter(t -> !t.getTransactionDate().isBefore(start) && !t.getTransactionDate().isAfter(end))
            .collect(Collectors.toList());

        double totalSales = transactions.stream().mapToDouble(Transaction::getTotalAmount).sum();
        int totalTransactions = transactions.size();
        Map<String, Long> transactionsByPaymentMethod = transactions.stream()
            .collect(Collectors.groupingBy(Transaction::getPaymentMethod, Collectors.counting()));

        String cashierName = transactions.isEmpty() ? "Unknown" : transactions.get(0).getCashierName();

        StringBuilder report = new StringBuilder();
        report.append("================================\n");
        report.append("    LAPORAN PENJUALAN KASIR\n");
        report.append("================================\n");
        report.append("Kasir: ").append(cashierName).append("\n");
        report.append("Periode: ").append(startDate).append(" - ").append(endDate).append("\n");
        report.append("Total Transaksi: ").append(totalTransactions).append("\n");
        report.append("Total Penjualan: Rp ").append(String.format("%,.0f", totalSales)).append("\n");
        report.append("--------------------------------\n");
        report.append("Metode Pembayaran:\n");
        transactionsByPaymentMethod.forEach((method, count) ->
            report.append("- ").append(method).append(": ").append(count).append(" transaksi\n"));
        report.append("================================\n");

        return report.toString();
    }

    // New methods for enhanced reporting

    public List<Transaction> getTransactionsForReport(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        return getTransactionsByDateRange(start, end);
    }

    public double getTotalSalesByPeriod(LocalDate startDate, LocalDate endDate) {
        return getTransactionsForReport(startDate, endDate).stream()
            .mapToDouble(Transaction::getTotalAmount)
            .sum();
    }

    public double getTotalOmzet() {
        return getAllTransactions().stream()
            .mapToDouble(Transaction::getTotalAmount)
            .sum();
    }

    public List<SoldProduct> getTopSellingProducts(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        return cartDAO.findSoldProductsByDateRange(start, end);
    }

    public List<SoldProduct> getTopSellingProductsByUser(LocalDate startDate, LocalDate endDate, int userId) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        return cartDAO.findSoldProductsByDateRangeAndUser(start, end, userId);
    }

    public Map<LocalDate, Double> getDailySalesMap(LocalDate startDate, LocalDate endDate) {
        return getTransactionsForReport(startDate, endDate).stream()
            .collect(Collectors.groupingBy(
                t -> t.getTransactionDate().toLocalDate(),
                Collectors.summingDouble(Transaction::getTotalAmount)
            ));
    }

    public Map<YearMonth, Double> getMonthlySalesMap(LocalDate startDate, LocalDate endDate) {
        return getTransactionsForReport(startDate, endDate).stream()
            .collect(Collectors.groupingBy(
                t -> YearMonth.from(t.getTransactionDate()),
                Collectors.summingDouble(Transaction::getTotalAmount)
            ));
    }

    public Map<LocalDate, Double> getDailySalesMapByUser(LocalDate startDate, LocalDate endDate, int userId) {
        return getTransactionsForReport(startDate, endDate).stream()
            .filter(t -> t.getUserId() == userId)
            .collect(Collectors.groupingBy(
                t -> t.getTransactionDate().toLocalDate(),
                Collectors.summingDouble(Transaction::getTotalAmount)
            ));
    }

    public Map<String, Double> getSalesByPaymentMethod(LocalDate startDate, LocalDate endDate) {
        return getTransactionsForReport(startDate, endDate).stream()
            .collect(Collectors.groupingBy(
                Transaction::getPaymentMethod,
                Collectors.summingDouble(Transaction::getTotalAmount)
            ));
    }

    public Map<String, Double> getSalesByPaymentMethodByUser(LocalDate startDate, LocalDate endDate, int userId) {
        return getTransactionsForReport(startDate, endDate).stream()
            .filter(t -> t.getUserId() == userId)
            .collect(Collectors.groupingBy(
                Transaction::getPaymentMethod,
                Collectors.summingDouble(Transaction::getTotalAmount)
            ));
    }

    public Map<String, Double> getSalesByCashier(LocalDate startDate, LocalDate endDate) {
        return getTransactionsForReport(startDate, endDate).stream()
            .collect(Collectors.groupingBy(
                Transaction::getCashierName,
                Collectors.summingDouble(Transaction::getTotalAmount)
            ));
    }

    public double getMonthlySales(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        return getTotalSalesByPeriod(startDate, endDate);
    }

    public Map<YearMonth, Double> getSalesComparison(int currentYear, int currentMonth, int previousMonths) {
        Map<YearMonth, Double> comparison = new java.util.LinkedHashMap<>();
        for (int i = previousMonths - 1; i >= 0; i--) {
            YearMonth ym = YearMonth.of(currentYear, currentMonth).minusMonths(i);
            double sales = getMonthlySales(ym.getYear(), ym.getMonthValue());
            comparison.put(ym, sales);
        }
        return comparison;
    }

    // New helper to fetch cart items for a saved cart id
    public java.util.List<CartItem> getCartItemsByCartId(int cartId) {
        return cartDAO.findCartItemsByCartId(cartId);
    }
}
