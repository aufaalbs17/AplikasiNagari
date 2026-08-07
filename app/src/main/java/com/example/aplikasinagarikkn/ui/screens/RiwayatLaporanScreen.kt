package com.example.aplikasinagarikkn.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.aplikasinagarikkn.data.FirebaseRepository
import com.example.aplikasinagarikkn.model.LaporanModel
import com.example.aplikasinagarikkn.model.SuratModel

// Backward compatibility alias & fallback mock
typealias Laporan = LaporanModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiwayatLaporanScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val laporanList by FirebaseRepository.laporanListState.collectAsState()
    val suratList by FirebaseRepository.suratListState.collectAsState()
    val currentUser by FirebaseRepository.currentUserState.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var selectedLaporan by remember { mutableStateOf<LaporanModel?>(null) }
    var selectedSurat by remember { mutableStateOf<SuratModel?>(null) }

    // Filter user's own reports & letters (with safe fallback)
    val userLaporanList = laporanList.filter {
        currentUser.role == "admin" || it.userId == currentUser.id || it.pelaporNama == currentUser.nama || (currentUser.id == "warga_101" && (it.userId == "warga_101" || it.pelaporNama == "Budi Santoso"))
    }
    val displayLaporanList = if (userLaporanList.isNotEmpty()) userLaporanList else laporanList

    val userSuratList = suratList.filter {
        currentUser.role == "admin" || it.userId == currentUser.id || it.pemohonNama == currentUser.nama || (currentUser.id == "warga_101" && (it.userId == "warga_101" || it.pemohonNama == "Budi Santoso"))
    }
    val displaySuratList = if (userSuratList.isNotEmpty()) userSuratList else suratList

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Riwayat Aktivitas Saya", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text("Daftar Pengaduan & Permohonan Surat Warga", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Tab Switcher between Laporan and Surat
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = Color(0xFF047857)
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = {
                        Text(
                            text = "📢 Laporan (${displayLaporanList.size})",
                            fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = {
                        Text(
                            text = "📜 Surat (${displaySuratList.size})",
                            fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
            }

            if (selectedTabIndex == 0) {
                // Tab 0: Laporan Pengaduan
                if (displayLaporanList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Belum ada riwayat pengaduan warga.", color = Color.Gray, fontSize = 14.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(displayLaporanList) { laporan ->
                            LaporanCardItem(
                                laporan = laporan,
                                onClick = { selectedLaporan = laporan }
                            )
                        }
                    }
                }
            } else {
                // Tab 1: Permohonan Surat
                if (displaySuratList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Belum ada riwayat permohonan surat.", color = Color.Gray, fontSize = 14.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(displaySuratList) { surat ->
                            SuratCardItem(
                                surat = surat,
                                onClick = { selectedSurat = surat }
                            )
                        }
                    }
                }
            }
        }

        // Full Detail Dialog for Laporan Warga
        val itemLaporan = selectedLaporan
        if (itemLaporan != null) {
            val (statusBgColor, statusTextColor) = when (itemLaporan.status) {
                "Menunggu" -> Pair(Color(0xFFFFFBEB), Color(0xFFD97706))
                "Diproses" -> Pair(Color(0xFFEFF6FF), Color(0xFF2563EB))
                "Selesai" -> Pair(Color(0xFFECFDF5), Color(0xFF047857))
                else -> Pair(Color.LightGray, Color.Black)
            }

            AlertDialog(
                onDismissRequest = { selectedLaporan = null },
                confirmButton = {
                    Button(
                        onClick = { selectedLaporan = null },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857))
                    ) {
                        Text("Tutup", fontWeight = FontWeight.Bold)
                    }
                },
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = itemLaporan.judul,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = statusBgColor,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = itemLaporan.status,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusTextColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(text = "Kategori: ${itemLaporan.kategori}", fontSize = 12.sp, color = Color(0xFF64748B))
                        Text(text = "Diajukan: ${itemLaporan.tanggal}", fontSize = 12.sp, color = Color(0xFF64748B))
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF047857), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = itemLaporan.lokasiAlamat.ifBlank { "Jorong Pasia, Nagari Sako Selatan" },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF0F172A)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Rincian Keluhan:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = itemLaporan.deskripsi.ifBlank { "Tidak ada deskripsi tambahan." }, fontSize = 12.sp, color = Color(0xFF475569))

                        if (!itemLaporan.fotoUri.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = "Foto Bukti Pelapor:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                            Spacer(modifier = Modifier.height(4.dp))
                            AsyncImage(
                                model = itemLaporan.fotoUri,
                                contentDescription = "Foto Bukti Warga",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .clip(RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }

                        if (itemLaporan.tanggapanAdmin.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                color = Color(0xFFECFDF5),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.QuestionAnswer, contentDescription = null, tint = Color(0xFF047857), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "Tanggapan Ibu Wali Nagari:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF064E3B))
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = itemLaporan.tanggapanAdmin, fontSize = 11.sp, color = Color(0xFF047857))
                                }
                            }
                        }

                        if (!itemLaporan.fotoBuktiPenangananUri.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                color = Color(0xFFEFF6FF),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = itemLaporan.fotoBuktiPenangananUri,
                                        contentDescription = "Foto Hasil Lapangan",
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(text = "📸 Bukti Penanganan Lapangan Nagari", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E40AF))
                                        Text(text = "Perangkat Nagari telah menuntaskan pekerjaan fisik di lokasi.", fontSize = 10.sp, color = Color(0xFF1E3A8A))
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }

        // Full Detail Dialog for Permohonan Surat Warga
        val itemSurat = selectedSurat
        if (itemSurat != null) {
            val (statusBgColor, statusTextColor) = when (itemSurat.status) {
                "Diajukan" -> Pair(Color(0xFFFFFBEB), Color(0xFFD97706))
                "Ditinjau Wali" -> Pair(Color(0xFFFEF3C7), Color(0xFFB45309))
                "Disetujui", "Selesai" -> Pair(Color(0xFFECFDF5), Color(0xFF047857))
                "Ditolak" -> Pair(Color(0xFFFEF2F2), Color(0xFFDC2626))
                else -> Pair(Color.LightGray, Color.Black)
            }

            AlertDialog(
                onDismissRequest = { selectedSurat = null },
                confirmButton = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        if (itemSurat.status == "Disetujui" || itemSurat.status == "Selesai") {
                            if (itemSurat.metodePengambilan == "Digital") {
                                Button(
                                    onClick = {
                                        Toast.makeText(context, "📥 Mengunduh berkas ${itemSurat.jenisSurat} (.pdf)...", Toast.LENGTH_LONG).show()
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857))
                                ) {
                                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Unduh Berkas Surat (.pdf)", fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                        }
                        OutlinedButton(
                            onClick = { selectedSurat = null },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Tutup", fontWeight = FontWeight.Bold)
                        }
                    }
                },
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = itemSurat.jenisSurat,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = statusBgColor,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = itemSurat.status,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusTextColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(text = "Pemohon: ${itemSurat.pemohonNama} (NIK: ${itemSurat.pemohonNik})", fontSize = 12.sp, color = Color(0xFF64748B))
                        Text(text = "Tanggal Pengajuan: ${itemSurat.tanggal}", fontSize = 12.sp, color = Color(0xFF64748B))

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "Keperluan / Catatan:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = itemSurat.keperluan.ifBlank { "Tidak ada catatan keperluan." }, fontSize = 12.sp, color = Color(0xFF475569))

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "Metode Pengambilan:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (itemSurat.metodePengambilan == "Digital") "📥 File Digital (.pdf) - Langsung Unduh di Aplikasi" else "🏢 Dokumen Fisik - Ambil Langsung di Kantor Wali Nagari",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF047857)
                        )

                        if (itemSurat.keterangan.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Surface(
                                color = Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(text = "Catatan Ibu Wali Nagari / Perangkat:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = itemSurat.keterangan, fontSize = 11.sp, color = Color(0xFF334155))
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun LaporanCardItem(
    laporan: LaporanModel,
    onClick: () -> Unit = {}
) {
    val (statusBgColor, statusTextColor) = when (laporan.status) {
        "Menunggu" -> Pair(Color(0xFFFFFBEB), Color(0xFFD97706)) // Amber
        "Diproses" -> Pair(Color(0xFFEFF6FF), Color(0xFF2563EB)) // Blue
        "Selesai" -> Pair(Color(0xFFECFDF5), Color(0xFF047857))  // Emerald
        else -> Pair(Color.LightGray, Color.Black)
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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

            if (!laporan.fotoBuktiPenangananUri.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color(0xFFEFF6FF),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = laporan.fotoBuktiPenangananUri,
                            contentDescription = "Foto Hasil Lapangan",
                            modifier = Modifier
                                .size(55.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "📸 Foto Bukti Penanganan Lapangan Nagari",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E40AF)
                            )
                            Text(
                                text = "Perangkat Nagari telah menyelesaikan penanganan di lokasi kejadian.",
                                fontSize = 10.sp,
                                color = Color(0xFF1E3A8A)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "👉 Ketuk untuk rincian detail laporan",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF047857)
            )
        }
    }
}

@Composable
fun SuratCardItem(
    surat: SuratModel,
    onClick: () -> Unit = {}
) {
    val (statusBgColor, statusTextColor) = when (surat.status) {
        "Diajukan" -> Pair(Color(0xFFFFFBEB), Color(0xFFD97706))
        "Ditinjau Wali" -> Pair(Color(0xFFFEF3C7), Color(0xFFB45309))
        "Disetujui", "Selesai" -> Pair(Color(0xFFECFDF5), Color(0xFF047857))
        "Ditolak" -> Pair(Color(0xFFFEF2F2), Color(0xFFDC2626))
        else -> Pair(Color.LightGray, Color.Black)
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
                        text = surat.jenisSurat,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Pemohon: ${surat.pemohonNama}",
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
                        text = surat.status,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = statusTextColor
                    )
                }
            }

            if (surat.keperluan.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Keperluan: ${surat.keperluan}",
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
                    text = "Diajukan: ${surat.tanggal}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = if (surat.metodePengambilan == "Digital") "📥 Unduh Digital" else "🏢 Ambil di Kantor",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF047857)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "👉 Ketuk untuk rincian detail permohonan surat",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF047857)
            )
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
