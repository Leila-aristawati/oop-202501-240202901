package com.upb.agripos.view;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ProductFormView extends VBox {
    private TextField txtCode;
    private TextField txtName;
    private TextField txtPrice;
    private TextField txtStock;
    private Button btnAdd;

    public ProductFormView() {
        txtCode = new TextField();
        txtCode.setPromptText("Kode Produk");

        txtName = new TextField();
        txtName.setPromptText("Nama Produk");

        txtPrice = new TextField();
        txtPrice.setPromptText("Harga");

        txtStock = new TextField();
        txtStock.setPromptText("Stok");

        btnAdd = new Button("Tambah Produk");

        this.getChildren().addAll(txtCode, txtName, txtPrice, txtStock, btnAdd);
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

    public Button getBtnAdd() {
        return btnAdd;
    }
}