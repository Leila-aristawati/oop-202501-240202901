package com.upb.agripos.view;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.upb.agripos.model.Product;
import com.upb.agripos.model.SoldProduct;
import com.upb.agripos.service.CartService;
import com.upb.agripos.service.ProductService;

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

public class WarehouseReportDialog extends Stage {
    private final ProductService productService;
    private final CartService cartService;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    public WarehouseReportDialog(ProductService productService, CartService cartService,
                                LocalDateTime startDate, LocalDateTime endDate, String title) {
        this.productService = productService;
        this.cartService = cartService;
        this.startDate = startDate;
        this.endDate = endDate;

        try {
            initModality(Modality.APPLICATION_MODAL);
            setTitle(title);
            setScene(createScene());
            setWidth(1000);
            setHeight(700);
        } catch (Exception e) {
            System.err.println("Error creating WarehouseReportDialog: " + e.getMessage());
            e.printStackTrace();
            // Create a simple error dialog instead
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Gagal membuka laporan gudang");
            alert.setContentText("Terjadi kesalahan: " + e.getMessage());
            alert.showAndWait();
            return;
        }
    }

    private Scene createScene() {
        TabPane tabPane = new TabPane();

        // Tab Inventaris Produk (Stock In)
        Tab inventoryTab = new Tab("📦 Inventaris Produk");
        inventoryTab.setClosable(false);
        inventoryTab.setContent(createInventoryTab());

        // Tab Produk Keluar (Stock Out)
        Tab stockOutTab = new Tab("📤 Produk Keluar");
        stockOutTab.setClosable(false);
        stockOutTab.setContent(createStockOutTab());

        tabPane.getTabs().addAll(inventoryTab, stockOutTab);

        return new Scene(tabPane);
    }

    private VBox createInventoryTab() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        try {
            List<Product> products = productService.findAll();
            int totalProducts = products.size();
            int totalStock = products.stream().mapToInt(Product::getStock).sum();
            double totalValue = products.stream().mapToDouble(p -> p.getPrice() * p.getStock()).sum();

            Label titleLabel = new Label("📦 LAPORAN INVENTARIS PRODUK");
            titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

            Label summaryLabel = new Label(String.format(
                "Total Jenis Produk: %d | Total Stok: %d unit | Total Nilai: Rp %,d",
                totalProducts, totalStock, (long) totalValue));
            summaryLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #1976d2;");

            // Table for products
            TableView<Product> table = new TableView<>();
            table.setItems(FXCollections.observableArrayList(products));
            table.setPrefHeight(500);

            TableColumn<Product, String> codeCol = new TableColumn<>("Kode");
            codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));
            codeCol.setPrefWidth(100);

            TableColumn<Product, String> nameCol = new TableColumn<>("Nama Produk");
            nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            nameCol.setPrefWidth(200);

            TableColumn<Product, Integer> stockCol = new TableColumn<>("Stok");
            stockCol.setCellValueFactory(new PropertyValueFactory<>("stock"));
            stockCol.setPrefWidth(80);

            TableColumn<Product, Double> priceCol = new TableColumn<>("Harga");
            priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
            priceCol.setPrefWidth(100);
            priceCol.setCellFactory(col -> new javafx.scene.control.TableCell<Product, Double>() {
                @Override
                protected void updateItem(Double price, boolean empty) {
                    super.updateItem(price, empty);
                    if (empty || price == null) {
                        setText(null);
                    } else {
                        setText("Rp " + String.format("%,.0f", price));
                    }
                }
            });

            TableColumn<Product, Double> totalValueCol = new TableColumn<>("Total Nilai");
            totalValueCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(
                cellData.getValue().getPrice() * cellData.getValue().getStock()).asObject());
            totalValueCol.setPrefWidth(120);
            totalValueCol.setCellFactory(col -> new javafx.scene.control.TableCell<Product, Double>() {
                @Override
                protected void updateItem(Double value, boolean empty) {
                    super.updateItem(value, empty);
                    if (empty || value == null) {
                        setText(null);
                    } else {
                        setText("Rp " + String.format("%,.0f", value));
                    }
                }
            });

            table.getColumns().addAll(codeCol, nameCol, stockCol, priceCol, totalValueCol);

            content.getChildren().addAll(titleLabel, summaryLabel, table);

        } catch (Exception e) {
            Label errorLabel = new Label("❌ Error loading inventory data: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red;");
            content.getChildren().add(errorLabel);
        }

        return content;
    }

    private VBox createStockOutTab() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        try {
            List<SoldProduct> soldProducts = cartService.getSoldProductsByDateRange(startDate, endDate);
            int totalProductsSold = soldProducts.stream().mapToInt(SoldProduct::getQuantitySold).sum();
            double totalSalesValue = soldProducts.stream().mapToDouble(SoldProduct::getTotalValue).sum();

            Label titleLabel = new Label("📤 LAPORAN PRODUK KELUAR");
            titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

            Label periodLabel = new Label("Periode: " + startDate.toLocalDate() + " - " + endDate.toLocalDate());
            periodLabel.setStyle("-fx-font-size: 14;");

            Label summaryLabel = new Label(String.format(
                "Total Jenis Produk Terjual: %d | Total Unit Terjual: %d | Total Nilai: Rp %,d",
                soldProducts.size(), totalProductsSold, (long) totalSalesValue));
            summaryLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #d32f2f;");

            // Table for sold products
            TableView<SoldProduct> table = new TableView<>();
            table.setItems(FXCollections.observableArrayList(soldProducts));
            table.setPrefHeight(500);

            TableColumn<SoldProduct, String> codeCol = new TableColumn<>("Kode Produk");
            codeCol.setCellValueFactory(new PropertyValueFactory<>("productCode"));
            codeCol.setPrefWidth(120);

            TableColumn<SoldProduct, String> nameCol = new TableColumn<>("Nama Produk");
            nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            nameCol.setPrefWidth(200);

            TableColumn<SoldProduct, Integer> quantityCol = new TableColumn<>("Jumlah Terjual");
            quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantitySold"));
            quantityCol.setPrefWidth(120);

            TableColumn<SoldProduct, Double> totalValueCol = new TableColumn<>("Total Nilai");
            totalValueCol.setCellValueFactory(new PropertyValueFactory<>("totalValue"));
            totalValueCol.setPrefWidth(120);
            totalValueCol.setCellFactory(col -> new javafx.scene.control.TableCell<SoldProduct, Double>() {
                @Override
                protected void updateItem(Double value, boolean empty) {
                    super.updateItem(value, empty);
                    if (empty || value == null) {
                        setText(null);
                    } else {
                        setText("Rp " + String.format("%,.0f", value));
                    }
                }
            });

            table.getColumns().addAll(codeCol, nameCol, quantityCol, totalValueCol);

            content.getChildren().addAll(titleLabel, periodLabel, summaryLabel, table);

        } catch (Exception e) {
            Label errorLabel = new Label("❌ Error loading stock out data: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red;");
            content.getChildren().add(errorLabel);
        }

        return content;
    }
}