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
    val status: String = "Diajukan", // "Diajukan", "Ditinjau Wali", "Selesai", "Disetujui", "Ditolak"
    val keterangan: String = "",
    val lampiranUri: String? = null,
    val metodePengambilan: String = "Digital", // "Digital" (Unduh File) vs "Fisik" (Ambil di Kantor Nagari)
    val fileHasilUri: String? = null
)
