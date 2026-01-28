package com.upb.agripos.view;

import com.upb.agripos.model.Transaction;
import com.upb.agripos.model.User;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class AdminView extends VBox {
    private TextField txtUsername, txtPassword, txtFullName;
    private ComboBox<String> cbRole;
    private Button btnAddUser, btnRefreshHistory, btnLogout;
    private TableView<User> userTable;
    private TableView<Transaction> historyTable;
    private Label userInfoLabel;
    private TabPane tabPane;
    private Button btnCashierReport, btnWarehouseReport;

    public AdminView() {
        // ========== MAIN STYLING ==========
        this.setPadding(new Insets(20));
        this.setSpacing(15);
        this.setStyle("-fx-background-color: #f0f0f0;");

        // ========== HEADER ==========
        VBox headerBox = createHeaderBox();

        // ========== USER INFO ==========
        HBox userInfoBox = new HBox(10);
        userInfoBox.setAlignment(Pos.CENTER_LEFT);
        userInfoLabel = new Label("👤 User: -");
        userInfoLabel.setStyle("-fx-font-size: 13; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");
        btnLogout = createStyledButton("🚪 Logout", "#c62828");
        userInfoBox.getChildren().addAll(userInfoLabel, btnLogout);

        // ========== TAB PANE ==========
        tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-radius: 5;");

        // Tab Add User
        Tab tabAddUser = new Tab("Tambah User");
        tabAddUser.setClosable(false);
        tabAddUser.setContent(createAddUserTab());

        // Tab History
        Tab tabHistory = new Tab("History Penjualan");
        tabHistory.setClosable(false);
        tabHistory.setContent(createHistoryTab());

        // Tab Laporan
        Tab tabLaporan = new Tab("Laporan Penjualan");
        tabLaporan.setClosable(false);
        tabLaporan.setContent(createLaporanTab());

        tabPane.getTabs().addAll(tabAddUser, tabHistory, tabLaporan);

        // ========== ASSEMBLE MAIN LAYOUT ==========
        this.getChildren().addAll(headerBox, userInfoBox, tabPane);
    }

    private VBox createHeaderBox() {
        VBox headerBox = new VBox(5);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("🛡️ ADMIN PANEL - AGRIPOS");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");

        Label subtitleLabel = new Label("Kelola user dan lihat history penjualan");
        subtitleLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #666666;");

        headerBox.getChildren().addAll(titleLabel, subtitleLabel);
        return headerBox;
    }

    private VBox createAddUserTab() {
        VBox tabContent = new VBox(15);
        tabContent.setPadding(new Insets(20));

        Label formLabel = new Label("➕ Tambah User Baru");
        formLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");

        // Form fields
        txtUsername = new TextField();
        txtUsername.setPromptText("Username");
        txtUsername.setPrefWidth(200);

        txtPassword = new PasswordField();
        txtPassword.setPromptText("Password");
        txtPassword.setPrefWidth(200);

        txtFullName = new TextField();
        txtFullName.setPromptText("Nama Lengkap");
        txtFullName.setPrefWidth(200);

        cbRole = new ComboBox<>();
        cbRole.getItems().addAll("admin", "kasir", "gudang");
        cbRole.setValue("kasir");
        cbRole.setPrefWidth(200);

        btnAddUser = createStyledButton("➕ Tambah User", "#2E7D32");

        // User Table
        userTable = new TableView<>();
        userTable.setStyle("-fx-font-size: 11; -fx-padding: 5;");

        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);
        idCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameCol.setPrefWidth(100);
        usernameCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<User, String> fullNameCol = new TableColumn<>("Nama Lengkap");
        fullNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        fullNameCol.setPrefWidth(150);
        fullNameCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        roleCol.setPrefWidth(80);
        roleCol.setStyle("-fx-alignment: CENTER;");

        userTable.getColumns().addAll(idCol, usernameCol, fullNameCol, roleCol);
        userTable.setPrefHeight(200);
        // Make table expand to full width and distribute columns evenly
        userTable.setMaxWidth(Double.MAX_VALUE);
        userTable.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);
        // bind each column to equal share of the table width
        idCol.prefWidthProperty().bind(userTable.widthProperty().divide(4));
        usernameCol.prefWidthProperty().bind(userTable.widthProperty().divide(4));
        fullNameCol.prefWidthProperty().bind(userTable.widthProperty().divide(4));
        roleCol.prefWidthProperty().bind(userTable.widthProperty().divide(4));

        HBox formBox = new HBox(10, txtUsername, txtPassword, txtFullName, cbRole, btnAddUser);
        formBox.setAlignment(Pos.CENTER_LEFT);

        tabContent.getChildren().addAll(formLabel, formBox, userTable);
        VBox.setVgrow(userTable, Priority.ALWAYS);

        return tabContent;
    }

    private VBox createHistoryTab() {
        VBox tabContent = new VBox(15);
        tabContent.setPadding(new Insets(20));

        Label historyLabel = new Label("📊 History Penjualan");
        historyLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");

        btnRefreshHistory = createStyledButton("🔄 Refresh", "#1976d2");

        // History Table
        historyTable = new TableView<>();
        historyTable.setStyle("-fx-font-size: 11; -fx-padding: 5;");

        TableColumn<Transaction, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<Transaction, Integer> cartIdCol = new TableColumn<>("Cart ID");
        cartIdCol.setCellValueFactory(new PropertyValueFactory<>("cartId"));
        cartIdCol.setPrefWidth(80);

        TableColumn<Transaction, Integer> userIdCol = new TableColumn<>("User ID");
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        userIdCol.setPrefWidth(80);

        TableColumn<Transaction, Double> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        totalCol.setPrefWidth(100);

        TableColumn<Transaction, String> paymentCol = new TableColumn<>("Pembayaran");
        paymentCol.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        paymentCol.setPrefWidth(100);

        TableColumn<Transaction, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(80);

        TableColumn<Transaction, String> dateCol = new TableColumn<>("Tanggal");
        dateCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
            cell.getValue().getTransactionDate().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
        ));
        dateCol.setPrefWidth(150);

        historyTable.getColumns().addAll(idCol, cartIdCol, userIdCol, totalCol, paymentCol, statusCol, dateCol);
        historyTable.setPrefHeight(300);

        HBox buttonBox = new HBox(btnRefreshHistory);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        tabContent.getChildren().addAll(historyLabel, buttonBox, historyTable);
        VBox.setVgrow(historyTable, Priority.ALWAYS);

        return tabContent;
    }

    private VBox createLaporanTab() {
        VBox laporanTab = new VBox(15);
        laporanTab.setStyle("-fx-background-color: #fafafa; -fx-padding: 12;");

        Label laporanLabel = new Label("📊 Laporan Penjualan");
        laporanLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #2E7D32;");

        Label descLabel = new Label("Pilih jenis laporan yang ingin ditampilkan.\n" +
            "Laporan Kasir menampilkan detail laporan untuk kasir tertentu.\n" +
            "Laporan Gudang menampilkan inventaris produk dan produk keluar.");
        descLabel.setStyle("-fx-font-size: 11; -fx-text-fill: #666666;");

        // Buttons for reports
        btnCashierReport = createStyledButton("👤 Laporan Kasir", "#4caf50");
        btnWarehouseReport = createStyledButton("📦 Laporan Gudang", "#9c27b0");

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(btnCashierReport, Priority.ALWAYS);
        HBox.setHgrow(btnWarehouseReport, Priority.ALWAYS);
        buttonBox.getChildren().addAll(btnCashierReport, btnWarehouseReport);

        laporanTab.getChildren().addAll(laporanLabel, descLabel, buttonBox);
        return laporanTab;
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setPrefHeight(35);
        // allow button to grow to fill available width when HBox.setHgrow(..., Priority.ALWAYS) is used
        button.setMaxWidth(Double.MAX_VALUE);
        // give a reasonable preferred width for consistent initial sizing
        button.setPrefWidth(150);
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
    public TextField getTxtUsername() { return txtUsername; }
    public TextField getTxtPassword() { return txtPassword; }
    public TextField getTxtFullName() { return txtFullName; }
    public ComboBox<String> getCbRole() { return cbRole; }
    public Button getBtnAddUser() { return btnAddUser; }
    public Button getBtnRefreshHistory() { return btnRefreshHistory; }
    public Button getBtnLogout() { return btnLogout; }
    public TableView<User> getUserTable() { return userTable; }
    public TableView<Transaction> getHistoryTable() { return historyTable; }
    public Label getUserInfoLabel() { return userInfoLabel; }
    public TabPane getTabPane() { return tabPane; }
    public Button getBtnCashierReport() { return btnCashierReport; }
    public Button getBtnWarehouseReport() { return btnWarehouseReport; }
}
