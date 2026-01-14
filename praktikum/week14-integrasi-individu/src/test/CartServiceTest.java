package com.upb.agripos;

import com.upb.agripos.controller.PosController;
import com.upb.agripos.dao.JdbcProductDAO;
import com.upb.agripos.service.CartService;
import com.upb.agripos.service.ProductService;
import com.upb.agripos.view.PosView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppJavaFX extends Application {

    @Override
    public void start(Stage stage) {
        System.out.println("Hello World, I am Leila Aristawati-240202901");

        ProductService ps = new ProductService(JdbcProductDAO.getInstance());
        CartService cs = new CartService();
        PosController controller = new PosController(ps, cs);

        stage.setScene(new Scene(new PosView(controller), 600, 400));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}