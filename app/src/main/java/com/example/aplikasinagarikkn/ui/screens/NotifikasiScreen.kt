package com.example.aplikasinagarikkn.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    val targetId: Int = 0,
    val type: String = "Laporan",
    val judul: String,
    val pesan: String,
    val waktu: String,
    val kategori: String = "Sistem",
    val timestampMs: Long = 0L
)

private fun parseWaktuToMillis(waktu: String, stageOffsetMs: Long = 0L): Long {
    return try {
        val cleanWaktu = waktu.replace(" WIB", "").trim()
        val formatFull = java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale.forLanguageTag("id-ID"))
        val formatShort = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.forLanguageTag("id-ID"))
        val date = try {
            formatFull.parse(cleanWaktu)
        } catch (e: Exception) {
            formatShort.parse(cleanWaktu)
        }
        (date?.time ?: System.currentTimeMillis()) + stageOffsetMs
    } catch (e: Exception) {
        System.currentTimeMillis() + stageOffsetMs
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotifikasiScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDetailLaporan: (Int) -> Unit = {},
    onNavigateToRiwayatLaporan: () -> Unit = {},
    isAdminMode: Boolean = false
) {
    val currentUser by FirebaseRepository.currentUserState.collectAsState()
    val dbLaporanList by FirebaseRepository.laporanListState.collectAsState()
    val dbSuratList by FirebaseRepository.suratListState.collectAsState()

    val isAdmin = isAdminMode || currentUser.role == "Admin"
    var selectedFilterCategory by remember { mutableStateOf("Semua") }

    // Filter Database records based on logged-in user with safe fallback
    val filteredLaporanList = if (isAdmin) {
        dbLaporanList
    } else {
        val userLaporans = dbLaporanList.filter { laporan ->
            laporan.userId == currentUser.id ||
            laporan.pelaporNama.equals(currentUser.nama, ignoreCase = true) ||
            laporan.pelaporNik == currentUser.nik ||
            currentUser.id == "warga_101"
        }
        if (userLaporans.isNotEmpty()) userLaporans else dbLaporanList
    }

    val filteredSuratList = if (isAdmin) {
        dbSuratList
    } else {
        val userSurats = dbSuratList.filter { surat ->
            surat.userId == currentUser.id ||
            surat.pemohonNama.equals(currentUser.nama, ignoreCase = true) ||
            surat.pemohonNik == currentUser.nik ||
            currentUser.id == "warga_101"
        }
        if (userSurats.isNotEmpty()) userSurats else dbSuratList
    }

    // Generate real-time dynamic notifications list from Database State
    val dynamicNotifikasiList = mutableListOf<Notifikasi>()

    // 1. Convert Laporan items into multi-stage notifications
    filteredLaporanList.forEach { laporan ->
        val timeMs = parseWaktuToMillis(laporan.tanggal)

        // Stage 1: Always add "Laporan Pengaduan Baru Masuk"
        val stage1Judul = if (isAdmin) "🚨 Laporan Pengaduan Baru Masuk" else "🚨 Laporan Anda Berhasil Dikirim"
        val stage1Pesan = if (isAdmin) {
            "Laporan '${laporan.judul}' dari warga a.n ${laporan.pelaporNama} (NIK: ${laporan.pelaporNik}) telah masuk ke sistem dan menunggu tindakan Ibu Wali."
        } else {
            "Laporan Anda '${laporan.judul}' telah diterima sistem dan sedang menunggu tindakan Ibu Wali Nagari."
        }

        dynamicNotifikasiList.add(
            Notifikasi(
                id = laporan.id * 10 + 1,
                targetId = laporan.id,
                type = "Laporan",
                judul = stage1Judul,
                pesan = stage1Pesan,
                waktu = laporan.tanggal.ifBlank { "05 Agu 2026" },
                kategori = if (isAdmin) "Pengaduan Masuk" else "Laporan Anda",
                timestampMs = timeMs + 1000L
            )
        )

        // Stage 2: If status is "Diproses" or "Selesai", add "Sedang Diproses" notification
        if (laporan.status == "Diproses" || laporan.status == "Selesai") {
            val stage2Judul = if (isAdmin) "🔄 Laporan Warga Dalam Penanganan" else "🔄 Laporan Anda Sedang Diproses"
            val stage2Pesan = if (isAdmin) {
                "Laporan '${laporan.judul}' (Pelapor: ${laporan.pelaporNama}) statusnya kini 'Diproses'. Tanggapan: ${laporan.tanggapanAdmin.ifBlank { "Sedang ditindaklanjuti Perangkat Nagari di lapangan." }}"
            } else {
                "Laporan Anda '${laporan.judul}' sedang ditindaklanjuti Perangkat Nagari. Tanggapan: ${laporan.tanggapanAdmin.ifBlank { "Sedang dalam proses penanganan di lapangan." }}"
            }

            dynamicNotifikasiList.add(
                Notifikasi(
                    id = laporan.id * 10 + 2,
                    targetId = laporan.id,
                    type = "Laporan",
                    judul = stage2Judul,
                    pesan = stage2Pesan,
                    waktu = laporan.tanggal.ifBlank { "05 Agu 2026" },
                    kategori = if (isAdmin) "Pengaduan" else "Laporan Anda",
                    timestampMs = timeMs + 60_000L
                )
            )
        }

        // Stage 3: If status is "Selesai", add "Tuntas Selesai" notification
        if (laporan.status == "Selesai") {
            val stage3Judul = if (isAdmin) "✅ Laporan Warga Tuntas Selesai" else "✅ Laporan Anda Tuntas Selesai"
            val stage3Pesan = if (isAdmin) {
                "Laporan '${laporan.judul}' (Pelapor: ${laporan.pelaporNama}) statusnya kini 'Selesai'. Tanggapan resmi & bukti foto penanganan telah berhasil dikirim ke akun warga."
            } else {
                "Laporan Anda '${laporan.judul}' telah tuntas diselesaikan oleh Perangkat Nagari. Tanggapan Resmi: ${laporan.tanggapanAdmin.ifBlank { "Masalah telah dituntaskan di lokasi." }}. Silakan buka menu Riwayat Laporan untuk melihat tanggapan Ibu Wali & foto bukti penanganan lapangan."
            }

            dynamicNotifikasiList.add(
                Notifikasi(
                    id = laporan.id * 10 + 3,
                    targetId = laporan.id,
                    type = "Laporan",
                    judul = stage3Judul,
                    pesan = stage3Pesan,
                    waktu = laporan.tanggal.ifBlank { "05 Agu 2026" },
                    kategori = if (isAdmin) "Pengaduan" else "Laporan Anda",
                    timestampMs = timeMs + 120_000L
                )
            )
        }
    }

    // 2. Convert Surat items into multi-stage notifications
    filteredSuratList.forEach { surat ->
        val timeMs = parseWaktuToMillis(surat.tanggal)

        // Stage 1: Always add "Permohonan Surat Baru Masuk"
        val stage1Judul = if (isAdmin) "📜 Permohonan Surat Masuk dari Warga" else "📜 Permohonan Surat Anda Diterima"
        val stage1Pesan = if (isAdmin) {
            "Warga a.n ${surat.pemohonNama} (NIK: ${surat.pemohonNik}) mengajukan permohonan '${surat.jenisSurat}'."
        } else {
            "Permohonan '${surat.jenisSurat}' Anda telah dikirim ke sistem dan sedang menunggu verifikasi Ibu Wali Nagari."
        }

        dynamicNotifikasiList.add(
            Notifikasi(
                id = (surat.id + 10000) * 10 + 1,
                targetId = surat.id,
                type = "Surat",
                judul = stage1Judul,
                pesan = stage1Pesan,
                waktu = surat.tanggal.ifBlank { "26 Juli 2026" },
                kategori = if (isAdmin) "Surat Masuk" else "Surat Anda",
                timestampMs = timeMs + 500L
            )
        )

        // Stage 2: If status is "Ditinjau Wali", "Disetujui", or "Selesai", add "Ditinjau" notification
        if (surat.status == "Ditinjau Wali" || surat.status == "Disetujui" || surat.status == "Selesai") {
            val stage2Judul = if (isAdmin) "🔍 Permohonan Surat Dalam Peninjauan" else "🔍 Surat Anda Sedang Ditinjau"
            val stage2Pesan = if (isAdmin) {
                "Permohonan '${surat.jenisSurat}' a.n ${surat.pemohonNama} sedang ditinjau oleh Ibu Wali Nagari. Keterangan: ${surat.keterangan.ifBlank { "Sedang verifikasi berkas." }}"
            } else {
                "Permohonan '${surat.jenisSurat}' Anda kini dalam proses peninjauan & verifikasi oleh Ibu Wali Nagari."
            }

            dynamicNotifikasiList.add(
                Notifikasi(
                    id = (surat.id + 10000) * 10 + 2,
                    targetId = surat.id,
                    type = "Surat",
                    judul = stage2Judul,
                    pesan = stage2Pesan,
                    waktu = surat.tanggal.ifBlank { "27 Juli 2026" },
                    kategori = if (isAdmin) "Surat Masuk" else "Surat Anda",
                    timestampMs = timeMs + 60_000L
                )
            )
        }

        // Stage 3: If status is "Selesai" or "Disetujui", add "Selesai" notification
        if (surat.status == "Selesai" || surat.status == "Disetujui") {
            val isDigital = surat.metodePengambilan == "Digital"
            val stage3Judul = if (isAdmin) {
                "✅ Permohonan Surat Warga Disetujui"
            } else {
                if (isDigital) "📥 Surat Selesai - File Digital Siap Diunduh" else "🏢 Surat Selesai - Siap Diambil di Kantor Nagari"
            }
            val stage3Pesan = if (isAdmin) {
                "Permohonan '${surat.jenisSurat}' a.n ${surat.pemohonNama} telah disetujui Ibu Wali Nagari (${if (isDigital) "Opsi: Unduh File Digital" else "Opsi: Ambil Fisik di Kantor Nagari"})."
            } else {
                if (isDigital) {
                    "Permohonan '${surat.jenisSurat}' Anda telah disetujui Ibu Wali Nagari. File surat digital (.pdf) sudah dapat Anda unduh langsung di menu Surat."
                } else {
                    "Permohonan '${surat.jenisSurat}' Anda telah disetujui Ibu Wali Nagari. Silakan ambil berkas fisik dokumen di Kantor Nagari Sako Selatan (Jam Kerja 08.00 - 15.00 WIB)."
                }
            }

            dynamicNotifikasiList.add(
                Notifikasi(
                    id = (surat.id + 10000) * 10 + 3,
                    targetId = surat.id,
                    type = "Surat",
                    judul = stage3Judul,
                    pesan = stage3Pesan,
                    waktu = surat.tanggal.ifBlank { "05 Agu 2026" },
                    kategori = if (isAdmin) "Surat Masuk" else "Surat Anda",
                    timestampMs = timeMs + 120_000L
                )
            )
        }

        // Stage 3 Alt: If status is "Ditolak"
        if (surat.status == "Ditolak") {
            val stage3AltJudul = if (isAdmin) "⚠️ Permohonan Surat Ditolak" else "⚠️ Permohonan Surat Anda Ditolak"
            val stage3AltPesan = if (isAdmin) {
                "Permohonan '${surat.jenisSurat}' a.n ${surat.pemohonNama} statusnya kini 'Ditolak'. Catatan: ${surat.keterangan}"
            } else {
                "Permohonan '${surat.jenisSurat}' Anda belum dapat disetujui. Catatan: ${surat.keterangan}"
            }

            dynamicNotifikasiList.add(
                Notifikasi(
                    id = (surat.id + 10000) * 10 + 3,
                    targetId = surat.id,
                    type = "Surat",
                    judul = stage3AltJudul,
                    pesan = stage3AltPesan,
                    waktu = surat.tanggal.ifBlank { "05 Agu 2026" },
                    kategori = if (isAdmin) "Surat Masuk" else "Surat Anda",
                    timestampMs = timeMs + 120_000L
                )
            )
        }
    }

    // Sort notifications strictly by most recent timestamp descending (terbaru di paling atas)
    dynamicNotifikasiList.sortByDescending { it.timestampMs }

    // Apply category filter chip selection (Only Pengaduan & Surat)
    val displayedNotifikasiList = dynamicNotifikasiList.filter { notif ->
        when (selectedFilterCategory) {
            "Pengaduan" -> notif.type == "Laporan"
            "Surat" -> notif.type == "Surat"
            else -> true
        }
    }

    val screenTitle = if (isAdmin) "Pusat Notifikasi Admin" else "Notifikasi & Pemberitahuan Warga"
    val screenSubtitle = if (isAdmin) "Panel Ibu Wali Nagari • Pengawasan Masuk" else "Layanan Warga • Progress Laporan & Surat Anda"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = screenTitle,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = screenSubtitle,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8FAFC))
        ) {
            // Category Filter Chips (Only 3 options: Semua, Pengaduan, Surat)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Semua", "Pengaduan", "Surat").forEach { category ->
                    val isSelected = selectedFilterCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilterCategory = category },
                        label = {
                            Text(
                                text = category,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldDark,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            if (displayedNotifikasiList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada pemberitahuan notifikasi (${selectedFilterCategory}).",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(displayedNotifikasiList) { notif ->
                        ExecutiveNotifikasiCard(
                            notif = notif,
                            isAdmin = isAdmin,
                            onClick = {
                                if (notif.type == "Laporan" && notif.targetId > 0) {
                                    if (isAdmin) {
                                        onNavigateToDetailLaporan(notif.targetId)
                                    } else {
                                        onNavigateToRiwayatLaporan()
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExecutiveNotifikasiCard(
    notif: Notifikasi,
    isAdmin: Boolean = false,
    onClick: () -> Unit = {}
) {
    val (accentColor, categoryBg) = when (notif.kategori) {
        "Urgent" -> Pair(Color(0xFFDC2626), Color(0xFFFEF2F2))
        "Surat Masuk", "Surat Anda" -> Pair(Color(0xFF2563EB), Color(0xFFEFF6FF))
        else -> Pair(EmeraldMedium, Color(0xFFECFDF5))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
                    .height(125.dp)
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
                    
                    if (notif.type == "Laporan" && notif.targetId > 0) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isAdmin) "👉 Ketuk untuk Buka & Tanggapi Laporan Ini" else "👉 Ketuk untuk Buka Riwayat Laporan",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    }
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
