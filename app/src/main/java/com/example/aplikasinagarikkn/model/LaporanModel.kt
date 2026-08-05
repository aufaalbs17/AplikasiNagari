package com.example.aplikasinagarikkn.model

/**
 * Data model for citizen complaint reports (Laporan Pengaduan Warga).
 */
data class LaporanModel(
    val id: Int = 0,
    val userId: String = "",
    val pelaporNama: String = "",
    val pelaporNik: String = "",
    val judul: String = "",
    val kategori: String = "Fasilitas Umum",
    val deskripsi: String = "",
    val tanggal: String = "",
    val status: String = "Menunggu", // "Menunggu", "Diproses", "Selesai"
    val tanggapanAdmin: String = "",
    val fotoUri: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val lokasiAlamat: String = ""
)
