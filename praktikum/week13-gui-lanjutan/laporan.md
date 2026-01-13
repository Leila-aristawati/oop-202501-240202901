# Laporan Praktikum Minggu 13
Topik: GUI Lanjutan JavaFX (TableView dan Lambda Expression)

## Identitas
- Nama  : Leila Aristawati
- NIM   : 240202901
- Kelas : 3IKRB

---

## Tujuan
1. Menampilkan data menggunakan TableView JavaFX.
2. Mengintegrasikan koleksi objek dengan GUI.
3. Menggunakan lambda expression untuk event handling.
4. Menghubungkan GUI dengan DAO secara penuh.
5. Membangun antarmuka GUI Agri-POS yang lebih interaktif.

---

## Dasar Teori
JavaFX sebagai GUI Lanjutan

JavaFX merupakan framework Java yang digunakan untuk membangun aplikasi desktop berbasis Graphical User Interface (GUI). Pada praktikum Week 13, JavaFX dimanfaatkan untuk membuat tampilan aplikasi yang lebih kompleks dan interaktif, seperti penggunaan TableView, Button, dan TextField untuk pengelolaan data.
JavaFX mendukung pemisahan antara logika program dan tampilan, sehingga memudahkan pengembangan aplikasi berskala menengah hingga besar.

---

## Kode Program
 
## Product.java
```java
package com.upb.agripos.model;

public class Product {
    private String code;
    private String name;
    private double price;
    private int stock;

    public Product(String code, String name, double price, int stock) {
        this.code = code;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
}
```

## ProductDAO.java
```java
package com.upb.agripos.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.upb.agripos.model.Product;

public class ProductDAO {

    private final String URL = "jdbc:postgresql://localhost:5432/agripos";
    private final String USER = "postgres";
    private final String PASS = "leila230107"; 

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public void insert(Product p) {
        String sql = "INSERT INTO products(code,name,price,stock) VALUES (?,?,?,?)";

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, p.getCode());
            ps.setString(2, p.getName());
            ps.setDouble(3, p.getPrice());
            ps.setInt(4, p.getStock());

            ps.executeUpdate();
            System.out.println("INSERT BERHASIL");

        } catch (SQLException e) {
            System.out.println("INSERT GAGAL");
            e.printStackTrace();
        }
    }

    public List<Product> findAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products";

        try (Connection c = getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Product(
                    rs.getString("code"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("stock")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void delete(String code) {
        String sql = "DELETE FROM products WHERE code=?";

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, code);
            ps.executeUpdate();
            System.out.println("DELETE BERHASIL");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

## ProductService.java
```java
package com.upb.agripos.service;

import java.util.List;

import com.upb.agripos.dao.ProductDAO;
import com.upb.agripos.model.Product;

public class ProductService {
    private final ProductDAO dao = new ProductDAO();

    public void insert(Product p) {
        dao.insert(p);
    }

    public void delete(String code) {
        dao.delete(code);
    }

    public List<Product> findAll() {
        return dao.findAll();
    }
}
```

## ProductTableView.java
```java
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
```

## ProductController.java
```java
package com.upb.agripos.controller;

import java.util.List;

import com.upb.agripos.model.Product;
import com.upb.agripos.service.ProductService;

public class ProductController {

    private final ProductService service = new ProductService();

    public void addProduct(Product p) {
        service.insert(p);
    }

    public void deleteProduct(String code) {
        service.delete(code);
    }

    public List<Product> getAllProducts() {
        return service.findAll();
    }
}
```

---

## Hasil Eksekusi  
![Screenshot hasil](screenshots/hasil.png)
---

## Analisis
- Jelaskan bagaimana kode berjalan.  
**Jawaban** Aplikasi diawali dengan menjalankan kelas utama yang mewarisi Application dari JavaFX. Pada metode start(Stage stage), program melakukan inisialisasi komponen utama aplikasi, termasuk pembuatan tampilan antarmuka dan pengaturan scene.
Selanjutnya, aplikasi membangun struktur berdasarkan pola Model–View–Controller (MVC). Data produk dikelola pada bagian model dan service, sementara tampilan antarmuka ditangani oleh komponen view seperti TableView, Button, dan TextField. Controller berperan sebagai penghubung antara tampilan dan logika aplikasi.
Ketika pengguna melakukan interaksi, seperti menekan tombol tambah atau hapus data, event handler pada JavaFX akan dipanggil. Event tersebut kemudian diproses oleh controller untuk melakukan operasi CRUD pada data. Setelah proses selesai, tampilan tabel akan diperbarui secara otomatis agar menampilkan data terbaru.
Dengan alur ini, aplikasi dapat menampilkan, menambah, dan menghapus data produk secara interaktif melalui antarmuka grafis. 
- Apa perbedaan pendekatan minggu ini dibanding minggu sebelumnya.   
**Jawaban** Pendekatan yang digunakan pada Week 13 berbeda dengan minggu sebelumnya yang masih berfokus pada logika program dan antarmuka sederhana. Pada minggu sebelumnya, interaksi pengguna masih terbatas dan belum sepenuhnya menerapkan pemisahan struktur kode yang jelas.
Pada Week 13, pengembangan aplikasi dilakukan menggunakan JavaFX GUI lanjutan dengan penerapan pola MVC, sehingga antara tampilan, logika, dan pengelolaan data dipisahkan secara jelas. Selain itu, penggunaan komponen seperti TableView dan event handling membuat aplikasi menjadi lebih interaktif dan mendekati aplikasi nyata.
Dengan pendekatan ini, kode menjadi lebih terstruktur, mudah dikembangkan, dan lebih mudah dalam proses debugging. 
- Kendala yang dihadapi dan cara mengatasinya.  
**Jawaban** Selama pengerjaan praktikum Week 13, beberapa kendala yang dihadapi antara lain kesalahan pemanggilan kelas dan objek yang belum terinisialisasi dengan benar, sehingga menyebabkan error saat proses kompilasi maupun saat aplikasi dijalankan.
Selain itu, kesalahan pada event handling dan pengelolaan data juga menyebabkan aplikasi tidak berjalan sesuai harapan, seperti data tidak muncul pada tabel atau event klik tidak berfungsi.
Kendala tersebut diatasi dengan melakukan pengecekan ulang struktur folder dan package, memastikan setiap kelas berada pada package yang sesuai, serta memperbaiki proses inisialisasi objek sebelum digunakan. Pengujian dilakukan secara bertahap untuk memastikan setiap bagian aplikasi berjalan dengan baik sebelum digabungkan menjadi satu kesatuan.

---

## Kesimpulan
Berdasarkan praktikum Week 13 yang telah dilakukan, dapat disimpulkan bahwa penggunaan JavaFX memungkinkan pembuatan aplikasi berbasis Graphical User Interface (GUI) yang lebih interaktif dan terstruktur. Dengan penerapan komponen GUI lanjutan seperti TableView, Button, serta mekanisme event handling, aplikasi dapat merespons tindakan pengguna secara langsung.
Penerapan pola arsitektur Model–View–Controller (MVC) membantu memisahkan antara tampilan, logika program, dan pengelolaan data, sehingga kode menjadi lebih terorganisir dan mudah dikembangkan. Pendekatan ini juga mempermudah proses pemeliharaan dan pengembangan fitur di masa mendatang.
Melalui praktikum ini, pemahaman mengenai konsep GUI lanjutan, alur kerja JavaFX, serta penanganan event dan error dalam aplikasi semakin meningkat. Dengan demikian, tujuan praktikum Week 13 untuk membangun aplikasi GUI yang fungsional dan terstruktur dapat tercapai dengan baik.

---
