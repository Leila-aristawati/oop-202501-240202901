package com.upb.agripos.view;

import java.time.LocalDate;
import java.util.List;

import com.upb.agripos.model.SoldProduct;
import com.upb.agripos.service.TransactionService;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ReportDialog extends Stage {
    private final TransactionService transactionService;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final Integer userId; // null for all users, specific id for filtered

    public ReportDialog(TransactionService transactionService, LocalDate startDate, LocalDate endDate, String title) {
        this(transactionService, startDate, endDate, null, title);
    }

    public ReportDialog(TransactionService transactionService, LocalDate startDate, LocalDate endDate, Integer userId, String title) {
        this.transactionService = transactionService;
        this.startDate = startDate;
        this.endDate = endDate;
        this.userId = userId;

        try {
            initModality(Modality.APPLICATION_MODAL);
            setTitle(title);
            setScene(createScene());
            setWidth(1000);
            setHeight(700);
        } catch (Exception e) {
            System.err.println("Error creating ReportDialog: " + e.getMessage());
            e.printStackTrace();
            // Create a simple error dialog instead
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Gagal membuka laporan");
            alert.setContentText("Terjadi kesalahan: " + e.getMessage());
            alert.showAndWait();
            return;
        }
    }

    private Scene createScene() {
        TabPane tabPane = new TabPane();

        // Tab Ringkasan
        Tab summaryTab = new Tab("Ringkasan");
        summaryTab.setClosable(false);
        summaryTab.setContent(createSummaryTab());

        // Tab Tabel Penjualan
        Tab salesTableTab = new Tab("Tabel Penjualan");
        salesTableTab.setClosable(false);
        salesTableTab.setContent(createSalesTableTab());

        // Tab Produk Terlaris
        Tab topProductsTab = new Tab("Produk Terlaris");
        topProductsTab.setClosable(false);
        topProductsTab.setContent(createTopProductsTab());

        tabPane.getTabs().addAll(summaryTab, salesTableTab, topProductsTab);

        return new Scene(tabPane);
    }

    private VBox createSummaryTab() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        List<com.upb.agripos.model.Transaction> transactions = getFilteredTransactions();
        double totalSales = transactions.stream().mapToDouble(com.upb.agripos.model.Transaction::getTotalAmount).sum();
        double totalOmzet = transactionService.getTotalOmzet();
        int totalTransactions = transactions.size();

        Label titleLabel = new Label("📊 RINGKASAN LAPORAN PENJUALAN");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        Label periodLabel = new Label("Periode: " + startDate + " - " + endDate);
        periodLabel.setStyle("-fx-font-size: 14;");

        String filterText = userId != null ? " (Filtered by User ID: " + userId + ")" : " (All Users)";
        Label filterLabel = new Label(filterText);
        filterLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #666;");

        Label totalSalesLabel = new Label("💰 Total Penjualan Periode: Rp " + String.format("%,.0f", totalSales));
        totalSalesLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #2E7D32; -fx-font-weight: bold;");

        Label totalOmzetLabel = new Label("🏪 Total Omzet Keseluruhan: Rp " + String.format("%,.0f", totalOmzet));
        totalOmzetLabel.setStyle("-fx-font-size: 16; -fx-text-fill: #1976d2; -fx-font-weight: bold;");

        Label transactionCountLabel = new Label("📈 Jumlah Transaksi: " + totalTransactions);
        transactionCountLabel.setStyle("-fx-font-size: 14;");

        content.getChildren().addAll(titleLabel, periodLabel, filterLabel, totalSalesLabel, totalOmzetLabel, transactionCountLabel);

        return content;
    }

    private List<com.upb.agripos.model.Transaction> getFilteredTransactions() {
        List<com.upb.agripos.model.Transaction> transactions = transactionService.getTransactionsForReport(startDate, endDate);
        if (userId != null) {
            transactions = transactions.stream()
                .filter(t -> t.getUserId() == userId)
                .collect(java.util.stream.Collectors.toList());
        }
        return transactions;
    }

    private VBox createSalesTableTab() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label titleLabel = new Label("📋 TABEL PENJUALAN DETAIL");
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        TableView<com.upb.agripos.model.Transaction> table = new TableView<>();
        table.setPrefHeight(400);

        TableColumn<com.upb.agripos.model.Transaction, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<com.upb.agripos.model.Transaction, Double> totalCol = new TableColumn<>("Total (Rp)");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        totalCol.setPrefWidth(120);

        TableColumn<com.upb.agripos.model.Transaction, String> paymentCol = new TableColumn<>("Pembayaran");
        paymentCol.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        paymentCol.setPrefWidth(100);

        TableColumn<com.upb.agripos.model.Transaction, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(80);

        TableColumn<com.upb.agripos.model.Transaction, String> cashierCol = new TableColumn<>("Kasir");
        cashierCol.setCellValueFactory(new PropertyValueFactory<>("cashierName"));
        cashierCol.setPrefWidth(100);

        TableColumn<com.upb.agripos.model.Transaction, LocalDate> dateCol = new TableColumn<>("Tanggal");
        dateCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(
            cellData.getValue().getTransactionDate().toLocalDate()));
        dateCol.setPrefWidth(100);

        table.getColumns().addAll(idCol, totalCol, paymentCol, statusCol, cashierCol, dateCol);

        List<com.upb.agripos.model.Transaction> transactions = getFilteredTransactions();
        table.setItems(FXCollections.observableArrayList(transactions));

        content.getChildren().addAll(titleLabel, table);

        return content;
    }

    private VBox createTopProductsTab() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label titleLabel = new Label("🏆 PRODUK TERLARIS");
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        TableView<SoldProduct> table = new TableView<>();
        table.setPrefHeight(400);

        TableColumn<SoldProduct, String> codeCol = new TableColumn<>("Kode");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("productCode"));
        codeCol.setPrefWidth(100);

        TableColumn<SoldProduct, String> nameCol = new TableColumn<>("Nama Produk");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        TableColumn<SoldProduct, Integer> qtyCol = new TableColumn<>("Qty Terjual");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantitySold"));
        qtyCol.setPrefWidth(100);

        TableColumn<SoldProduct, Double> valueCol = new TableColumn<>("Total Nilai (Rp)");
        valueCol.setCellValueFactory(new PropertyValueFactory<>("totalValue"));
        valueCol.setPrefWidth(120);

        table.getColumns().addAll(codeCol, nameCol, qtyCol, valueCol);

        List<SoldProduct> topProducts = userId != null ?
            transactionService.getTopSellingProductsByUser(startDate, endDate, userId) :
            transactionService.getTopSellingProducts(startDate, endDate);
        table.setItems(FXCollections.observableArrayList(topProducts));

        content.getChildren().addAll(titleLabel, table);

        return content;
    }
}