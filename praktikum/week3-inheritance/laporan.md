# Laporan Praktikum Minggu 3
Topik: Inheritance (Kategori Produk)

## Identitas
- Nama  : [Leila Aristawati]
- NIM   : [240202901]
- Kelas : [3IKRB]

---

## Tujuan
-Mahasiswa mampu menjelaskan konsep inheritance (pewarisan class) dalam OOP.

-Mahasiswa mampu membuat superclass dan subclass untuk produk pertanian.

-Mahasiswa mampu mendemonstrasikan hierarki class melalui contoh kode.

-Mahasiswa mampu menggunakan super untuk memanggil konstruktor dan method parent class.

-Mahasiswa mampu membuat laporan praktikum yang menjelaskan perbedaan penggunaan inheritance dibanding class tunggal.

---

## Dasar Teori
Inheritance adalah mekanisme dalam OOP yang memungkinkan suatu class mewarisi atribut dan method dari class lain.

   -Superclass: class induk yang mendefinisikan atribut umum.

   -Subclass: class turunan yang mewarisi atribut/method superclass, dan dapat menambahkan atribut/method baru.

   -super digunakan untuk memanggil konstruktor atau method superclass.

Dalam konteks Agri-POS, kita dapat membuat class Produk sebagai superclass, kemudian Benih, Pupuk, dan AlatPertanian sebagai subclass. Hal ini membuat kode lebih reusable dan terstruktur.

---

## Langkah Praktikum
1. Membuat Superclass Produk, dengan menggunakan class produk dari bab 2  
2. Membuat Subclass

   -Benih.java → atribut tambahan: varietas.

   -Pupuk.java → atribut tambahan: jenis pupuk (Urea, NPK, dll).

   -AlatPertanian.java → atribut tambahan: material (baja, kayu, plastik). 
3. Membuat Main Class

   -Instansiasi minimal satu objek dari tiap subclass.

   -Tampilkan data produk dengan memanfaatkan inheritance.

4. Menambahkan CreditBy

   -Panggil class CreditBy untuk menampilkan identitas mahasiswa.

5. Commit dan Push

   -Commit dengan pesan: week3-inheritance.

---

## Kode Program
### Benih.java


```Benih.java
package com.upb.agripos.model;

public class Benih extends Produk{
    private String varietas;

    public Benih(String kode, String nama, double harga, int stok, String varietas) {
        super(kode, nama, harga, stok);
        this.varietas = varietas;
    }

    public String getVarietas() { 
        return varietas; 
    }
    public void setVarietas(String varietas) { 
        this.varietas = varietas; 
    }
    @Override
    public void tampilkanInfo() {
        super.tampilkanInfo(); // tampilkan info dari Produk
        System.out.println("Varietas: " + varietas);
    }
}
```
### AlatPertanian.java

```AlatPertanian.java
package com.upb.agripos.model;

public class AlatPertanian extends Produk {
    private String material;

    public AlatPertanian(String kode, String nama, double harga, int stok, String material) {
        super(kode, nama, harga, stok);
        this.material = material;
    }

    public String getMaterial() { 
        return material; 
    }
    public void setMaterial(String material) { 
        this.material = material; 
    }
    @Override
    public void tampilkanInfo() {
        super.tampilkanInfo();
        System.out.println("Material: " + material);
    }
}
```
### Pupuk.java

```Pupuk.java
package com.upb.agripos.model;

public class Pupuk extends Produk {
    private String jenis;

    public Pupuk(String kode, String nama, double harga, int stok, String jenis) {
        super(kode, nama, harga, stok);
        this.jenis = jenis;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    @Override
    public void tampilkanInfo() {
        super.tampilkanInfo();
        System.out.println("Jenis Pupuk: " + jenis);
    }
}
```
### MainInheritance.java

```MainInheritance.java
package com.upb.agripos;

import com.upb.agripos.model.*;
import com.upb.agripos.util.CreditBy;

public class MainInheritance {
    public static void main(String[] args) {
        Benih b = new Benih("BNH-001", "Benih Padi IR64", 25000, 100, "IR64");
        Pupuk p = new Pupuk("PPK-101", "Pupuk Urea", 350000, 40, "Urea");
        AlatPertanian a = new AlatPertanian("ALT-501", "Cangkul Baja", 90000, 15, "Baja");

        System.out.println("\n=== Data Produk Pertanian ===");
        b.tampilkanInfo();
        System.out.println();
        p.tampilkanInfo();
        System.out.println();
        a.tampilkanInfo();

        CreditBy.print("<240202901>", "<Leila Aristawati>");
    }
}
```

---

## Hasil Eksekusi 
![Screenshot hasil](/screenshots/week3-inheritance.png)
---

## Analisis
- Jelaskan bagaimana kode berjalan. 

   Program menggunakan konsep inheritance, di mana class Produk menjadi superclass berisi atribut umum (kode, nama, harga, stok). Subclass Benih, Pupuk, dan AlatPertanian mewarisi class Produk dan menambah atribut khusus. Method tampilkanInfo() digunakan untuk menampilkan data tiap produk, lalu identitas mahasiswa ditampilkan lewat CreditBy.

- Apa perbedaan pendekatan minggu ini dibanding minggu sebelumnya.

   Minggu sebelumnya semua class berdiri sendiri. Minggu ini menggunakan inheritance sehingga kode lebih ringkas dan bisa digunakan ulang tanpa menulis ulang atribut atau method
- Kendala yang dihadapi dan cara mengatasinya. 
 
   -Error override → ditambah method tampilkanInfo() di Produk.

   -Output ganda → hapus pemanggilan duplikat di MainInheritance.

   -File Produk belum terbaca → copy dari Bab 2 ke folder Bab 3.)
---

## Kesimpulan
Inheritance membuat kode lebih efisien dan mudah dikembangkan karena subclass dapat mewarisi atribut dan method dari superclass.

---

## Quiz
1. [Apa keuntungan menggunakan inheritance dibanding membuat class terpisah tanpa hubungan?]  
   **Jawaban: Inheritance membuat kode lebih efisien dan mudah dikelola karena subclass dapat mewarisi atribut dan method dari superclass tanpa perlu menulis ulang. Selain itu, program jadi lebih terstruktur dan mudah dikembangkan**

2. [Bagaimana cara subclass memanggil konstruktor superclass?]  
   **Jawaban: Subclass memanggil konstruktor superclass menggunakan keyword super() di dalam konstruktor miliknya.**

3. [Berikan contoh kasus di POS pertanian selain Benih, Pupuk, dan Alat Pertanian yang bisa dijadikan subclass.]  
   **Jawaban: Contohnya ObatTanaman (menyimpan atribut jenis bahan aktif dan dosis pemakaian) atau BibitHewan (dengan atribut jenis hewan dan usia bibit)**
