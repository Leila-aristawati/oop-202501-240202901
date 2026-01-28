package com.upb.agripos.view;

import java.util.List;

import com.upb.agripos.model.CartItem;
import com.upb.agripos.model.Transaction;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TransactionDetailView {

    public static void show(Transaction transaction, List<CartItem> items) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Detail Transaksi - ID: " + transaction.getId());

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        Label header = new Label("Struk - ID: " + transaction.getId() + " | Kasir: " + transaction.getCashierName() + " | Total: Rp " + String.format("%,.0f", transaction.getTotalAmount()));
        header.setPadding(new Insets(0,0,8,0));

        TableView<CartItem> table = new TableView<>();
        TableColumn<CartItem, String> codeCol = new TableColumn<>("Kode");
        codeCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getProduct().getCode()));
        codeCol.setPrefWidth(100);

        TableColumn<CartItem, String> nameCol = new TableColumn<>("Nama");
        nameCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getProduct().getName()));
        nameCol.setPrefWidth(220);

        TableColumn<CartItem, Integer> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getQuantity()).asObject());
        qtyCol.setPrefWidth(60);

        TableColumn<CartItem, Double> priceCol = new TableColumn<>("Harga");
        priceCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleDoubleProperty(cell.getValue().getProduct().getPrice()).asObject());
        priceCol.setPrefWidth(100);

        TableColumn<CartItem, Double> totalCol = new TableColumn<>("Subtotal");
        totalCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleDoubleProperty(cell.getValue().getTotalPrice()).asObject());
        totalCol.setPrefWidth(120);

        table.getColumns().addAll(java.util.Arrays.asList(codeCol, nameCol, qtyCol, priceCol, totalCol));
        table.getItems().addAll(items);

        Button btnClose = new Button("Tutup");
        btnClose.setOnAction(e -> stage.close());

        HBox bottom = new HBox(10, btnClose);
        bottom.setPadding(new Insets(8,0,0,0));

        root.setTop(header);
        root.setCenter(table);
        root.setBottom(bottom);

        Scene scene = new Scene(root, 640, 400, Color.WHITE);
        stage.setScene(scene);
        stage.showAndWait();
    }
}
