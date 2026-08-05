package com.example.aplikasinagarikkn.model

/**
 * Data model for digital letter applications (Pengajuan Surat Digital).
 */
data class SuratModel(
    val id: Int = 0,
    val userId: String = "",
    val pemohonNama: String = "",
    val pemohonNik: String = "",
    val jenisSurat: String = "",
    val keperluan: String = "",
    val tanggal: String = "",
    val status: String = "Diajukan", // "Diajukan", "Ditinjau", "Disetujui", "Ditolak", "Selesai"
    val keterangan: String = "",
    val lampiranUri: String? = null
)
