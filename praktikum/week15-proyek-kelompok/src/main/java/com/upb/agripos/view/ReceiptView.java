package com.upb.agripos.view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.print.PrinterJob;

/**
 * Simple receipt dialog that shows a preformatted receipt text and allows printing.
 */
public class ReceiptView {

    public static void showReceipt(String receiptText) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Struk Transaksi");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        TextArea area = new TextArea(receiptText);
        area.setWrapText(false);
        area.setEditable(false);
        area.setStyle("-fx-font-family: 'Monospaced'; -fx-font-size: 12px;");

        root.setCenter(area);

        Button btnPrint = new Button("Cetak");
        Button btnClose = new Button("Tutup");

        btnPrint.setOnAction(e -> {
            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null) {
                boolean printed = job.printPage(area);
                if (printed) {
                    job.endJob();
                }
            }
        });

        btnClose.setOnAction(e -> stage.close());

        HBox buttons = new HBox(10, btnPrint, btnClose);
        buttons.setPadding(new Insets(10, 0, 0, 0));

        root.setBottom(buttons);

        Scene scene = new Scene(root, 480, 480, Color.WHITE);
        stage.setScene(scene);
        stage.showAndWait();
    }
}

