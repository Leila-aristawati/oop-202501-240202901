package com.upb.agripos.view;

import com.upb.agripos.model.Product;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class PosView extends VBox {
    private TableView<Product> productTable;
    private Button btnAddToCart, btnCheckout;
    private ListView<String> cartList;
    private Label totalLabel;
    private Label userInfoLabel;

    public PosView() {
        // ========== MAIN STYLING ==========
        this.setPadding(new Insets(20));
        this.setSpacing(15);
        this.setStyle("-fx-background-color: #f0f0f0;");

        // ========== HEADER ==========
        VBox headerBox = createHeaderBox();

        // ========== USER INFO ==========
        userInfoLabel = new Label("👤 User: -");
        userInfoLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");

        // ========== PRODUCT TABLE SECTION ==========
        VBox tableSection = createTableSection();

        // ========== MAIN CONTENT BOX ==========
        HBox contentBox = new HBox(20);
        contentBox.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-radius: 5;");

        VBox leftPanel = new VBox(15, tableSection);
        VBox.setVgrow(tableSection, Priority.ALWAYS);

        VBox rightPanel = createCartPanel();
        HBox.setHgrow(leftPanel, Priority.ALWAYS);

        contentBox.getChildren().addAll(leftPanel, rightPanel);

        // ========== ASSEMBLE MAIN LAYOUT ==========
        this.getChildren().addAll(headerBox, userInfoLabel, contentBox);
    }

    private VBox createHeaderBox() {
        VBox headerBox = new VBox(5);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("🛒 SISTEM KASIR AGRIPOS");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");

        Label subtitleLabel = new Label("Kelola transaksi penjualan dengan mudah");
        subtitleLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #666666;");

        headerBox.getChildren().addAll(titleLabel, subtitleLabel);
        return headerBox;
    }

    private VBox createTableSection() {
        VBox tableSection = new VBox(10);
        tableSection.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5; " +
                "-fx-background-color: #fafafa; -fx-padding: 12;");

        Label tableLabel = new Label("📊 Daftar Produk Tersedia");
        tableLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");

        // Product Table
        productTable = new TableView<>();
        productTable.setStyle("-fx-font-size: 11; -fx-padding: 5;");

        TableColumn<Product, String> codeCol = new TableColumn<>("Kode");
        codeCol.setCellValueFactory(cell -> cell.getValue().codeProperty());
        codeCol.setPrefWidth(80);

        TableColumn<Product, String> nameCol = new TableColumn<>("Nama Produk");
        nameCol.setCellValueFactory(cell -> cell.getValue().nameProperty());
        nameCol.setPrefWidth(150);

        TableColumn<Product, Double> priceCol = new TableColumn<>("Harga");
        priceCol.setCellValueFactory(cell -> cell.getValue().priceProperty().asObject());
        priceCol.setPrefWidth(100);

        TableColumn<Product, Integer> stockCol = new TableColumn<>("Stok");
        stockCol.setCellValueFactory(cell -> cell.getValue().stockProperty().asObject());
        stockCol.setPrefWidth(80);

        productTable.getColumns().addAll(codeCol, nameCol, priceCol, stockCol);
        productTable.setPrefHeight(250);

        btnAddToCart = createStyledButton("🛒 Tambah ke Keranjang", "#2E7D32");

        tableSection.getChildren().addAll(tableLabel, productTable, btnAddToCart);
        VBox.setVgrow(productTable, Priority.ALWAYS);

        return tableSection;
    }

    private VBox createCartPanel() {
        VBox cartPanel = new VBox(12);
        cartPanel.setStyle("-fx-border-color: #2E7D32; -fx-border-radius: 5; " +
                "-fx-background-color: white; -fx-padding: 15; -fx-border-width: 2;");
        cartPanel.setPrefWidth(280);

        Label cartTitleLabel = new Label("🛍️ KERANJANG BELANJA");
        cartTitleLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");

        Separator separator1 = new Separator();
        separator1.setStyle("-fx-padding: 0;");

        // Cart List
        cartList = new ListView<>();
        cartList.setStyle("-fx-font-size: 11; -fx-padding: 5;");
        cartList.setPrefHeight(200);

        Separator separator2 = new Separator();

        // Total
        totalLabel = new Label("💰 Total: Rp 0");
        totalLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #2E7D32; -fx-padding: 10;");
        totalLabel.setAlignment(Pos.CENTER_RIGHT);

        // Checkout Button
        btnCheckout = createStyledButton("✅ CHECKOUT", "#1976d2");
        btnCheckout.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-padding: 12; " +
                "-fx-background-color: #1976d2; -fx-text-fill: white; -fx-cursor: hand; " +
                "-fx-border-radius: 5;");
        btnCheckout.setPrefHeight(45);

        // Clear Cart Button
        Button btnClearCart = createStyledButton("🗑️ Hapus Keranjang", "#d32f2f");
        btnClearCart.setStyle("-fx-font-size: 12; -fx-font-weight: bold; -fx-padding: 12; " +
                "-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-cursor: hand; " +
                "-fx-border-radius: 5;");
        btnClearCart.setPrefHeight(45);

        cartPanel.getChildren().addAll(
            cartTitleLabel,
            separator1,
            cartList,
            separator2,
            totalLabel,
            btnCheckout,
            btnClearCart
        );

        VBox.setVgrow(cartList, Priority.ALWAYS);
        return cartPanel;
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setPrefHeight(35);
        button.setStyle("-fx-font-size: 11; -fx-font-weight: bold; -fx-padding: 8; " +
                "-fx-background-color: " + color + "; -fx-text-fill: white; -fx-cursor: hand; " +
                "-fx-border-radius: 3;");

        // Hover effect
        button.setOnMouseEntered(e -> {
            button.setStyle("-fx-font-size: 11; -fx-font-weight: bold; -fx-padding: 8; " +
                    "-fx-background-color: " + darkenColor(color) + "; -fx-text-fill: white; " +
                    "-fx-cursor: hand; -fx-border-radius: 3;");
        });

        button.setOnMouseExited(e -> {
            button.setStyle("-fx-font-size: 11; -fx-font-weight: bold; -fx-padding: 8; " +
                    "-fx-background-color: " + color + "; -fx-text-fill: white; -fx-cursor: hand; " +
                    "-fx-border-radius: 3;");
        });

        return button;
    }

    private String darkenColor(String color) {
        return switch (color) {
            case "#2E7D32" -> "#1B5E20";
            case "#d32f2f" -> "#b71c1c";
            case "#1976d2" -> "#0d47a1";
            case "#f57c00" -> "#e65100";
            case "#555555" -> "#212121";
            default -> color;
        };
    }

    // ========== GETTERS ==========
    public TableView<Product> getProductTable() {
        return productTable;
    }

    public Button getBtnAddToCart() {
        return btnAddToCart;
    }

    // New: button to clear entire cart
    private Button btnClearCart;

    public Button getBtnClearCart() {
        return btnClearCart;
    }

    public Button getBtnCheckout() {
        return btnCheckout;
    }

    public ListView<String> getCartList() {
        return cartList;
    }

    public Label getTotalLabel() {
        return totalLabel;
    }

    public Label getUserInfoLabel() {
        return userInfoLabel;
    }
}
