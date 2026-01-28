package com.upb.agripos;

import com.upb.agripos.config.DatabaseConnection;
import com.upb.agripos.controller.AdminController;
import com.upb.agripos.controller.PosController;
import com.upb.agripos.dao.CartDAO;
import com.upb.agripos.dao.ProductDAOImpl;
import com.upb.agripos.service.CartService;
import com.upb.agripos.service.ProductService;
import com.upb.agripos.service.TransactionService;
import com.upb.agripos.service.UserService;
import com.upb.agripos.view.AdminView;
import com.upb.agripos.view.GudangView;
import com.upb.agripos.view.KasirView;
import com.upb.agripos.view.LoginView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppJavaFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Print identity
        System.out.println("Hello World, I am Vg-240202888");

        // Initialize database
        var dbConn = DatabaseConnection.getInstance();
        var dataSource = dbConn.getDataSource();

        // Initialize DAO
        var productDAO = new ProductDAOImpl(dataSource);
        var cartDAO = CartDAO.getInstance();

        // Initialize Services
        var productService = new ProductService(productDAO);
        var cartService = new CartService(productService, cartDAO);
        var userService = new UserService();
        var transactionService = new TransactionService();
        // Show login screen first
        showLoginScreen(primaryStage, productService, cartService, userService, transactionService);
    }

    private void showLoginScreen(Stage primaryStage, ProductService productService, CartService cartService, UserService userService, TransactionService transactionService) {
        LoginView loginView = new LoginView(primaryStage, userService);
        loginView.setOnLoginSuccess(() -> showMainApp(primaryStage, productService, cartService, userService, transactionService));
        Scene loginScene = new Scene(loginView, 500, 600);
        primaryStage.setScene(loginScene);
        primaryStage.setTitle("Agri-POS - Login");
        primaryStage.show();
    }

    private void showMainApp(Stage primaryStage, ProductService productService, CartService cartService, UserService userService, TransactionService transactionService) {
        var current = userService.getCurrentUser();
        
        if (current != null && "gudang".equalsIgnoreCase(current.getRole())) {
            // Show Gudang View
            showGudangApp(primaryStage, productService, cartService, userService, transactionService, current);
        } else if (current != null && "admin".equalsIgnoreCase(current.getRole())) {
            // Show Admin View
            showAdminApp(primaryStage, productService, cartService, userService, transactionService, current);
        } else {
            // Show Kasir View
            showKasirApp(primaryStage, productService, cartService, userService, transactionService, current);
        }
    }

    private void showGudangApp(Stage primaryStage, ProductService productService, CartService cartService, UserService userService, TransactionService transactionService, com.upb.agripos.model.User current) {
        var controller = new PosController(productService, cartService, userService, transactionService);
        var view = new GudangView();

        // Display current user info
        view.getUserInfoLabel().setText("👤 " + current.getFullName() + " (GUDANG)");

        // Bind action handlers for product management
        view.getBtnAddProduct().setOnAction(e -> controller.addProduct(
            view.getTxtCode(), view.getTxtName(), view.getTxtPrice(), view.getTxtStock(), view.getProductTable()
        ));
        view.getBtnDeleteProduct().setOnAction(e -> controller.deleteProduct(view.getProductTable()));
        view.getBtnIncreaseStock().setOnAction(e -> controller.increaseStock(view.getProductTable()));
        view.getBtnDecreaseStock().setOnAction(e -> controller.decreaseStock(view.getProductTable()));

        // Bind logout action
        view.getBtnLogout().setOnAction(e -> {
            userService.logout();
            showLoginScreen(primaryStage, productService, cartService, userService, transactionService);
        });

        // Bind report buttons for gudang
        if (view.getBtnStockInReport() != null) {
            view.getBtnStockInReport().setOnAction(e -> controller.showStockInReport());
        }
        if (view.getBtnStockOutReport() != null) {
            view.getBtnStockOutReport().setOnAction(e -> controller.showStockOutReport());
        }

        // Load products
        controller.loadProducts(view.getProductTable());

        Scene scene = new Scene(view, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("📦 Agri-POS - Manajemen Gudang");
        primaryStage.show();
    }

    private void showAdminApp(Stage primaryStage, ProductService productService, CartService cartService, UserService userService, TransactionService transactionService, com.upb.agripos.model.User current) {
        var controller = new AdminController(userService, transactionService, productService, cartService);
        var view = new AdminView();

        // Display current user info
        view.getUserInfoLabel().setText("👤 " + current.getFullName() + " (ADMIN)");

        // Bind action handlers for admin functions
        view.getBtnAddUser().setOnAction(e -> controller.addUser(
            view.getTxtUsername(), view.getTxtPassword(), view.getTxtFullName(), view.getCbRole(), view.getUserTable()
        ));
        view.getBtnRefreshHistory().setOnAction(e -> controller.loadHistory(view.getHistoryTable()));

        // Bind action handlers for reports
        if (view.getBtnCashierReport() != null) {
            view.getBtnCashierReport().setOnAction(e -> controller.showCashierSalesReport());
        }
        if (view.getBtnWarehouseReport() != null) {
            view.getBtnWarehouseReport().setOnAction(e -> controller.showWarehouseReport());
        }

        // Bind logout action
        view.getBtnLogout().setOnAction(e -> {
            userService.logout();
            showLoginScreen(primaryStage, productService, cartService, userService, transactionService);
        });

        // Load initial data
        controller.loadUsers(view.getUserTable());
        controller.loadHistory(view.getHistoryTable());

        // Double-click on history row shows transaction details
        view.getHistoryTable().setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                var tr = view.getHistoryTable().getSelectionModel().getSelectedItem();
                if (tr != null) controller.showTransactionDetail(tr);
            }
        });

        Scene scene = new Scene(view, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("🛡️ Agri-POS - Admin Panel");
        primaryStage.show();
    }

    private void showKasirApp(Stage primaryStage, ProductService productService, CartService cartService, UserService userService, TransactionService transactionService, com.upb.agripos.model.User current) {
        var controller = new PosController(productService, cartService, userService, transactionService);
        var view = new KasirView();

        // Display current user info
        String userName = current != null ? current.getFullName() : "-";
        String userRole = current != null ? current.getRole().toUpperCase() : "KASIR";
        view.getUserInfoLabel().setText("👤 " + userName + " (" + userRole + ")");

        // Bind action handlers for sales transaction
        view.getBtnAddToCart().setOnAction(e -> controller.addToCart(
            view.getProductTable(), view.getCartTable(), view.getTotalLabel()
        ));
        // Pass history table so checkout can refresh it after success
        view.getBtnCheckout().setOnAction(e -> controller.checkout(view.getProductTable(), view.getCartTable(), view.getTotalLabel(), view.getHistoryTable()));
        // Clear cart button (remove all items)
        view.getBtnClearCart().setOnAction(e -> controller.clearCart(view.getCartTable(), view.getTotalLabel()));

        // Set callbacks for cart quantity changes
        view.setOnIncreaseQuantity(productCode -> controller.increaseQuantity(productCode, view.getCartTable(), view.getTotalLabel()));
        view.setOnDecreaseQuantity(productCode -> controller.decreaseQuantity(productCode, view.getCartTable(), view.getTotalLabel()));

        // Load products for all users (use controller helper)
        try {
            controller.loadProducts(view.getProductTable());
        } catch (Exception e) {
            System.err.println("Error loading products:");
            e.printStackTrace();
        }

        // Bind logout action
        view.getBtnLogout().setOnAction(e -> {
            userService.logout();
            showLoginScreen(primaryStage, productService, cartService, userService, transactionService);
        });

        // Load initial data for kasir
        controller.loadKasirHistory(view.getHistoryTable(), current.getId());

        // Double-click on kasir history row shows transaction details
        view.getHistoryTable().setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                var tr = view.getHistoryTable().getSelectionModel().getSelectedItem();
                if (tr != null) controller.showTransactionDetail(tr);
            }
        });

        Scene scene = new Scene(view, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("🛒 Agri-POS - Sistem Kasir");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
