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
    val dbLaporanList by FirebaseRepository.laporanListState.collectAsState()
    val dbSuratList by FirebaseRepository.suratListState.collectAsState()

    // Generate real-time dynamic notifications list from Database State
    val dynamicNotifikasiList = mutableListOf<Notifikasi>()

    // 1. Convert Laporan items into notifications
    dbLaporanList.forEach { laporan ->
        val notifJudul = when (laporan.status) {
            "Menunggu" -> "🚨 Laporan Pengaduan Baru Masuk"
            "Diproses" -> "🔄 Status Laporan: Sedang Diproses"
            "Selesai" -> "✅ Status Laporan: Tuntas Selesai"
            else -> "📢 Pembaruan Laporan Pengaduan"
        }

        val notifPesan = when (laporan.status) {
            "Menunggu" -> "Laporan '${laporan.judul}' dari warga a.n ${laporan.pelaporNama} telah diterima sistem dan menunggu tindakan Ibu Wali Nagari."
            else -> "Laporan '${laporan.judul}' (Pelapor: ${laporan.pelaporNama}) statusnya kini '${laporan.status}'. Tanggapan: ${laporan.tanggapanAdmin.ifBlank { "Tanggapan resmi tercatat di sistem." }}"
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
    dbSuratList.forEach { surat ->
        val notifJudul = when (surat.status) {
            "Diajukan" -> "📜 Permohonan Surat Baru Masuk"
            "Ditinjau Wali" -> "🔍 Surat Sedang Ditinjau Wali Nagari"
            "Selesai", "Disetujui" -> "✅ Permohonan Surat Disetujui"
            "Ditolak" -> "⚠️ Permohonan Surat Ditolak"
            else -> "📜 Pembaruan Pengajuan Surat"
        }

        val notifPesan = when (surat.status) {
            "Diajukan" -> "Warga a.n ${surat.pemohonNama} (NIK: ${surat.pemohonNik}) baru saja mengajukan permohonan '${surat.jenisSurat}'."
            else -> "Permohonan '${surat.jenisSurat}' a.n ${surat.pemohonNama} statusnya kini '${surat.status}'. Keterangan: ${surat.keterangan}"
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
