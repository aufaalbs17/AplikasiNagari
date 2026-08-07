package com.example.aplikasinagarikkn.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aplikasinagarikkn.data.FirebaseRepository

private val EmeraldDark = Color(0xFF064E3B)
private val EmeraldMedium = Color(0xFF047857)
private val BorderSubtle = Color(0xFFE2E8F0)

data class Notifikasi(
    val id: Int,
    val judul: String,
    val pesan: String,
    val waktu: String,
    val kategori: String = "Sistem"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotifikasiScreen(
    onNavigateBack: () -> Unit
) {
    val currentUser by FirebaseRepository.currentUserState.collectAsState()
    val dbLaporanList by FirebaseRepository.laporanListState.collectAsState()
    val dbSuratList by FirebaseRepository.suratListState.collectAsState()

    val isAdmin = currentUser.role == "Admin"

    // Filter Database records based on logged-in user
    val filteredLaporanList = if (isAdmin) {
        dbLaporanList
    } else {
        dbLaporanList.filter { laporan ->
            laporan.userId == currentUser.id ||
            laporan.pelaporNama.equals(currentUser.nama, ignoreCase = true) ||
            laporan.pelaporNik == currentUser.nik ||
            currentUser.id == "warga_101"
        }
    }

    val filteredSuratList = if (isAdmin) {
        dbSuratList
    } else {
        dbSuratList.filter { surat ->
            surat.userId == currentUser.id ||
            surat.pemohonNama.equals(currentUser.nama, ignoreCase = true) ||
            surat.pemohonNik == currentUser.nik ||
            currentUser.id == "warga_101"
        }
    }

    // Generate real-time dynamic notifications list from Database State
    val dynamicNotifikasiList = mutableListOf<Notifikasi>()

    // 1. Convert Laporan items into notifications
    filteredLaporanList.forEach { laporan ->
        val notifJudul = if (isAdmin) {
            when (laporan.status) {
                "Menunggu" -> "🚨 Laporan Pengaduan Baru Masuk"
                "Diproses" -> "🔄 Status Laporan: Sedang Diproses"
                "Selesai" -> "✅ Status Laporan: Tuntas Selesai"
                else -> "📢 Pembaruan Laporan Pengaduan"
            }
        } else {
            when (laporan.status) {
                "Menunggu" -> "🚨 Laporan Anda Berhasil Dikirim"
                "Diproses" -> "🔄 Laporan Anda Sedang Diproses"
                "Selesai" -> "✅ Laporan Anda Tuntas Selesai"
                else -> "📢 Pembaruan Status Laporan Anda"
            }
        }

        val notifPesan = if (isAdmin) {
            when (laporan.status) {
                "Menunggu" -> "Laporan '${laporan.judul}' dari warga a.n ${laporan.pelaporNama} telah diterima sistem dan menunggu tindakan Ibu Wali Nagari."
                else -> "Laporan '${laporan.judul}' (Pelapor: ${laporan.pelaporNama}) statusnya kini '${laporan.status}'. Tanggapan: ${laporan.tanggapanAdmin.ifBlank { "Tanggapan resmi tercatat di sistem." }}"
            }
        } else {
            when (laporan.status) {
                "Menunggu" -> "Laporan Anda '${laporan.judul}' telah diterima sistem dan sedang menunggu tindakan Ibu Wali Nagari."
                "Diproses" -> "Laporan Anda '${laporan.judul}' sedang ditindaklanjuti oleh Perangkat Nagari. Tanggapan: ${laporan.tanggapanAdmin.ifBlank { "Sedang dalam proses penanganan di lapangan." }}"
                "Selesai" -> "Laporan Anda '${laporan.judul}' telah selesai ditangani. Tanggapan Resmi: ${laporan.tanggapanAdmin.ifBlank { "Masalah telah dituntaskan di lapangan." }}"
                else -> "Laporan Anda '${laporan.judul}' statusnya kini '${laporan.status}'."
            }
        }

        val kategori = if (laporan.status == "Menunggu") "Urgent" else "Pengaduan"

        dynamicNotifikasiList.add(
            Notifikasi(
                id = laporan.id,
                judul = notifJudul,
                pesan = notifPesan,
                waktu = laporan.tanggal.ifBlank { "Baru saja" },
                kategori = kategori
            )
        )
    }

    // 2. Convert Surat items into notifications
    filteredSuratList.forEach { surat ->
        val notifJudul = if (isAdmin) {
            when (surat.status) {
                "Diajukan" -> "📜 Permohonan Surat Baru Masuk"
                "Ditinjau Wali" -> "🔍 Surat Sedang Ditinjau Wali Nagari"
                "Selesai", "Disetujui" -> if (surat.metodePengambilan == "Digital") "📥 Surat Selesai - File Digital Siap Diunduh" else "🏢 Surat Selesai - Siap Diambil di Kantor Nagari"
                "Ditolak" -> "⚠️ Permohonan Surat Ditolak"
                else -> "📜 Pembaruan Pengajuan Surat"
            }
        } else {
            when (surat.status) {
                "Diajukan" -> "📜 Permohonan Surat Anda Diterima"
                "Ditinjau Wali" -> "🔍 Surat Anda Sedang Ditinjau"
                "Selesai", "Disetujui" -> if (surat.metodePengambilan == "Digital") "📥 Surat Selesai - File Digital Siap Diunduh" else "🏢 Surat Selesai - Siap Diambil di Kantor Nagari"
                "Ditolak" -> "⚠️ Permohonan Surat Anda Ditolak"
                else -> "📜 Pembaruan Pengajuan Surat Anda"
            }
        }

        val notifPesan = if (isAdmin) {
            when (surat.status) {
                "Diajukan" -> "Warga a.n ${surat.pemohonNama} (NIK: ${surat.pemohonNik}) baru saja mengajukan permohonan '${surat.jenisSurat}'."
                "Selesai", "Disetujui" -> "Permohonan '${surat.jenisSurat}' a.n ${surat.pemohonNama} telah disetujui (${if (surat.metodePengambilan == "Digital") "Opsi: Unduh File Digital" else "Opsi: Ambil Fisik di Kantor Nagari"})."
                else -> "Permohonan '${surat.jenisSurat}' a.n ${surat.pemohonNama} statusnya kini '${surat.status}'. Keterangan: ${surat.keterangan}"
            }
        } else {
            when (surat.status) {
                "Diajukan" -> "Permohonan '${surat.jenisSurat}' Anda telah dikirim ke sistem dan sedang menunggu verifikasi Ibu Wali Nagari."
                "Ditinjau Wali" -> "Permohonan '${surat.jenisSurat}' Anda kini dalam proses peninjauan & verifikasi oleh Ibu Wali Nagari."
                "Selesai", "Disetujui" -> if (surat.metodePengambilan == "Digital") {
                    "Permohonan '${surat.jenisSurat}' Anda telah disetujui Ibu Wali Nagari. File surat digital (.pdf) sudah dapat Anda unduh langsung di menu Surat."
                } else {
                    "Permohonan '${surat.jenisSurat}' Anda telah disetujui Ibu Wali Nagari. Silakan ambil berkas fisik dokumen di Kantor Nagari Sako Selatan (Jam Kerja 08.00 - 15.00 WIB)."
                }
                "Ditolak" -> "Permohonan '${surat.jenisSurat}' Anda belum dapat disetujui. Catatan: ${surat.keterangan}"
                else -> "Permohonan '${surat.jenisSurat}' Anda statusnya kini '${surat.status}'."
            }
        }

        dynamicNotifikasiList.add(
            Notifikasi(
                id = surat.id + 100000,
                judul = notifJudul,
                pesan = notifPesan,
                waktu = surat.tanggal.ifBlank { "Baru saja" },
                kategori = "Surat"
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Notifikasi & Pemberitahuan",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Pusat Informasi Nagari Sako Selatan",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        if (dynamicNotifikasiList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Belum ada pemberitahuan notifikasi.",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8FAFC))
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(dynamicNotifikasiList) { notif ->
                    ExecutiveNotifikasiCard(notif)
                }
            }
        }
    }
}

@Composable
fun ExecutiveNotifikasiCard(notif: Notifikasi) {
    val (accentColor, categoryBg) = when (notif.kategori) {
        "Urgent" -> Pair(Color(0xFFDC2626), Color(0xFFFEF2F2))
        "Surat" -> Pair(Color(0xFF2563EB), Color(0xFFEFF6FF))
        else -> Pair(EmeraldMedium, Color(0xFFECFDF5))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderSubtle),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(110.dp)
                    .background(accentColor)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(categoryBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.NotificationsActive,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = categoryBg,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = notif.kategori,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = accentColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = notif.waktu,
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = notif.judul,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF0F172A),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = notif.pesan,
                        fontSize = 11.sp,
                        color = Color(0xFF475569),
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotifikasiPreview() {
    MaterialTheme {
        NotifikasiScreen({})
    }
}
