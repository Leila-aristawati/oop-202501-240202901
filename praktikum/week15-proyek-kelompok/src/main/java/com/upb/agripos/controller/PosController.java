package com.upb.agripos.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.upb.agripos.model.CartItem;
import com.upb.agripos.model.Product;
import com.upb.agripos.model.Transaction;
import com.upb.agripos.model.payment.PaymentMethod;
import com.upb.agripos.service.CartService;
import com.upb.agripos.service.ProductService;
import com.upb.agripos.service.TransactionService;
import com.upb.agripos.service.UserService;
import com.upb.agripos.view.PaymentDialog;
import com.upb.agripos.view.ReceiptView;
import com.upb.agripos.view.ReportDialog;
import com.upb.agripos.view.TransactionDetailView;
import com.upb.agripos.view.WarehouseReportDialog;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;

public class PosController {
    private final ProductService productService;
    private final CartService cartService;
    private final UserService userService;
    private final TransactionService transactionService;

    public PosController(ProductService productService, CartService cartService, UserService userService, TransactionService transactionService) {
        this.productService = productService;
        this.cartService = cartService;
        this.userService = userService;
        this.transactionService = transactionService;
    }

    // Show transaction detail dialog for a given transaction
    public void showTransactionDetail(Transaction transaction) {
        try {
            if (transaction == null) return;
            var items = transactionService.getCartItemsByCartId(transaction.getCartId());
            TransactionDetailView.show(transaction, items);
        } catch (Exception e) {
            showAlert("Error", "Gagal menampilkan detail transaksi: " + e.getMessage());
        }
    }

    public void addProduct(TextField txtCode, TextField txtName, TextField txtPrice, TextField txtStock, TableView<Product> tableView) {
        // only gudang can add products
        if (!isCurrentUserGudang()) {
            showAlert("Unauthorized", "Akses ditolak. Hanya user GUDANG yang dapat menambah produk.");
            return;
        }
        try {
            String code = txtCode.getText().trim();
            String name = txtName.getText().trim();
            double price = Double.parseDouble(txtPrice.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());

            Product product = new Product(code, name, price, stock);
            productService.insert(product);
            // reload safely into the table view (handles FilteredList wrappers)
            loadProducts(tableView);
            clearFields(txtCode, txtName, txtPrice, txtStock);
        } catch (NumberFormatException e) {
            showAlert("Error", "Input tidak valid: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            showAlert("Error", e.getMessage());
        } catch (Exception e) {
            showAlert("Error", "Gagal menambah produk: " + e.getMessage());
        }
    }

    public void deleteProduct(TableView<Product> tableView) {
        // only gudang can delete products
        if (!isCurrentUserGudang()) {
            showAlert("Unauthorized", "Akses ditolak. Hanya user GUDANG yang dapat menghapus produk.");
            return;
        }

        Product selectedProduct = tableView.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            showAlert("Error", "Pilih produk untuk dihapus");
            return;
        }

        // Confirm deletion
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Hapus");
        confirm.setHeaderText("Hapus Produk");
        confirm.setContentText("Anda yakin ingin menghapus produk '" + selectedProduct.getName() + "' (Kode: " + selectedProduct.getCode() + ")?");

        var res = confirm.showAndWait();
        if (res.isEmpty() || res.get() != ButtonType.OK) return;

        try {
            productService.delete(selectedProduct.getCode());
            loadProducts(tableView);
            showAlert("Info", "Produk berhasil dihapus");
        } catch (Exception e) {
            // Try to detect foreign key / integrity constraint errors for a friendlier message
            Throwable cause = e;
            boolean fkRelated = false;
            while (cause != null) {
                String m = cause.getMessage() != null ? cause.getMessage().toLowerCase() : "";
                if (cause instanceof java.sql.SQLIntegrityConstraintViolationException || m.contains("foreign key") || m.contains("constraint") || m.contains("referenced")) {
                    fkRelated = true;
                    break;
                }
                cause = cause.getCause();
            }
            if (fkRelated) {
                showAlert("Error", "Tidak dapat menghapus produk karena sudah direferensikan oleh transaksi/keranjang. Pertimbangkan untuk menonaktifkan produk atau menghapus histori terkait.");
            } else {
                showAlert("Error", "Gagal menghapus produk: " + e.getMessage());
            }
        }
    }

    public void loadProducts(TableView<Product> tableView) {
        try {
            var products = productService.findAll();
            ObservableList<Product> items = tableView.getItems();
            if (items instanceof FilteredList) {
                // Update underlying source list for FilteredList
                ObservableList<Product> src = (ObservableList<Product>) ((FilteredList<Product>) items).getSource();
                src.setAll(products);
            } else {
                items.setAll(products);
            }
        } catch (Exception e) {
            // Log and show a helpful message, then provide fallback sample data so UI is not empty
            e.printStackTrace();
            showAlert("Error", "Gagal memuat produk dari database: " + (e.getMessage() != null ? e.getMessage() : "(lihat console)") + ". Menampilkan contoh produk.");
            try {
                var sample = java.util.List.of(
                    new Product("P001", "Contoh Beras 5kg", 75000.0, 10),
                    new Product("P002", "Contoh Gula 1kg", 12000.0, 25),
                    new Product("P003", "Contoh Minyak 1L", 18000.0, 15)
                );
                ObservableList<Product> items = tableView.getItems();
                if (items instanceof FilteredList) {
                    ObservableList<Product> src = (ObservableList<Product>) ((FilteredList<Product>) items).getSource();
                    src.setAll(sample);
                } else {
                    items.setAll(sample);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void increaseStock(TableView<Product> tableView) {
        if (!isCurrentUserGudang()) {
            showAlert("Unauthorized", "Akses ditolak. Hanya user GUDANG yang dapat mengubah stok.");
            return;
        }
        Product selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Pilih produk untuk ditambah stok");
            return;
        }
        TextInputDialog dlg = new TextInputDialog("1");
        dlg.setTitle("Tambah Stok");
        dlg.setHeaderText("Masukkan jumlah stok yang ditambahkan untuk " + selected.getName());
        dlg.setContentText("Jumlah:");
        dlg.showAndWait().ifPresent(s -> {
            try {
                int qty = Integer.parseInt(s.trim());
                if (qty <= 0) throw new IllegalArgumentException("Jumlah harus positif");
                int newStock = selected.getStock() + qty;
                productService.updateStock(selected.getCode(), newStock);
                loadProducts(tableView);
            } catch (NumberFormatException ex) {
                showAlert("Error", "Jumlah harus angka");
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
            }
        });
    }

    public void decreaseStock(TableView<Product> tableView) {
        if (!isCurrentUserGudang()) {
            showAlert("Unauthorized", "Akses ditolak. Hanya user GUDANG yang dapat mengubah stok.");
            return;
        }
        Product selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Pilih produk untuk dikurangi stok");
            return;
        }
        TextInputDialog dlg = new TextInputDialog("1");
        dlg.setTitle("Kurangi Stok");
        dlg.setHeaderText("Masukkan jumlah stok yang dikurangi untuk " + selected.getName());
        dlg.setContentText("Jumlah:");
        dlg.showAndWait().ifPresent(s -> {
            try {
                int qty = Integer.parseInt(s.trim());
                if (qty <= 0) throw new IllegalArgumentException("Jumlah harus positif");
                productService.decreaseStock(selected.getCode(), qty);
                loadProducts(tableView);
            } catch (NumberFormatException ex) {
                showAlert("Error", "Jumlah harus angka");
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
            }
        });
    }

    private boolean isCurrentUserGudang() {
        try {
            var u = userService.getCurrentUser();
            return u != null && "gudang".equalsIgnoreCase(u.getRole());
        } catch (Exception e) {
            return false;
        }
    }

    public void addToCart(TableView<Product> productTable, TableView<CartItem> cartTable, Label totalLabel) {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            TextInputDialog dialog = new TextInputDialog("1");
            dialog.setTitle("Tambah ke Keranjang");
            dialog.setHeaderText("Masukkan jumlah untuk " + selectedProduct.getName());
            dialog.setContentText("Jumlah:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(quantityStr -> {
                try {
                    int quantity = Integer.parseInt(quantityStr.trim());
                    cartService.addItem(selectedProduct.getCode(), quantity);
                    updateCartDisplay(cartTable, totalLabel);
                } catch (NumberFormatException e) {
                    showAlert("Error", "Jumlah harus angka");
                } catch (IllegalArgumentException e) {
                    showAlert("Error", e.getMessage());
                } catch (Exception e) {
                    showAlert("Error", "Gagal menambah ke keranjang: " + e.getMessage());
                }
            });
        } else {
            showAlert("Error", "Pilih produk untuk ditambah ke keranjang");
        }
    }

    // Modified checkout signature: add historyTable parameter so we can refresh kasir history after success
    public void checkout(TableView<Product> productTable, TableView<CartItem> cartTable, Label totalLabel, TableView<Transaction> historyTable) {
        if (cartService.getCart().getItems().isEmpty()) {
            showAlert("Error", "Keranjang kosong");
            return;
        }
        try {
            // Get current user
            var currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                showAlert("Error", "User tidak ditemukan");
                return;
            }

            // Show payment dialog
            double totalAmount = cartService.getTotalPrice();
            PaymentDialog paymentDialog = new PaymentDialog(totalAmount);

            if (!paymentDialog.isConfirmed()) {
                showAlert("Info", "Pembayaran dibatalkan");
                return;
            }

            PaymentMethod paymentMethod = paymentDialog.getSelectedPaymentMethod();
            double paidAmount = paymentDialog.getPaidAmount();
            double change = paidAmount - totalAmount;

            // Decrease stock for each item in cart
            for (var item : cartService.getCart().getItems()) {
                productService.decreaseStock(item.getProduct().getCode(), item.getQuantity());
            }

            // Save cart to database
            int cartId = cartService.saveCart(currentUser.getId());
            if (cartId == -1) {
                throw new Exception("Gagal menyimpan keranjang");
            }

            // Save transaction with payment details
            String paymentMethodStr = paymentMethod.name();
            Transaction transaction = new Transaction(
                0, 
                cartId, 
                currentUser.getId(), 
                totalAmount, 
                paymentMethodStr, 
                "SUCCESS", 
                LocalDateTime.now(),
                paidAmount,
                change,
                currentUser.getFullName()
            );
            transactionService.saveTransaction(transaction);

            // Generate receipt text with payment details
            String receipt = generateReceipt(transaction);
            // Show receipt dialog (printable)
            ReceiptView.showReceipt(receipt);

            // Clear cart after checkout
            showAlert("Info", "Checkout berhasil. Total: " + String.format("Rp %,.0f", totalAmount));
            cartService.clearCart();
            updateCartDisplay(cartTable, totalLabel);

            // Refresh product table after stock changes (handle FilteredList safely)
            ObservableList<Product> productItems = productTable.getItems();
            var fresh = productService.findAll();
            if (productItems instanceof FilteredList) {
                ObservableList<Product> src = (ObservableList<Product>) ((FilteredList<Product>) productItems).getSource();
                src.setAll(fresh);
            } else {
                productItems.setAll(fresh);
            }

            // --- NEW: refresh kasir history table so the UI updates immediately for the cashier ---
            try {
                if (historyTable != null) {
                    loadKasirHistory(historyTable, currentUser.getId());
                }
            } catch (Exception ex) {
                // Don't block checkout success for history refresh failures; log and continue
                ex.printStackTrace();
            }
        } catch (Exception e) {
            // Log full stacktrace for debugging
            e.printStackTrace();
            // Guard against null message (some exceptions have null message)
            String msg = e.getMessage() != null ? e.getMessage() : e.toString();
            showAlert("Error", "Gagal checkout: " + msg);
        }
    }

    private String generateReceipt(Transaction transaction) {
        StringBuilder sb = new StringBuilder();
        sb.append("================================\n");
        sb.append("\tAGRI-POS RECEIPT\n");
        sb.append("================================\n");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        sb.append("Tanggal: ").append(transaction.getTransactionDate().format(fmt)).append("\n");
        sb.append("Kasir: ").append(transaction.getCashierName()).append("\n");
        sb.append("--------------------------------\n");
        sb.append(String.format("%-20s %5s %10s %10s\n", "Item", "Qty", "Harga", "Subtotal"));
        sb.append("--------------------------------\n");
        for (var item : cartService.getCart().getItems()) {
            String name = item.getProduct().getName();
            int qty = item.getQuantity();
            double price = item.getProduct().getPrice();
            double subtotal = item.getTotalPrice();
            sb.append(String.format("%-20s %5d %10.0f %10.0f\n", name, qty, price, subtotal));
        }
        sb.append("--------------------------------\n");
        sb.append(String.format("%-20s %26.0f\n", "Total:", transaction.getTotalAmount()));
        sb.append("\n");
        sb.append(String.format("%-20s %26s\n", "Metode Pembayaran:", transaction.getPaymentMethod()));

        if (PaymentMethod.CASH.name().equals(transaction.getPaymentMethod())) {
            sb.append(String.format("%-20s %26.0f\n", "Jumlah Dibayar:", transaction.getPaidAmount()));
            sb.append(String.format("%-20s %26.0f\n", "Kembalian:", transaction.getChange()));
        }

        sb.append("\n\n");
        sb.append("\tTerima Kasih Atas Belanja Anda\n");
        sb.append("================================\n");
        return sb.toString();
    }

    private String generateReceipt() {
        // Kept for backward compatibility
        StringBuilder sb = new StringBuilder();
        sb.append("================================\n");
        sb.append("\tAGRI-POS RECEIPT\n");
        sb.append("================================\n");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        sb.append("Tanggal: ").append(LocalDateTime.now().format(fmt)).append("\n");
        sb.append("--------------------------------\n");
        sb.append(String.format("%-20s %5s %10s %10s\n", "Item", "Qty", "Harga", "Subtotal"));
        sb.append("--------------------------------\n");
        for (var item : cartService.getCart().getItems()) {
            String name = item.getProduct().getName();
            int qty = item.getQuantity();
            double price = item.getProduct().getPrice();
            double subtotal = item.getTotalPrice();
            sb.append(String.format("%-20s %5d %10.0f %10.0f\n", name, qty, price, subtotal));
        }
        sb.append("--------------------------------\n");
        sb.append(String.format("%-20s %26.0f\n", "Total:", cartService.getTotalPrice()));
        sb.append("\n");
        sb.append(String.format("%-20s %26s\n", "Metode Pembayaran:", "TUNAI"));
        sb.append(String.format("%-20s %26.0f\n", "Jumlah Dibayar:", cartService.getTotalPrice()));
        sb.append(String.format("%-20s %26.0f\n", "Kembalian:", 0.0));
        sb.append("\n\n");
        sb.append("\tTerima Kasih Atas Belanja Anda\n");
        sb.append("================================\n");
        return sb.toString();
    }

    public void increaseQuantity(String productCode, TableView<CartItem> cartTable, Label totalLabel) {
        try {
            var item = cartService.getCart().getItems().stream()
                .filter(i -> i.getProduct().getCode().equals(productCode))
                .findFirst().orElse(null);
            if (item != null) {
                cartService.updateQuantity(productCode, item.getQuantity() + 1);
                updateCartDisplay(cartTable, totalLabel);
            }
        } catch (Exception e) {
            showAlert("Error", "Gagal menambah quantity: " + e.getMessage());
        }
    }

    public void decreaseQuantity(String productCode, TableView<CartItem> cartTable, Label totalLabel) {
        try {
            var item = cartService.getCart().getItems().stream()
                .filter(i -> i.getProduct().getCode().equals(productCode))
                .findFirst().orElse(null);
            if (item != null) {
                int newQty = item.getQuantity() - 1;
                if (newQty <= 0) {
                    cartService.removeItem(productCode);
                } else {
                    cartService.updateQuantity(productCode, newQty);
                }
                updateCartDisplay(cartTable, totalLabel);
            }
        } catch (Exception e) {
            showAlert("Error", "Gagal mengurangi quantity: " + e.getMessage());
        }
    }

    // New: clear entire cart with confirmation
    public void clearCart(TableView<CartItem> cartTable, Label totalLabel) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Hapus Semua");
        confirm.setHeaderText("Hapus Semua Item dari Keranjang");
        confirm.setContentText("Anda yakin ingin menghapus semua item dari keranjang?");
        var res = confirm.showAndWait();
        if (res.isEmpty() || res.get() != ButtonType.OK) return;
        try {
            cartService.clearCart();
            updateCartDisplay(cartTable, totalLabel);
        } catch (Exception e) {
            showAlert("Error", "Gagal menghapus semua item: " + e.getMessage());
        }
    }

    private void updateCartDisplay(TableView<CartItem> cartTable, Label totalLabel) {
        javafx.collections.ObservableList<CartItem> cartItems = cartTable.getItems();
        var fresh = cartService.getCart().getItems();
        if (cartItems instanceof FilteredList) {
            javafx.collections.ObservableList<CartItem> src = (javafx.collections.ObservableList<CartItem>) ((FilteredList<CartItem>) cartItems).getSource();
            src.setAll(fresh);
        } else {
            cartItems.setAll(fresh);
        }
        totalLabel.setText("Total: " + cartService.getTotalPrice());
    }

    private void clearFields(TextField... fields) {
        for (TextField field : fields) {
            field.clear();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void loadKasirHistory(TableView<Transaction> historyTable, int userId) {
        try {
            javafx.collections.ObservableList<Transaction> historyItems = historyTable.getItems();
            var fresh = transactionService.getTransactionsByUserId(userId);
            if (historyItems instanceof FilteredList) {
                javafx.collections.ObservableList<Transaction> src = (javafx.collections.ObservableList<Transaction>) ((FilteredList<Transaction>) historyItems).getSource();
                src.setAll(fresh);
            } else {
                historyItems.setAll(fresh);
            }
        } catch (Exception e) {
            showAlert("Error", "Gagal memuat history penjualan: " + e.getMessage());
        }
    }

    public void showSalesReport() {
        showCashierSalesReport();
    }

    public void showCashierSalesReport() {
        try {
            var currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                showAlert("Error", "User tidak ditemukan");
                return;
            }

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
                        "Laporan Penjualan Kasir " + currentUser.getFullName());
                    reportDialog.showAndWait();
                }
            }
        } catch (Exception e) {
            showAlert("Error", "Gagal membuat laporan: " + e.getMessage());
        }
    }

    public void showStockInReport() {
        try {
            LocalDateTime now = LocalDateTime.now();
            WarehouseReportDialog warehouseDialog = new WarehouseReportDialog(
                productService, cartService, now.minusDays(30), now, "Laporan Gudang - Inventaris Saat Ini");
            warehouseDialog.showAndWait();
        } catch (Exception e) {
            showAlert("Error", "Gagal membuat laporan produk masuk: " + e.getMessage());
        }
    }

    public void showStockOutReport() {
        try {
            // Ask for date range
            TextInputDialog startDateDialog = new TextInputDialog(LocalDate.now().minusDays(7).toString());
            startDateDialog.setTitle("Laporan Produk Keluar");
            startDateDialog.setHeaderText("Masukkan tanggal mulai (format: YYYY-MM-DD)");
            startDateDialog.setContentText("Tanggal Mulai:");
            Optional<String> startResult = startDateDialog.showAndWait();

            if (startResult.isPresent()) {
                java.time.LocalDate startDate = java.time.LocalDate.parse(startResult.get().trim());

                TextInputDialog endDateDialog = new TextInputDialog(LocalDate.now().toString());
                endDateDialog.setTitle("Laporan Produk Keluar");
                endDateDialog.setHeaderText("Masukkan tanggal akhir (format: YYYY-MM-DD)");
                endDateDialog.setContentText("Tanggal Akhir:");
                Optional<String> endResult = endDateDialog.showAndWait();

                if (endResult.isPresent()) {
                    java.time.LocalDate endDate = java.time.LocalDate.parse(endResult.get().trim());
                    java.time.LocalDateTime start = startDate.atStartOfDay();
                    java.time.LocalDateTime end = endDate.atTime(23,59,59);

                    WarehouseReportDialog warehouseDialog = new WarehouseReportDialog(
                        productService, cartService, start, end, "Laporan Gudang - " + startDate + " sampai " + endDate);
                    warehouseDialog.showAndWait();
                }
            }
        } catch (Exception e) {
            showAlert("Error", "Gagal membuat laporan produk keluar: " + e.getMessage());
        }
    }
}
