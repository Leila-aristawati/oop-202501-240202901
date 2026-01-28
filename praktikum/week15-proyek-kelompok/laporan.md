# Laporan Praktikum Minggu 15
Topik: Proyek Kelompok

## Identitas
- Vigian Agus Isnaeni   [240202888]
- Fendy Agustian        [240202899]
- Leila Aristawati      [240202901]
- Mayang Nur Annisa K   [240202902]
- Tsaqif Nur Fathan A   [240202886]

# Pendahuluan
Proyek ini bertujuan mengembangkan sistem Kelola Barang berbasis Java yang membantu usaha kecil dan menengah dalam mengelola inventaris, mencatat transaksi penjualan, serta menyusun laporan yang akurat dan mudah diakses. Latar belakang proyek adalah kebutuhan praktis akan solusi terintegrasi yang user-friendly dan mudah dikembangkan, sekaligus sebagai wadah pembelajaran penerapan prinsip-prinsip OOP, integrasi database, antarmuka grafis, dan pola desain perangkat lunak.

Tujuan khusus proyek ini meliputi:
- Mendesain struktur kelas dan relasi yang konsisten dengan prinsip OOP (enkapsulasi, pewarisan, polimorfisme).
- Mengimplementasikan antarmuka pengguna interaktif menggunakan JavaFX untuk operasi CRUD, transaksi, dan laporan.
- Membangun lapisan akses data (DAO) menggunakan JDBC untuk integrasi database yang aman dan mudah diuji.
- Menerapkan pola desain seperti MVC, DAO, dan Singleton untuk meningkatkan keterpeliharaan dan skalabilitas.
- Menyusun dokumentasi dan melakukan pengujian untuk memastikan kualitas dan kelayakan penggunaan.

---

## Deskripsi Sistem
Sistem "Kelola Barang" adalah aplikasi desktop berbasis Java yang dirancang untuk membantu usaha kecil dan menengah mengelola inventaris, mencatat transaksi penjualan, dan menghasilkan laporan operasional. Aplikasi ini memadukan antarmuka JavaFX yang responsif, lapisan akses data (DAO) dengan JDBC untuk keamanan dan konsistensi data, serta struktur OOP yang modular untuk memudahkan pengembangan dan pemeliharaan.

Fitur utama:
- **Manajemen Barang:** CRUD untuk produk, kategori, dan pengelolaan stok (penyesuaian stok, notifikasi stok rendah).
- **Transaksi Penjualan:** Pembuatan transaksi cepat, perhitungan otomatis, dan pencatatan riwayat penjualan.
- **Pelaporan:** Laporan stok dan penjualan (harian/bulanan) serta ekspor ke CSV untuk analisis lebih lanjut.
- **Pencarian & Filter:** Pencarian produk dan filter berdasarkan kategori, rentang tanggal, atau status stok.
- **Pengguna & Hak Akses:** Manajemen pengguna dengan peran (admin, kasir) untuk kontrol akses dan keamanan.
- **Backup & Restore:** Fasilitas ekspor/import data untuk cadangan dan pemulihan data.

---

## Desain dan Arsitektur
Sistem dirancang menggunakan pendekatan berlapis dan prinsip OOP untuk memisahkan tanggung jawab dan memudahkan pemeliharaan.

Arsitektur lapisannya meliputi:
- **Presentasi (View):** Antarmuka pengguna berbasis JavaFX (fxml / controller) untuk form CRUD, layar transaksi, dan laporan.
- **Kontrol / Logika Bisnis (Controller / Service):** Pengolah alur aplikasi, validasi input, dan orkestrasi transaksi. Layer ini menangani manajemen transaksi JDBC (commit/rollback).
- **Akses Data (DAO):** Kelas DAO bertanggung jawab untuk operasi CRUD terhadap RDBMS menggunakan JDBC dan PreparedStatement.
- **Database (Persistence):** Skema relasional (tabel produk, kategori, pengguna, transaksi, detail_transaksi) yang disimpan di SQLite/MySQL/Postgres sesuai konfigurasi.

Paket dan struktur kelas (contoh):
- `com.upb.kelolabarang.model`
  - Product (id, nama, harga, stok, Category)
  - Category (id, nama)
  - User (id, username, passwordHash, Role)
  - Transaction (id, tanggal, total, List<TransactionItem>)
  - TransactionItem (productId, qty, price)
- `com.upb.kelolabarang.dao`
  - ProductDao, CategoryDao, UserDao, TransactionDao
- `com.upb.kelolabarang.service`
  - ProductService, TransactionService, ReportService
- `com.upb.kelolabarang.controller`
  - JavaFX controllers untuk setiap tampilan (mis. ProductController, SalesController, ReportController)
- `com.upb.kelolabarang.util`
  - DBConnection (Singleton), DaoFactory, CsvExporter, Validators

Relasi antar kelas (ringkas):
- Product "belongs to" Category (Many-to-One).
- Transaction mengandung collection TransactionItem (composition); setiap TransactionItem mereferensikan Product (association).
- User memiliki Role (enum) untuk hak akses (admin, kasir).

Pola desain yang diterapkan:
- **MVC (Model-View-Controller):** Memisahkan tampilan (JavaFX), logika presentasi (Controller) dan model/domain.
- **DAO (Data Access Object):** Mengabstraksi operasi database sehingga mudah diuji dan diganti implementasinya.
- **Singleton:** `DBConnection` untuk mengelola pool/instance koneksi shared.
- **Factory:** `DaoFactory` untuk mendapatkan instance DAO yang sesuai.
- **Observer / Event-driven:** Digunakan untuk memperbarui tampilan saat ada perubahan stok atau transaksi baru (mis. PropertyListener pada JavaFX).
- **Transaction Management:** Layer service mengelola transaksi JDBC (commit/rollback) untuk menjaga konsistensi data.

Pertimbangan lain:
- Validasi dan penanganan exception dilakukan di layer service untuk menjaga integritas dan memberikan pesan user-friendly di UI.
- Unit test ditujukan pada `service` dan `dao` menggunakan database in-memory (mis. H2) atau mock untuk memastikan logika berjalan benar.

---

## Uraian Singkat
Implementasi sistem mengintegrasikan antarmuka JavaFX, lapisan layanan (Service), dan lapisan akses data (DAO) sehingga alur kerja menjadi jelas dan terpisah.

- **GUI (JavaFX):** Tampilan dibuat dengan FXML dan JavaFX Controllers; komponen seperti `TableView`, `Form`, dan `Dialog` di-bind ke `ObservableList` dan JavaFX Properties untuk update otomatis antar komponen.
- **Kontrol & Logika Bisnis (Service):** Controller UI memanggil `Service` untuk validasi, perhitungan, dan orkestrasi transaksi. Service bertanggung jawab terhadap manajemen transaksi (commit/rollback) dan mapping antar model.
- **Akses Data (DAO/JDBC):** DAO menggunakan `PreparedStatement` untuk operasi CRUD yang aman dan mengembalikan objek domain (`Product`, `Category`, `User`, `Transaction`). Koneksi dikelola oleh `DBConnection` (Singleton).
- **Integrasi & Error Handling:** Transaksi database dikelola pada layer service; exception ditangani dan diubah menjadi pesan user-friendly di UI. Logging disediakan untuk debugging dan audit.
- **Pola Desain:** MVC (pemecahan View/Controller/Model), DAO (abstraksi penyimpanan), Singleton (`DBConnection`), Factory (`DaoFactory`), dan Observer/Event (JavaFX listeners) untuk update UI reaktif.
- **Testing & Tools:** Unit test fokus pada `Service` dan `DAO` (menggunakan H2 in-memory atau Mockito), serta utilitas ekspor CSV untuk backup/data exchange.

Dokumentasi kode dan konvensi paket (`com.upb.kelolabarang.{model,dao,service,controller,util}`) membantu keterbacaan dan pemeliharaan.

---

## Hasil Pengujian
Pengujian sistem dilakukan pada tiga level utama: unit testing, integrasi, dan pengujian manual/demonstrasi.

- **Unit Testing:** Test unit ditulis menggunakan JUnit (dan Mockito untuk mock dependency) untuk `Service` dan `Dao`. Skenario utama yang diuji meliputi operasi CRUD, validasi input, dan perhitungan total transaksi. Hasil lokal: skenario utama lulus; test suite tersedia di folder `src/test/java`.

- **Integration Testing:** Pengujian integrasi menggunakan database in-memory (mis. H2) untuk memastikan DAO berinteraksi benar dengan skema database. Test mencakup manajemen transaksi (commit/rollback) dan konsistensi data pada operasi gabungan (mis. pembuatan transaksi + pengurangan stok).

- **Pengujian Manual / Demonstrasi:** Pengujian manual mencakup alur end-to-end:
  1. Login sebagai `admin` → akses manajemen produk.
  2. Tambah produk baru → periksa daftar produk dan status stok.
  3. Lakukan transaksi penjualan → periksa penurunan stok, pembuatan `Transaction` dan `TransactionItem`.
  4. Generate laporan penjualan dan ekspor CSV → verifikasi isi file CSV.
  5. Backup & Restore data → verifikasi data setelah restore.


| No | Test Class | Test Method | Status | Deskripsi |
|----:|------------|-------------|:------:|----------|
| 1 | ProductServiceTest | insertValidProduct | ✅ | Menyisipkan produk valid melalui service -> tersimpan di DAO |
| 2 | ProductServiceTest | insertInvalidCode | ✅ | Validasi menolak produk dengan kode kosong |
| 3 | ProductServiceTest | insertNegativePrice | ✅ | Menangani/menolak harga negatif sesuai kebijakan model/service |
| 4 | ProductServiceTest | updateStockValid | ✅ | Memperbarui stok produk berhasil dan tercermin di DAO |
| 5 | ProductServiceTest | updateStockNegative | ✅ | Menolak operasi update stok dengan nilai negatif |
| 6 | ProductServiceMoreTest | productFindAllAfterInsert | ✅ | `findAll()` mengembalikan semua produk setelah insert berganda |
| 7 | ProductServiceMoreTest | productDecreaseStockWorks | ✅ | `decreaseStock` mengurangi stok sesuai jumlah yang diminta |
| 8 | ProductServiceMoreTest | productDeleteRemoves | ✅ | Menghapus produk menghilangkan entri dari penyimpanan DAO |
| 9 | CartServiceTest | addItemValid | ✅ | Menambah item valid ke keranjang melalui CartService berhasil |
| 10 | CartServiceTest | addItemNotFound | ✅ | Menambah produk yang tidak ada memicu IllegalArgumentException |
| 11 | CartServiceTest | addItemInsufficientStock | ✅ | Menambah item melebihi stok ditolak |
| 12 | CartServiceTest | addItemInvalidQty | ✅ | Menambah dengan qty <=0 ditolak |
| 13 | CartServiceTest | updateQuantityValid | ✅ | Memperbarui quantity item berhasil & total terupdate |
| 14 | CartServiceTest | updateQuantityNegative | ✅ | Menolak update quantity negatif |
| 15 | CartServiceTest | saveCartReturnsId | ✅ | Menyimpan keranjang lewat service mengembalikan id (>= -1) |
| 16 | CartServiceTest | removeItemFromCartService | ✅ | removeItem lewat service menghapus entry cart |
| 17 | CartServiceTest | clearCartService | ✅ | clearCart() menghapus seluruh item di keranjang |
| 18 | CartServiceTest | getTotalPriceReflectsCart | ✅ | getTotalPrice() merefleksikan isi cart saat ini |
| 19 | CartServiceSaveTest | cartServiceSaveReturnsId | ✅ | Save cart via DAO mengembalikan cart id atau -1 pada kegagalan |
| 20 | CartServiceTest | testCartTotalPrice | ✅ | Kalkulasi total pada model cart (simple scenario) |
| 21 | CartServiceTest | testCartAddItem | ✅ | Menambah item ke cart model dan merge quantity |
| 22 | ProductModelTest | productProperties | ✅ | Verifikasi getter/setter dan atribut dasar produk |
| 23 | ProductModelTest | productSetters | ✅ | Setter bekerja dan memperbarui atribut produk |
| 24 | ProductModelTest | productNegativePriceAllowedInModel | ✅ | Model mengizinkan harga negatif (behavior diuji) |
| 25 | ProductModelTest | productStockProperty | ✅ | Properti stok tersimpan dan terbaca |
| 26 | ProductModelTest | productCodeNonEmpty | ✅ | Code produk tidak boleh kosong |
| 27 | CartTest | testAddNewItem | ✅ | Menambah item baru ke cart membuat entry baru |
| 28 | CartTest | testAddMergeQuantity | ✅ | Menambah produk sama menggabungkan quantity |
| 29 | CartTest | testRemoveItem | ✅ | removeItem menghapus entry produk |
| 30 | CartTest | testUpdateQuantity | ✅ | updateQuantity mengatur jumlah item |
| 31 | CartTest | testUpdateQuantityToZeroRemoves | ✅ | Mengatur qty ke 0 menghapus item |
| 32 | CartTest | testGetTotalPrice | ✅ | getTotalPrice menggabungkan subtotal item |
| 33 | CartModelMoreTest | cartClearRemovesAll | ✅ | clear() mengosongkan keranjang |
| 34 | CartModelMoreTest | cartGetTotalWithMultipleItems | ✅ | Total dihitung untuk beberapa item berbeda |

---

## Analisis Kualitas
Analisis kualitas dan keterpaduan OOP, database, dan GUI

- **Kualitas OOP:** Struktur kelas mematuhi prinsip enkapsulasi dan pemisahan tanggung jawab (model, service, controller). Penerapan pola desain (MVC, DAO, Singleton) meningkatkan keterbacaan dan keterpeliharaan, namun beberapa kelas masih bisa disederhanakan untuk mengurangi tanggung jawab ganda dan meningkatkan kohesi.

- **Kualitas Database:** Skema basis data ter-normalisasi untuk entitas utama (product, category, transaction, transaction_item). Penggunaan transaksi (commit/rollback) dan PreparedStatement menjaga konsistensi dan mengurangi risiko SQL injection. Rekomendasi: tambahkan indeks pada kolom yang sering dicari (nama produk, tanggal transaksi) dan sediakan strategi backup otomatis.

- **Kualitas GUI:** JavaFX dengan binding (`ObservableList`, Properties) membuat UI reaktif dan konsisten dengan model. Validasi input pada sisi-klien perlu diperkuat dan pesan error perlu dibuat lebih informatif untuk meningkatkan pengalaman pengguna.

- **Integrasi & Non-Fungsional:** Service layer mengelola transaksi dan penanganan error secara terpusat; logging memadai untuk debugging. Pengujian unit dan integrasi sudah ada tetapi cakupan perlu ditingkatkan—termasuk penambahan UI tests (TestFX) dan pengujian performa untuk skenario data besar.

- **Keamanan & Pemeliharaan:** Password disimpan secara hashed dan kontrol akses berbasis peran (admin/kasir) diimplementasikan. Untuk peningkatan, tambahkan enkripsi untuk konfigurasi sensitif, sanitasi input lebih ketat, dan audit trail untuk operasi kritis.

**Rekomendasi:** Tingkatkan validasi input (client + server), tambahkan indeks DB dan backup otomatis, perluas cakupan testing (unit, integrasi, UI), serta perkuat logging dan pesan error agar siap untuk deployment produksi.

---

## Kesimpulan

Proyek " Agripos Kelola Barang" berhasil dirancang dan diimplementasikan sebagai aplikasi desktop berbasis Java yang memenuhi tujuan utama: manajemen inventaris, pencatatan transaksi, dan pembuatan laporan yang dapat diekspor.

Arsitektur berlapis (MVC, Service, DAO) dan penerapan prinsip-prinsip OOP meningkatkan modularitas, keterbacaan, dan keterpeliharaan kode, sementara penggunaan pola desain seperti Singleton dan Factory membantu pengelolaan sumber daya dan ekstensi fitur.

Pengujian unit dan integrasi menunjukkan bahwa fungsi-fungsi inti (CRUD, manajemen stok, pembuatan transaksi, dan perhitungan total) bekerja sesuai harapan; pengujian manual juga memverifikasi alur end-to-end serta fitur ekspor CSV dan backup/restore.

Untuk meningkatkan kesiapan produksi disarankan memperkuat validasi input pada sisi klien dan server, menambahkan indeks dan mekanisme backup otomatis pada database, memperluas cakupan pengujian (termasuk UI tests), serta memperkuat logging dan kontrol akses untuk keamanan dan audit.

Secara keseluruhan, proyek ini memenuhi tujuan pembelajaran dan menyediakan fondasi yang solid untuk pengembangan lanjutan dan penerapan pada skala usaha kecil-menengah.

