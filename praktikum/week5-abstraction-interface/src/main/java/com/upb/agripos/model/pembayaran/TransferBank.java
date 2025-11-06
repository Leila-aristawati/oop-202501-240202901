package com.upb.agripos.model.pembayaran;


import com.upb.agripos.model.kontrak.Validatable;
import com.upb.agripos.model.pembayaran.Pembayaran;
import com.upb.agripos.model.kontrak.Receiptable;

public class TransferBank extends Pembayaran implements Validatable, Receiptable {
    private String rekeningTujuan;
    private String token; // kode keamanan 8 digit

    public TransferBank(String invoiceNo, double total, String rekeningTujuan, String token) {
        super(invoiceNo, total);
        this.rekeningTujuan = rekeningTujuan;
        this.token = token;
    }

    @Override
    public double biaya() {
        return 3500; // biaya tetap transfer bank
    }

    @Override
    public boolean validasi() {
        return token != null && token.length() == 8; // token harus 8 digit
    }

    @Override
    public boolean prosesPembayaran() {
        return validasi();
    }

    @Override
    public String cetakStruk() {
        return String.format("INVOICE %s | TOTAL+FEE: %.2f | TRANSFER BANK: Bank Jago  (%s) | STATUS: %s",
                invoiceNo, totalBayar(), rekeningTujuan,
                prosesPembayaran() ? "BERHASIL" : "GAGAL");
    }
}
