package com.example.aplikasinagarikkn.model

/**
 * Data model for User and Admin accounts in Nagari Sako Selatan Pasia Talang system.
 */
data class UserAccount(
    val id: String = "",
    val nama: String = "",
    val nik: String = "",
    val email: String = "",
    val noHp: String = "",
    val alamatJorong: String = "",
    val role: String = "warga", // "warga" or "admin"
    val avatarUrl: String? = null
)

object DefaultAccounts {
    val defaultWarga = UserAccount(
        id = "warga_101",
        nama = "Budi Santoso",
        nik = "1303012807980001",
        email = "warga@nagari.go.id",
        noHp = "0812-3456-7890",
        alamatJorong = "Jorong Pasia, RT 02",
        role = "warga"
    )

    val defaultAdmin = UserAccount(
        id = "admin_999",
        nama = "Ibu Wali Nagari",
        nik = "1303010000000001",
        email = "admin@nagari.go.id",
        noHp = "0821-7489-9901",
        alamatJorong = "Kantor Wali Nagari Sako Selatan",
        role = "admin"
    )
}
