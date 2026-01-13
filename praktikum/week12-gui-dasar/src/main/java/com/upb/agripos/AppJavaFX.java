package com.upb.agripos;

import com.upb.agripos.controller.ProductController;
import com.upb.agripos.dao.ProductDAOImpl;
import com.upb.agripos.service.ProductService;
import com.upb.agripos.view.ProductFormView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;

public class AppJavaFX extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // Load driver
        Class.forName("org.postgresql.Driver");

        // Koneksi database
        Connection conn = DriverManager.getConnection(
            "jdbc:postgresql://localhost:5432/agripos",
            "postgres",
            "leila230107"
        );

        // Service
        ProductService service =
            new ProductService(new ProductDAOImpl(conn));

        // View (TANPA parameter)
        ProductFormView view = new ProductFormView();

        // Controller (service + view)
        new ProductController(service, view);

        // Scene
        stage.setScene(new Scene(view, 400, 400));
        stage.setTitle("AgriPOS");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
