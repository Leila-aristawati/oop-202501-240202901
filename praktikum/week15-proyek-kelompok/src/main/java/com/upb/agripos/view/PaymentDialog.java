package com.upb.agripos.view;

import com.upb.agripos.model.payment.PaymentMethod;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Dialog untuk memilih metode pembayaran dan memasukkan jumlah yang dibayarkan
 */
public class PaymentDialog {
    private PaymentMethod selectedPaymentMethod;
    private double paidAmount;
    private boolean confirmed = false;

    public PaymentDialog(double totalAmount) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Pembayaran");
        stage.setResizable(false);

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: white;");

        // Title
        Label titleLabel = new Label("💳 FORM PEMBAYARAN");
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");

        // Total Amount
        VBox totalBox = new VBox(5);
        Label totalLabelText = new Label("Total Belanja:");
        totalLabelText.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
        Label totalValue = new Label("Rp " + formatCurrency(totalAmount));
        totalValue.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #1976d2;");
        totalBox.getChildren().addAll(totalLabelText, totalValue);

        // Payment Method Section
        VBox methodBox = new VBox(8);
        Label methodLabel = new Label("Pilih Metode Pembayaran:");
        methodLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");

        ToggleGroup paymentGroup = new ToggleGroup();
        RadioButton rbCash = new RadioButton("💵 Tunai (Cash)");
        RadioButton rbEWallet = new RadioButton("📱 E-Wallet");
        rbCash.setToggleGroup(paymentGroup);
        rbEWallet.setToggleGroup(paymentGroup);
        rbCash.setSelected(true);

        methodBox.getChildren().addAll(methodLabel, rbCash, rbEWallet);

        // Paid Amount Section
        VBox paidBox = new VBox(8);
        Label paidLabel = new Label("Jumlah Pembayaran:");
        paidLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");

        HBox paidInputBox = new HBox(5);
        paidInputBox.setAlignment(Pos.CENTER_LEFT);
        Label currencyLabel = new Label("Rp");
        currencyLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
        TextField txtPaidAmount = new TextField();
        txtPaidAmount.setPrefWidth(150);
        txtPaidAmount.setStyle("-fx-font-size: 12; -fx-padding: 5;");
        txtPaidAmount.setPromptText("0");
        txtPaidAmount.setText(String.valueOf((long) totalAmount));

        paidInputBox.getChildren().addAll(currencyLabel, txtPaidAmount);
        paidBox.getChildren().addAll(paidLabel, paidInputBox);

        // Change display (only for cash payment)
        VBox changeBox = new VBox(5);
        Label changeLabel = new Label("Kembalian:");
        changeLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
        Label changeValue = new Label("Rp 0");
        changeValue.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");
        changeBox.getChildren().addAll(changeLabel, changeValue);
        changeBox.setVisible(rbCash.isSelected());

        // Real-time change calculation
        txtPaidAmount.textProperty().addListener((obs, old, newVal) -> {
            try {
                double paid = newVal.isEmpty() ? 0 : Double.parseDouble(newVal);
                double change = paid - totalAmount;
                changeValue.setText("Rp " + formatCurrency(Math.max(0, change)));
                changeValue.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: " + (change < 0 ? "#d32f2f" : "#2E7D32") + ";");
            } catch (NumberFormatException e) {
                changeValue.setText("Rp 0");
                changeValue.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #d32f2f;");
            }
        });

        // Toggle visibility of change box based on payment method
        rbCash.selectedProperty().addListener((obs, old, newVal) -> changeBox.setVisible(newVal));
        rbEWallet.selectedProperty().addListener((obs, old, newVal) -> changeBox.setVisible(!newVal));

        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        Button btnConfirm = new Button("✅ Proses");
        btnConfirm.setStyle("-fx-font-size: 12; -fx-padding: 8 20; -fx-background-color: #1976d2; -fx-text-fill: white; -fx-cursor: hand;");

        Button btnCancel = new Button("❌ Batal");
        btnCancel.setStyle("-fx-font-size: 12; -fx-padding: 8 20; -fx-background-color: #d32f2f; -fx-text-fill: white; -fx-cursor: hand;");

        btnConfirm.setOnAction(e -> {
            try {
                double paid = txtPaidAmount.getText().isEmpty() ? 0 : Double.parseDouble(txtPaidAmount.getText().trim());

                if (paid <= 0) {
                    showAlert("Error", "Jumlah pembayaran harus lebih dari 0");
                    return;
                }

                if (rbCash.isSelected() && paid < totalAmount) {
                    showAlert("Error", "Jumlah pembayaran tidak cukup untuk pembayaran tunai");
                    return;
                }

                selectedPaymentMethod = rbCash.isSelected() ? PaymentMethod.CASH : PaymentMethod.E_WALLET;
                paidAmount = paid;
                confirmed = true;
                stage.close();
            } catch (NumberFormatException ex) {
                showAlert("Error", "Format jumlah pembayaran tidak valid");
            }
        });

        btnCancel.setOnAction(e -> stage.close());

        buttonBox.getChildren().addAll(btnConfirm, btnCancel);

        // Assemble
        root.getChildren().addAll(
            titleLabel,
            new Separator(),
            totalBox,
            new Separator(),
            methodBox,
            new Separator(),
            paidBox,
            changeBox,
            buttonBox
        );

        Scene scene = new Scene(root, 380, 420);
        stage.setScene(scene);
        stage.showAndWait();
    }

    private String formatCurrency(double amount) {
        return String.format("%,.0f", amount);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public PaymentMethod getSelectedPaymentMethod() {
        return selectedPaymentMethod;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
