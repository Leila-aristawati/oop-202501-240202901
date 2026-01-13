package com.upb.agripos.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class ProductFormView extends VBox {
    private TextField txtCode = new TextField();
    private TextField txtName = new TextField();
    private TextField txtPrice = new TextField();
    private TextField txtStock = new TextField();
    private Button btnAdd = new Button("Tambah Produk");
    private ListView<String> listView = new ListView<>();

    public ProductFormView() {
        setPadding(new Insets(20));
        setSpacing(10);

        // Form Layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Kode Produk:"), 0, 0);
        grid.add(txtCode, 1, 0);

        grid.add(new Label("Nama Produk:"), 0, 1);
        grid.add(txtName, 1, 1);

        grid.add(new Label("Harga:"), 0, 2);
        grid.add(txtPrice, 1, 2);

        grid.add(new Label("Stok:"), 0, 3);
        grid.add(txtStock, 1, 3);

        // Add components to VBox
        getChildren().addAll(
            new Label("Form Input Produk Agri-POS"),
            grid,
            btnAdd,
            new Label("Daftar Produk:"),
            listView
        );
    }

    public TextField getTxtCode() { return txtCode; }
    public TextField getTxtName() { return txtName; }
    public TextField getTxtPrice() { return txtPrice; }
    public TextField getTxtStock() { return txtStock; }
    public Button getBtnAdd() { return btnAdd; }
    public ListView<String> getListView() { return listView; }

    public void clearForm() {
        txtCode.clear();
        txtName.clear();
        txtPrice.clear();
        txtStock.clear();
        txtCode.requestFocus();
    }
}