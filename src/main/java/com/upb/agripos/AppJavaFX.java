package com.upb.agripos;

import com.upb.agripos.config.DatabaseConnection;
import com.upb.agripos.controller.PosController;
import com.upb.agripos.dao.ProductDAOImpl;
import com.upb.agripos.service.CartService;
import com.upb.agripos.service.ProductService;
import com.upb.agripos.view.PosView;
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

        // Initialize Services
        var productService = new ProductService(productDAO);
        var cartService = new CartService(productService);

        // Initialize Controller
        var controller = new PosController(productService, cartService);

        // Initialize View
        var view = new PosView();

        // Bind actions
        view.getBtnAddProduct().setOnAction(e -> controller.addProduct(
            view.getTxtCode(), view.getTxtName(), view.getTxtPrice(), view.getTxtStock(), view.getProductTable()
        ));
        view.getBtnDeleteProduct().setOnAction(e -> controller.deleteProduct(view.getProductTable()));
        view.getBtnAddToCart().setOnAction(e -> controller.addToCart(
            view.getProductTable(), view.getCartList(), view.getTotalLabel()
        ));
        view.getBtnCheckout().setOnAction(e -> controller.checkout(view.getCartList(), view.getTotalLabel()));
        view.getBtnRefresh().setOnAction(e -> controller.loadProducts(view.getProductTable()));

        // Load initial data
        controller.loadProducts(view.getProductTable());

        // Scene
        Scene scene = new Scene(view, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Agri-POS");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
