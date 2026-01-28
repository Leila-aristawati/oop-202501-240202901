package com.upb.agripos.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import com.upb.agripos.model.Transaction;
import com.upb.agripos.model.User;
import com.upb.agripos.service.CartService;
import com.upb.agripos.service.ProductService;
import com.upb.agripos.service.TransactionService;
import com.upb.agripos.service.UserService;
import com.upb.agripos.view.ReportDialog;
import com.upb.agripos.view.TransactionDetailView;
import com.upb.agripos.view.WarehouseReportDialog;

import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;

public class AdminController {
    private final UserService userService;
    private final TransactionService transactionService;
    private final ProductService productService;
    private final CartService cartService;

    public AdminController(UserService userService, TransactionService transactionService,
                           ProductService productService, CartService cartService) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.productService = productService;
        this.cartService = cartService;
    }

    public void addUser(TextField txtUsername, TextField txtPassword, TextField txtFullName, ComboBox<String> cbRole, TableView<User> userTable) {
        try {
            String username = txtUsername.getText().trim();
            String password = txtPassword.getText().trim();
            String fullName = txtFullName.getText().trim();
            String role = cbRole.getValue();

            if (userService.addUser(username, password, fullName, role)) {
                showAlert("Sukses", "User berhasil ditambahkan");
                clearFields(txtUsername, txtPassword, txtFullName);
                loadUsers(userTable);
            } else {
                showAlert("Error", "Gagal menambahkan user. Username mungkin sudah ada.");
            }
        } catch (Exception e) {
            showAlert("Error", "Gagal menambahkan user: " + e.getMessage());
        }
    }

    public void loadUsers(TableView<User> userTable) {
        try {
            javafx.collections.ObservableList<User> userItems = userTable.getItems();
            var fresh = userService.getAllUsers();
            if (userItems instanceof javafx.collections.transformation.FilteredList) {
                javafx.collections.ObservableList<User> src = (javafx.collections.ObservableList<User>) ((javafx.collections.transformation.FilteredList<User>) userItems).getSource();
                src.setAll(fresh);
            } else {
                userItems.setAll(fresh);
            }
        } catch (Exception e) {
            showAlert("Error", "Gagal memuat users: " + e.getMessage());
        }
    }

    public void loadHistory(TableView<Transaction> historyTable) {
        try {
            javafx.collections.ObservableList<Transaction> historyItems = historyTable.getItems();
            var fresh = transactionService.getAllTransactions();
            if (historyItems instanceof javafx.collections.transformation.FilteredList) {
                javafx.collections.ObservableList<Transaction> src = (javafx.collections.ObservableList<Transaction>) ((javafx.collections.transformation.FilteredList<Transaction>) historyItems).getSource();
                src.setAll(fresh);
            } else {
                historyItems.setAll(fresh);
            }
        } catch (Exception e) {
            showAlert("Error", "Gagal memuat history: " + e.getMessage());
        }
    }

    // Show transaction detail (uses TransactionService to fetch cart items and opens a dialog)
    public void showTransactionDetail(Transaction transaction) {
        try {
            if (transaction == null) return;
            var items = transactionService.getCartItemsByCartId(transaction.getCartId());
            TransactionDetailView.show(transaction, items);
        } catch (Exception e) {
            showAlert("Error", "Gagal menampilkan detail transaksi: " + e.getMessage());
        }
    }

    public void showCashierSalesReport() {
        // Show report for all cashiers without selection
        try {
            TextInputDialog startDateDialog = new TextInputDialog(LocalDate.now().minusDays(7).toString());
            startDateDialog.setTitle("Laporan Penjualan Kasir");
            startDateDialog.setHeaderText("Masukkan tanggal mulai (format: YYYY-MM-DD)");
            startDateDialog.setContentText("Tanggal Mulai:");
            Optional<String> startResult = startDateDialog.showAndWait();

            if (startResult.isPresent()) {
                LocalDate startDate = LocalDate.parse(startResult.get().trim());

                TextInputDialog endDateDialog = new TextInputDialog(LocalDate.now().toString());
                endDateDialog.setTitle("Laporan Penjualan Kasir");
                endDateDialog.setHeaderText("Masukkan tanggal akhir (format: YYYY-MM-DD)");
                endDateDialog.setContentText("Tanggal Akhir:");
                Optional<String> endResult = endDateDialog.showAndWait();

                if (endResult.isPresent()) {
                    LocalDate endDate = LocalDate.parse(endResult.get().trim());
                    ReportDialog reportDialog = new ReportDialog(transactionService, startDate, endDate,
                        "Laporan Penjualan Semua Kasir");
                    reportDialog.showAndWait();
                }
            }
        } catch (Exception e) {
            showAlert("Error", "Gagal membuat laporan: " + e.getMessage());
        }
    }

    private void clearFields(TextField... fields) {
        for (TextField field : fields) {
            field.clear();
        }
    }

    public void showWarehouseReport() {
        try {
            // Ask for date range for stock out report
            TextInputDialog startDateDialog = new TextInputDialog(LocalDate.now().minusDays(7).toString());
            startDateDialog.setTitle("Laporan Gudang");
            startDateDialog.setHeaderText("Masukkan tanggal mulai untuk laporan produk keluar (format: YYYY-MM-DD)");
            startDateDialog.setContentText("Tanggal Mulai:");
            Optional<String> startResult = startDateDialog.showAndWait();

            if (startResult.isPresent()) {
                LocalDate startDate = LocalDate.parse(startResult.get().trim());

                TextInputDialog endDateDialog = new TextInputDialog(LocalDate.now().toString());
                endDateDialog.setTitle("Laporan Gudang");
                endDateDialog.setHeaderText("Masukkan tanggal akhir untuk laporan produk keluar (format: YYYY-MM-DD)");
                endDateDialog.setContentText("Tanggal Akhir:");
                Optional<String> endResult = endDateDialog.showAndWait();

                if (endResult.isPresent()) {
                    LocalDate endDate = LocalDate.parse(endResult.get().trim());
                    LocalDateTime start = startDate.atStartOfDay();
                    LocalDateTime end = endDate.atTime(23, 59, 59);

                    WarehouseReportDialog warehouseDialog = new WarehouseReportDialog(
                        productService, cartService, start, end, "Laporan Gudang - " + startDate + " sampai " + endDate);
                    warehouseDialog.showAndWait();
                }
            }
        } catch (Exception e) {
            showAlert("Error", "Gagal membuat laporan gudang: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
