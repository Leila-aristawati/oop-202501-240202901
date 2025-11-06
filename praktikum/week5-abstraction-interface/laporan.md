# Laporan Praktikum Minggu 5
Topik: Abstraction (Abstract Class & Interface)

## Identitas
- Nama  : Leila Aristawati
- NIM   : 240202901
- Kelas : 3IKRB

---

## Tujuan
- Mahasiswa mampu **menjelaskan perbedaan abstract class dan interface**.
- Mahasiswa mampu **mendesain abstract class dengan method abstrak** sesuai kebutuhan kasus.
- Mahasiswa mampu **membuat interface dan mengimplementasikannya pada class**.
- Mahasiswa mampu **menerapkan multiple inheritance melalui interface** pada rancangan kelas.
- Mahasiswa mampu **mendokumentasikan kode** (komentar kelas/method, README singkat pada folder minggu).

---

## Dasar Teori
**Abstraksi** adalah proses menyederhanakan kompleksitas dengan menampilkan elemen penting dan menyembunyikan detail implementasi.
- **Abstract class**: tidak dapat diinstansiasi, dapat memiliki method abstrak (tanpa badan) dan non-abstrak. Dapat menyimpan state (field).
- **Interface**: kumpulan kontrak (method tanpa implementasi konkret). Sejak Java 8 mendukung default method. Mendukung **multiple inheritance** (class dapat mengimplementasikan banyak interface).
- Gunakan **abstract class** bila ada _shared state_ dan perilaku dasar; gunakan **interface** untuk mendefinisikan kemampuan/kontrak lintas hierarki.

Dalam konteks Agri-POS, **Pembayaran** dapat dimodelkan sebagai abstract class dengan method abstrak `prosesPembayaran()` dan `biaya()`. Implementasi konkritnya: `Cash` dan `EWallet`. Kemudian, interface seperti `Validatable` (mis. verifikasi OTP) dan `Receiptable` (mencetak bukti) dapat diimplementasikan oleh jenis pembayaran yang relevan.

---

## Langkah Praktikum
1. **Abstract Class – Pembayaran**
   - Buat `Pembayaran` (abstract) dengan field `invoiceNo`, `total` dan method:
     - `double biaya()` (abstrak) → biaya tambahan (fee).
     - `boolean prosesPembayaran()` (abstrak) → mengembalikan status berhasil/gagal.
     - `double totalBayar()` (konkrit) → `return total + biaya();`.

2. **Subclass Konkret**
   - `Cash` → biaya = 0, proses = selalu berhasil jika `tunai >= totalBayar()`.
   - `EWallet` → biaya = 1.5% dari `total`; proses = membutuhkan validasi.

3. **Interface**
   - `Validatable` → `boolean validasi();` (contoh: OTP).
   - `Receiptable` → `String cetakStruk();`

4. **Multiple Inheritance via Interface**
   - `EWallet` mengimplementasikan **dua interface**: `Validatable`, `Receiptable`.
   - `Cash` setidaknya mengimplementasikan `Receiptable`.

5. **Main Class**
    - Buat `MainAbstraction.java` untuk mendemonstrasikan pemakaian `Pembayaran` (polimorfik).
    - Tampilkan hasil proses dan struk. Di akhir, panggil `CreditBy.print("[NIM]", "[Nama]")`.

6. **Commit dan Push**
   - Commit dengan pesan: `week5-abstraction-interface`.


---

## Kode Program  

### Pembayaran.java (abstract)
```java
package com.upb.agripos.model.pembayaran;

public abstract class Pembayaran {
    protected String invoiceNo;
    protected double total;

    public Pembayaran(String invoiceNo, double total) {
        this.invoiceNo = invoiceNo;
        this.total = total;
    }

    public abstract double biaya();               // fee/biaya tambahan
    public abstract boolean prosesPembayaran();   // proses spesifik tiap metode

    public double totalBayar() {
        return total + biaya();
    }

    public String getInvoiceNo() { return invoiceNo; }
    public double getTotal() { return total; }
}
```

### Interface: Validatable & Receiptable
```java
package com.upb.agripos.model.kontrak;

public interface Validatable {
    boolean validasi(); // misal validasi OTP/ PIN
}
```
```java
package com.upb.agripos.model.kontrak;

public interface Receiptable {
    String cetakStruk();
}
```

### Cash.java (extends Pembayaran, implements Receiptable)
```java
package com.upb.agripos.model.pembayaran;

import com.upb.agripos.model.kontrak.Receiptable;

public class Cash extends Pembayaran implements Receiptable {
    private double tunai;

    public Cash(String invoiceNo, double total, double tunai) {
        super(invoiceNo, total);
        this.tunai = tunai;
    }

    @Override
    public double biaya() {
        return 0.0;
    }

    @Override
    public boolean prosesPembayaran() {
        return tunai >= totalBayar(); // sederhana: cukup uang tunai
    }

    @Override
    public String cetakStruk() {
        return String.format("INVOICE %s | TOTAL: %.2f | BAYAR CASH: %.2f | KEMBALI: %.2f",
                invoiceNo, totalBayar(), tunai, Math.max(0, tunai - totalBayar()));
    }
}
```

### EWallet.java (extends Pembayaran, implements Validatable & Receiptable)
```java
package com.upb.agripos.model.pembayaran;

import com.upb.agripos.model.kontrak.Validatable;
import com.upb.agripos.model.kontrak.Receiptable;

public class EWallet extends Pembayaran implements Validatable, Receiptable {
    private String akun;
    private String otp; // sederhana untuk simulasi

    public EWallet(String invoiceNo, double total, String akun, String otp) {
        super(invoiceNo, total);
        this.akun = akun;
        this.otp = otp;
    }

    @Override
    public double biaya() {
        return total * 0.015; // 1.5% fee
    }

    @Override
    public boolean validasi() {
        return otp != null && otp.length() == 6; // contoh validasi sederhana
    }

    @Override
    public boolean prosesPembayaran() {
        return validasi(); // jika validasi lolos, anggap berhasil
    }

    @Override
    public String cetakStruk() {
        return String.format("INVOICE %s | TOTAL+FEE: %.2f | E-WALLET: %s | STATUS: %s",
                invoiceNo, totalBayar(), akun, prosesPembayaran() ? "BERHASIL" : "GAGAL");
    }
}
```

### MainAbstraction.java
```java
package com.upb.agripos;

import com.upb.agripos.model.pembayaran.*;
import com.upb.agripos.model.kontrak.*;
import com.upb.agripos.util.CreditBy;

public class MainAbstraction {
    public static void main(String[] args) {
        Pembayaran cash = new Cash("INV-001", 100000, 120000);
        Pembayaran ew = new EWallet("INV-002", 150000, "user@ewallet", "123456");

        System.out.println(((Receiptable) cash).cetakStruk());
        System.out.println(((Receiptable) ew).cetakStruk());

    CreditBy.print("[NIM]", "[Nama Mahasiswa]");
    }
}
```

---

## Hasil Eksekusi  
![Screenshot hasil](./screenshots/hasil_week5.png)
---

## Analisis
- Jelaskan bagaimana kode berjalan.  
   Program menggunakan abstract class Pembayaran sebagai kerangka dasar untuk berbagai jenis pembayaran.Class Cash, EWallet, dan TransferBank mengimplementasikan method sesuai kebutuhan masing-masing.Interface Validatable digunakan untuk validasi seperti OTP atau token, dan Receiptable digunakan untuk mencetak struk.Setiap objek diproses lewat MainAbstraction, lalu mencetak hasil transaksi sesuai jenis pembayarannya.

- Apa perbedaan pendekatan minggu ini dibanding minggu sebelumnya.
   Minggu sebelumnya membahas polymorphism yang menekankan perbedaan perilaku method.Minggu ini fokus pada abstraction dan interface, yaitu menyembunyikan detail implementasi dan memaksa class turunan untuk mengikuti struktur tertentu.Dengan cara ini, program menjadi lebih terstruktur dan mudah dikembangkan.

- Kendala yang dihadapi dan cara mengatasinya.

   _

---

## Kesimpulan
Pada praktikum ini, konsep abstraction dan interface berhasil diterapkan untuk membuat sistem pembayaran yang fleksibel dan terstruktur.Dengan menggunakan abstract class sebagai kerangka umum dan interface sebagai kontrak perilaku, setiap jenis pembayaran dapat memiliki cara kerja sendiri tanpa mengubah struktur utama program.

---

## Quiz
1. Jelaskan perbedaan konsep dan penggunaan **abstract class** dan **interface**.  
   **Jawaban:** Abstract class digunakan sebagai kerangka dasar yang bisa berisi atribut dan method dengan atau tanpa isi, biasanya untuk class yang memiliki hubungan “is-a”.Sedangkan interface hanya berisi kontrak perilaku (method tanpa isi) yang harus diimplementasikan oleh class, cocok untuk fitur tambahan seperti validasi atau pencetakan struk.

2. Mengapa **multiple inheritance** lebih aman dilakukan dengan interface pada Java?  
   **Jawaban:** Karena interface tidak membawa data atau implementasi, hanya deklarasi method.Hal ini mencegah konflik antar class dan membuat Java bisa menggabungkan beberapa interface tanpa ambiguitas, berbeda dengan pewarisan ganda dari class yang bisa menimbulkan bentrok kode.

3. Pada contoh Agri-POS, bagian mana yang **paling tepat** menjadi abstract class dan mana yang menjadi interface? Jelaskan alasannya.  
   **Jawaban:** Class Pembayaran tepat dijadikan abstract class karena berfungsi sebagai dasar semua jenis pembayaran dengan atribut dan method umum seperti total dan totalBayar().Sedangkan Validatable dan Receiptable dijadikan interface karena hanya mendefinisikan perilaku tambahan (validasi dan cetak struk) yang bisa diterapkan oleh berbagai jenis pembayaran tanpa harus punya struktur data yang sama
 
