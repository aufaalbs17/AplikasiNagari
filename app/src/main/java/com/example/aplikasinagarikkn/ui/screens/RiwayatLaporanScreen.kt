package com.example.aplikasinagarikkn.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aplikasinagarikkn.data.FirebaseRepository
import com.example.aplikasinagarikkn.model.LaporanModel

// Backward compatibility alias & fallback mock
typealias Laporan = LaporanModel

val mockRiwayatLaporan = listOf(
    LaporanModel(1, "warga_101", "Budi Santoso", "130301...", "Jalan Berlubang di RT 02", "Fasilitas Umum", "Jalan berlubang", "12 Ags 2026", "Selesai", "Sudah ditambal."),
    LaporanModel(2, "warga_101", "Budi Santoso", "130301...", "Lampu Jalan Mati di Simpang Tiga", "Fasilitas Umum", "Lampu jalan mati", "15 Ags 2026", "Diproses", "Petugas sedang meluncur."),
    LaporanModel(3, "warga_101", "Budi Santoso", "130301...", "Tumpukan Sampah Dekat Balai", "Kebersihan", "Sampah menumpuk", "18 Ags 2026", "Menunggu", "")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiwayatLaporanScreen(
    onNavigateBack: () -> Unit
) {
    val laporanList by FirebaseRepository.laporanListState.collectAsState()
    val currentUser by FirebaseRepository.currentUserState.collectAsState()

    // Filter user's own reports (or show all if admin)
    val userLaporanList = laporanList.filter {
        currentUser.role == "admin" || it.userId == currentUser.id || it.pelaporNama == currentUser.nama
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Riwayat Laporan Saya", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text("Daftar Pengaduan Warga & Status Terkini", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        if (userLaporanList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Belum ada riwayat pengaduan warga.", color = Color.Gray, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(userLaporanList) { laporan ->
                    LaporanCardItem(laporan)
                }
            }
        }
    }
}

@Composable
fun LaporanCardItem(laporan: LaporanModel) {
    val (statusBgColor, statusTextColor) = when (laporan.status) {
        "Menunggu" -> Pair(Color(0xFFFFFBEB), Color(0xFFD97706)) // Amber
        "Diproses" -> Pair(Color(0xFFEFF6FF), Color(0xFF2563EB)) // Blue
        "Selesai" -> Pair(Color(0xFFECFDF5), Color(0xFF047857))  // Emerald
        else -> Pair(Color.LightGray, Color.Black)
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = laporan.judul,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Kategori: ${laporan.kategori}",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                // Status Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusBgColor)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = laporan.status,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = statusTextColor
                    )
                }
            }

            if (laporan.deskripsi.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = laporan.deskripsi,
                    fontSize = 12.sp,
                    color = Color(0xFF334155),
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Diajukan: ${laporan.tanggal}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (laporan.tanggapanAdmin.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = Color(0xFFECFDF5),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Filled.QuestionAnswer,
                            contentDescription = null,
                            tint = Color(0xFF047857),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Tanggapan Ibu Wali Nagari:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF064E3B)
                            )
                            Text(
                                text = laporan.tanggapanAdmin,
                                fontSize = 11.sp,
                                color = Color(0xFF047857)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RiwayatLaporanPreview() {
    MaterialTheme {
        RiwayatLaporanScreen({})
    }
}
