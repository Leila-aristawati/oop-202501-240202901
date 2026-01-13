package com.upb.agripos.view;

import com.upb.agripos.controller.ProductController;
import com.upb.agripos.model.Product;

import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class ProductTableView extends VBox {

    private final ProductController controller = new ProductController();
    private final TableView<Product> table = new TableView<>();

    public ProductTableView() {

        TextField txtCode = new TextField();
        txtCode.setPromptText("Kode");

        TextField txtName = new TextField();
        txtName.setPromptText("Nama");

        TextField txtPrice = new TextField();
        txtPrice.setPromptText("Harga");

        TextField txtStock = new TextField();
        txtStock.setPromptText("Stok");

        Button btnAdd = new Button("Tambah Produk");
        Button btnDelete = new Button("Hapus Produk");

        TableColumn<Product,String> colCode = new TableColumn<>("Kode");
        colCode.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getCode()));

        TableColumn<Product,String> colName = new TableColumn<>("Nama");
        colName.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getName()));

        TableColumn<Product,Number> colPrice = new TableColumn<>("Harga");
        colPrice.setCellValueFactory(d -> new ReadOnlyDoubleWrapper(d.getValue().getPrice()));

        TableColumn<Product,Number> colStock = new TableColumn<>("Stok");
        colStock.setCellValueFactory(d -> new ReadOnlyIntegerWrapper(d.getValue().getStock()));

        table.getColumns().addAll(colCode, colName, colPrice, colStock);

btnAdd.setOnAction(e -> {
    try {
        double price = Double.parseDouble(
            txtPrice.getText().replace(".", "")
        );

        Product p = new Product(
            txtCode.getText(),
            txtName.getText(),
            price,
            Integer.parseInt(txtStock.getText())
        );

        controller.addProduct(p);
        loadData();

        txtCode.clear();
        txtName.clear();
        txtPrice.clear();
        txtStock.clear();

    } catch (Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Input tidak valid");
        alert.setContentText("Harga dan stok harus berupa angka!");
        alert.show();
    }
});


        btnDelete.setOnAction(e -> {
            Product p = table.getSelectionModel().getSelectedItem();
            if (p != null) {
                controller.deleteProduct(p.getCode());
                loadData();
            }
        });

        loadData();

        getChildren().addAll(
                new HBox(5, txtCode, txtName, txtPrice, txtStock),
                new HBox(5, btnAdd, btnDelete),
                table
        );
    }

    private void loadData() {
        ObservableList<Product> data =
                FXCollections.observableArrayList(controller.getAllProducts());
        table.setItems(data);
    }
}