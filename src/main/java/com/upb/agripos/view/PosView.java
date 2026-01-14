package com.upb.agripos.view;

import com.upb.agripos.model.Product;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PosView extends VBox {
    private TableView<Product> productTable;
    private TextField txtCode, txtName, txtPrice, txtStock;
    private Button btnAddProduct, btnDeleteProduct, btnAddToCart, btnCheckout;
    private Button btnRefresh; // added refresh button
    private ListView<String> cartList;
    private Label totalLabel;

    public PosView() {
        // Product Form
        txtCode = new TextField();
        txtCode.setPromptText("Kode Produk");

        txtName = new TextField();
        txtName.setPromptText("Nama Produk");

        txtPrice = new TextField();
        txtPrice.setPromptText("Harga");

        txtStock = new TextField();
        txtStock.setPromptText("Stok");

        btnAddProduct = new Button("Tambah Produk");
        btnDeleteProduct = new Button("Hapus Produk");
        btnRefresh = new Button("Refresh"); // new button

        HBox formBox = new HBox(10, txtCode, txtName, txtPrice, txtStock, btnAddProduct, btnDeleteProduct, btnRefresh);

        // Product Table
        productTable = new TableView<>();
        TableColumn<Product, String> codeCol = new TableColumn<>("Kode");
        codeCol.setCellValueFactory(cell -> cell.getValue().codeProperty());

        TableColumn<Product, String> nameCol = new TableColumn<>("Nama");
        nameCol.setCellValueFactory(cell -> cell.getValue().nameProperty());

        TableColumn<Product, Double> priceCol = new TableColumn<>("Harga");
        priceCol.setCellValueFactory(cell -> cell.getValue().priceProperty().asObject());

        TableColumn<Product, Integer> stockCol = new TableColumn<>("Stok");
        stockCol.setCellValueFactory(cell -> cell.getValue().stockProperty().asObject());

        productTable.getColumns().addAll(codeCol, nameCol, priceCol, stockCol);
        productTable.setPrefHeight(200);

        btnAddToCart = new Button("Tambah ke Keranjang");

        // Cart
        cartList = new ListView<>();
        cartList.setPrefHeight(150);
        totalLabel = new Label("Total: 0.0");
        btnCheckout = new Button("Checkout");

        VBox cartBox = new VBox(10, new Label("Keranjang:"), cartList, totalLabel, btnCheckout);

        // Layout
        HBox mainBox = new HBox(20, new VBox(10, formBox, productTable, btnAddToCart), cartBox);
        this.getChildren().addAll(mainBox);
        this.setSpacing(10);
    }

    public TableView<Product> getProductTable() {
        return productTable;
    }

    public TextField getTxtCode() {
        return txtCode;
    }

    public TextField getTxtName() {
        return txtName;
    }

    public TextField getTxtPrice() {
        return txtPrice;
    }

    public TextField getTxtStock() {
        return txtStock;
    }

    public Button getBtnAddProduct() {
        return btnAddProduct;
    }

    public Button getBtnDeleteProduct() {
        return btnDeleteProduct;
    }

    public Button getBtnAddToCart() {
        return btnAddToCart;
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

    public Button getBtnRefresh() { // getter for refresh
        return btnRefresh;
    }
}
