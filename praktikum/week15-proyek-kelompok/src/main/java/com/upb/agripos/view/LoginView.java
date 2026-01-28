package com.upb.agripos.view;

import com.upb.agripos.model.User;
import com.upb.agripos.service.UserService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginView extends VBox {
    private final UserService userService;
    private final Stage stage;
    private Runnable onLoginSuccess;

    public LoginView(Stage stage, UserService userService) {
        this.stage = stage;
        this.userService = userService;

        this.setPadding(new Insets(30));
        this.setSpacing(15);
        this.setAlignment(Pos.CENTER);
        this.setStyle("-fx-background-color: #f0f0f0;");

        // ========== HEADER ==========
        Label titleLabel = new Label("🌾 AGRIPOS SYSTEM");
        titleLabel.setStyle("-fx-font-size: 28; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");

        Label subtitleLabel = new Label("Sistem Informasi Penjualan Pertanian");
        subtitleLabel.setStyle("-fx-font-size: 14; -fx-text-fill: #666666;");

        // ========== LOGIN FORM ==========
        Label loginLabel = new Label("📋 LOGIN");
        loginLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        // Username Field
        Label usernameLabel = new Label("Username:");
        usernameLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Masukkan username");
        usernameField.setPrefHeight(40);
        usernameField.setStyle("-fx-font-size: 12; -fx-padding: 10;");

        // Password Field
        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Masukkan password");
        passwordField.setPrefHeight(40);
        passwordField.setStyle("-fx-font-size: 12; -fx-padding: 10;");

        // Demo Credentials Info
        Label infoLabel = new Label("Demo Credentials:\n" +
                "👤 gudang / gudang123  (GUDANG)\n" +
                "👤 admin / admin123     (ADMIN)\n" +
                "👤 kasir / kasir123     (KASIR)");
        infoLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #666666; -fx-padding: 10;");
        infoLabel.setWrapText(true);

        // Login Button
        Button loginButton = new Button("🔓 LOGIN");
        loginButton.setPrefHeight(40);
        loginButton.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-padding: 10;");
        loginButton.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-padding: 10; " +
                "-fx-background-color: #2E7D32; -fx-text-fill: white; -fx-cursor: hand;");

        // Error Label
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-size: 12;");

        loginButton.setOnAction(e -> {
            errorLabel.setText("");
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("❌ Username dan password harus diisi!");
                return;
            }

            User user = userService.login(username, password);
            if (user != null) {
                showAlert("Sukses", "Login berhasil!\nSelamat datang, " + user.getFullName());
                if (onLoginSuccess != null) {
                    onLoginSuccess.run();
                }
            } else {
                errorLabel.setText("❌ Username atau password salah!");
                passwordField.clear();
                usernameField.requestFocus();
            }
        });

        // Handle Enter key
        passwordField.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("ENTER")) {
                loginButton.fire();
            }
        });

        // ========== ASSEMBLE LOGIN FORM ==========
        VBox formBox = new VBox(10);
        formBox.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; " +
                "-fx-background-color: white; -fx-padding: 20;");
        formBox.setPrefWidth(350);
        formBox.getChildren().addAll(
            loginLabel,
            usernameLabel,
            usernameField,
            passwordLabel,
            passwordField,
            errorLabel,
            loginButton,
            infoLabel
        );

        // ========== MAIN LAYOUT ==========
        this.getChildren().addAll(
            titleLabel,
            subtitleLabel,
            new Label(""),  // Spacer
            formBox
        );
    }

    public void setOnLoginSuccess(Runnable onSuccess) {
        this.onLoginSuccess = onSuccess;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
