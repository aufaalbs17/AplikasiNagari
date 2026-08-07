package com.example.aplikasinagarikkn.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- Color Tokens for Executive Theme ---
private val EmeraldDark = Color(0xFF064E3B)
private val EmeraldMedium = Color(0xFF047857)
private val EmeraldLight = Color(0xFF10B981)
private val SoftBgColor = Color(0xFFF8FAFC)
private val BorderSubtle = Color(0xFFE2E8F0)
private val UrgentRed = Color(0xFFDC2626)
private val UrgentRedBg = Color(0xFFFEF2F2)
private val ProcessingBlue = Color(0xFF2563EB)
private val ProcessingBlueBg = Color(0xFFEFF6FF)
private val PendingAmber = Color(0xFFD97706)
private val PendingAmberBg = Color(0xFFFFFBEB)
private val CompletedGreen = Color(0xFF059669)
private val CompletedGreenBg = Color(0xFFECFDF5)

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigateToKelolaLaporan: () -> Unit,
    onNavigateToNotifikasi: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val dbLaporanList by com.example.aplikasinagarikkn.data.FirebaseRepository.laporanListState.collectAsState()
    val dbSuratList by com.example.aplikasinagarikkn.data.FirebaseRepository.suratListState.collectAsState()

    // Dialog state for Admin Surat Management
    var showSuratManageDialog by remember { mutableStateOf(false) }
    var selectedSuratId by remember { mutableStateOf<Int?>(null) }
    var suratNotesInput by remember { mutableStateOf("") }
    var suratStatusInput by remember { mutableStateOf("Ditinjau Wali") }
    var suratMetodeInput by remember { mutableStateOf("Digital") }

    // Dynamic Complaint Stats
    val totalLaporan = dbLaporanList.size
    val pendingCount = dbLaporanList.count { it.status == "Menunggu" }
    val processingCount = dbLaporanList.count { it.status == "Diproses" }
    val completedCount = dbLaporanList.count { it.status == "Selesai" }
    val progressRate = if (totalLaporan > 0) (completedCount + processingCount).toFloat() / totalLaporan else 0.75f
    val progressPercentage = (progressRate * 100).toInt()

    // Dynamic Pending Letters
    val pendingSuratCount = dbSuratList.count { it.status == "Diajukan" || it.status == "Ditinjau Wali" || it.status == "Ditinjau" }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Shield,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Panel Ibu Wali Nagari",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Pemerintahan Nagari Sako Selatan",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToNotifikasi) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = UrgentRed,
                                    contentColor = Color.White
                                ) {
                                    Text("3", fontWeight = FontWeight.Bold)
                                }
                            }
                        ) {
                            Icon(
                                Icons.Filled.Notifications,
                                contentDescription = "Pusat Notifikasi",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = EmeraldDark,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SoftBgColor)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // --- 1. Header Banner Greeting (Ibu Wali Nagari Executive Card) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                EmeraldDark,
                                EmeraldMedium
                            )
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 22.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .border(2.dp, Color.White.copy(alpha = 0.6f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Face,
                                    contentDescription = null,
                                    tint = EmeraldDark,
                                    modifier = Modifier.size(38.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Selamat Datang,",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Ibu Wali Nagari",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    fontSize = 20.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Selasa, 28 Juli 2026 • Monitoring Layanan Nagari",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.95f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // =========================================================================
            // 🌟 MAIN HIGHLIGHT 1: PUSAT MONITORING PROGRESS PENGADUAN WARGA
            // =========================================================================
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(EmeraldMedium.copy(alpha = 0.12f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PendingActions,
                                contentDescription = null,
                                tint = EmeraldMedium,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Progress Pengaduan Warga",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }

                    Surface(
                        color = EmeraldMedium.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Highlight Utama",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldMedium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Progress Resolution Rate Meter Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BorderSubtle),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Tingkat Penyelesaian Pengaduan",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF64748B)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "$progressPercentage% Tuntas Ditangani",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = EmeraldDark
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(CompletedGreenBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = CompletedGreen,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Visual Linear Progress Bar
                        LinearProgressIndicator(
                            progress = { progressRate.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = EmeraldMedium,
                            trackColor = Color(0xFFE2E8F0)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${completedCount + processingCount} dari $totalLaporan Laporan Selesai & Diproses",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF475569)
                            )
                            Text(
                                text = "Target: 100%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Status Breakdown Grid (4 Clean Interactive Cards)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProfessionalStatCard(
                        title = "Total Masuk",
                        count = "$totalLaporan",
                        sub = "Semua Laporan",
                        icon = Icons.Filled.Description,
                        colorBg = Color.White,
                        colorAccent = EmeraldMedium,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToKelolaLaporan
                    )
                    ProfessionalStatCard(
                        title = "Menunggu",
                        count = "$pendingCount",
                        sub = "Perlu Respon Ibu",
                        icon = Icons.Filled.HourglassTop,
                        colorBg = PendingAmberBg,
                        colorAccent = PendingAmber,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToKelolaLaporan
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProfessionalStatCard(
                        title = "Diproses",
                        count = "$processingCount",
                        sub = "Sedang Dikerjakan",
                        icon = Icons.Filled.Sync,
                        colorBg = ProcessingBlueBg,
                        colorAccent = ProcessingBlue,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToKelolaLaporan
                    )
                    ProfessionalStatCard(
                        title = "Selesai",
                        count = "$completedCount",
                        sub = "Tuntas Teratasi",
                        icon = Icons.Filled.CheckCircle,
                        colorBg = CompletedGreenBg,
                        colorAccent = CompletedGreen,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToKelolaLaporan
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // =========================================================================
            // 🌟 MAIN HIGHLIGHT 1.5: PUSAT MONITORING PERMOHONAN SURAT DIGITAL WARGA
            // =========================================================================
            val totalSurat = dbSuratList.size
            val suratDitinjauCount = dbSuratList.count { it.status == "Diajukan" || it.status == "Ditinjau Wali" || it.status == "Ditinjau" }
            val suratDisetujuiCount = dbSuratList.count { it.status == "Disetujui" || it.status == "Selesai" }
            val suratDitolakCount = dbSuratList.count { it.status == "Ditolak" }

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(ProcessingBlueBg, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Description,
                                contentDescription = null,
                                tint = ProcessingBlue,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Layanan Surat Digital Warga",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }

                    TextButton(
                        onClick = {
                            val target = dbSuratList.firstOrNull { it.status != "Selesai" && it.status != "Disetujui" } ?: dbSuratList.firstOrNull()
                            if (target != null) {
                                selectedSuratId = target.id
                                suratStatusInput = target.status
                                suratNotesInput = target.keterangan
                                showSuratManageDialog = true
                            } else {
                                Toast.makeText(context, "Belum ada permohonan surat warga.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Kelola Surat Warga", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ProcessingBlue)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Surat Status Breakdown Grid (4 Clean Interactive Cards)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProfessionalStatCard(
                        title = "Total Surat",
                        count = "$totalSurat",
                        sub = "Semua Permohonan",
                        icon = Icons.Filled.Description,
                        colorBg = Color.White,
                        colorAccent = ProcessingBlue,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val target = dbSuratList.firstOrNull()
                            if (target != null) {
                                selectedSuratId = target.id
                                suratStatusInput = target.status
                                suratNotesInput = target.keterangan
                                showSuratManageDialog = true
                            }
                        }
                    )
                    ProfessionalStatCard(
                        title = "Perlu Tinjauan",
                        count = "$suratDitinjauCount",
                        sub = "Verifikasi Ibu Wali",
                        icon = Icons.Filled.HourglassTop,
                        colorBg = PendingAmberBg,
                        colorAccent = PendingAmber,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val target = dbSuratList.firstOrNull { it.status == "Diajukan" || it.status == "Ditinjau Wali" || it.status == "Ditinjau" } ?: dbSuratList.firstOrNull()
                            if (target != null) {
                                selectedSuratId = target.id
                                suratStatusInput = target.status
                                suratNotesInput = target.keterangan
                                showSuratManageDialog = true
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProfessionalStatCard(
                        title = "Surat Disetujui",
                        count = "$suratDisetujuiCount",
                        sub = "Digital & Fisik Siap",
                        icon = Icons.Filled.CheckCircle,
                        colorBg = CompletedGreenBg,
                        colorAccent = CompletedGreen,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val target = dbSuratList.firstOrNull { it.status == "Disetujui" || it.status == "Selesai" } ?: dbSuratList.firstOrNull()
                            if (target != null) {
                                selectedSuratId = target.id
                                suratStatusInput = target.status
                                suratNotesInput = target.keterangan
                                showSuratManageDialog = true
                            }
                        }
                    )
                    ProfessionalStatCard(
                        title = "Surat Ditolak",
                        count = "$suratDitolakCount",
                        sub = "Berkas Belum Lengkap",
                        icon = Icons.Filled.AddAlert,
                        colorBg = UrgentRedBg,
                        colorAccent = UrgentRed,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val target = dbSuratList.firstOrNull { it.status == "Ditolak" } ?: dbSuratList.firstOrNull()
                            if (target != null) {
                                selectedSuratId = target.id
                                suratStatusInput = target.status
                                suratNotesInput = target.keterangan
                                showSuratManageDialog = true
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // =========================================================================
            // 🌟 MAIN HIGHLIGHT 2: PUSAT NOTIFIKASI & PERLU TINDAKAN REAL-TIME
            // =========================================================================
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(UrgentRedBg, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AddAlert,
                                contentDescription = null,
                                tint = UrgentRed,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Notifikasi & Perlu Tindakan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                    }

                    TextButton(
                        onClick = onNavigateToNotifikasi,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Buka Semua Notifikasi", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = EmeraldMedium)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Alert Notification Feed Card 1 (Urgent Complaint Report)
                ProfessionalNotificationCard(
                    title = "🚨 Laporan Menunggu Tanggapan Ibu ($pendingCount Baru)",
                    desc = if (pendingCount > 0) "Terdapat $pendingCount pengaduan warga yang belum ditanggapi." else "Semua laporan warga telah ditanggapi.",
                    timeAgo = "Terbaru",
                    category = "Pengaduan Warga",
                    colorAccent = UrgentRed,
                    bgAccent = UrgentRedBg,
                    onActionClick = onNavigateToKelolaLaporan
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Alert Notification Feed Card 2 (Surat Request)
                ProfessionalNotificationCard(
                    title = "📜 Permohonan Surat Digital ($pendingSuratCount Masuk)",
                    desc = if (pendingSuratCount > 0) "Terdapat $pendingSuratCount permohonan surat warga yang perlu ditinjau & diverifikasi." else "Tidak ada permohonan surat tertunda.",
                    timeAgo = "Terbaru",
                    category = "Layanan Surat",
                    colorAccent = ProcessingBlue,
                    bgAccent = ProcessingBlueBg,
                    onActionClick = {
                        val firstPending = dbSuratList.firstOrNull { it.status != "Selesai" && it.status != "Disetujui" } ?: dbSuratList.firstOrNull()
                        if (firstPending != null) {
                            selectedSuratId = firstPending.id
                            suratStatusInput = "Ditinjau Wali"
                            suratNotesInput = firstPending.keterangan
                            showSuratManageDialog = true
                        } else {
                            Toast.makeText(context, "Belum ada pengajuan surat warga.", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- 5. Timeline Tracker Laporan Terbaru ---
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Progress Laporan Terbaru",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    TextButton(
                        onClick = onNavigateToKelolaLaporan,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Kelola Semua", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = EmeraldMedium)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                dbLaporanList.take(2).forEach { laporan ->
                    val (step, statusText, statusBg, statusColor) = when (laporan.status) {
                        "Selesai" -> Quadruple(3, "Tuntas Ditangani", CompletedGreenBg, CompletedGreen)
                        "Diproses" -> Quadruple(2, "Sedang Dikerjakan Petugas", ProcessingBlueBg, ProcessingBlue)
                        else -> Quadruple(1, "Menunggu Tanggapan Ibu Wali", PendingAmberBg, PendingAmber)
                    }

                    ProfessionalReportProgressCard(
                        judul = laporan.judul,
                        pelapor = "${laporan.pelaporNama} (Warga)",
                        tanggal = laporan.tanggal,
                        currentStep = step,
                        statusText = statusText,
                        statusBg = statusBg,
                        statusColor = statusColor,
                        onClick = onNavigateToKelolaLaporan
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // --- 6. Logout Button ---
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.5.dp, UrgentRed.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = UrgentRed
                    )
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Keluar dari Panel Ibu Wali Nagari", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }

        // --- Admin Surat Management Dialog ---
        if (showSuratManageDialog && selectedSuratId != null) {
            val suratItem = dbSuratList.find { it.id == selectedSuratId }
            if (suratItem != null) {
                AlertDialog(
                    onDismissRequest = { showSuratManageDialog = false },
                    title = {
                        Text(
                            text = "Kelola Pengajuan Surat Warga",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    },
                    text = {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Pemohon: ${suratItem.pemohonNama} (NIK: ${suratItem.pemohonNik})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Jenis Surat: ${suratItem.jenisSurat}",
                                fontSize = 12.sp,
                                color = EmeraldDark,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Keperluan: ${suratItem.keperluan}",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Status Verifikasi Ibu Wali:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF334155)
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                FilterChip(
                                    selected = suratStatusInput == "Ditinjau Wali",
                                    onClick = { suratStatusInput = "Ditinjau Wali" },
                                    label = { Text("Ditinjau", fontSize = 11.sp) }
                                )
                                FilterChip(
                                    selected = suratStatusInput == "Selesai",
                                    onClick = { suratStatusInput = "Selesai" },
                                    label = { Text("Disetujui / Selesai", fontSize = 11.sp) }
                                )
                                FilterChip(
                                    selected = suratStatusInput == "Ditolak",
                                    onClick = { suratStatusInput = "Ditolak" },
                                    label = { Text("Ditolak", fontSize = 11.sp) }
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            if (suratStatusInput == "Selesai") {
                                Text(
                                    text = "Opsi Penyerahan / Pengambilan Surat:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF334155)
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    FilterChip(
                                        selected = suratMetodeInput == "Digital",
                                        onClick = { suratMetodeInput = "Digital" },
                                        label = { Text("📥 File Digital (Warga Unduh Surat di Aplikasi)", fontSize = 11.sp) }
                                    )
                                    FilterChip(
                                        selected = suratMetodeInput == "Fisik",
                                        onClick = { suratMetodeInput = "Fisik" },
                                        label = { Text("🏢 Ambil Fisik di Kantor Nagari Sako Selatan", fontSize = 11.sp) }
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            Text(
                                text = "Catatan / Keterangan untuk Warga:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF334155)
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = suratNotesInput,
                                onValueChange = { suratNotesInput = it },
                                placeholder = { Text("misal: Berkas disetujui, silakan unduh file atau ambil fisik di kantor Nagari", fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                val defaultKeterangan = if (suratStatusInput == "Selesai") {
                                    if (suratMetodeInput == "Digital") "Surat telah disetujui & disahkan. File surat digital dapat Anda unduh langsung di aplikasi."
                                    else "Surat telah disetujui & dicetak. Silakan ambil dokumen fisik di Kantor Nagari Sako Selatan (Jam Layanan 08.00 - 15.00 WIB)."
                                } else "Status pengajuan surat diperbarui oleh Ibu Wali Nagari."

                                com.example.aplikasinagarikkn.data.FirebaseRepository.updateStatusSurat(
                                    id = suratItem.id,
                                    statusBaru = suratStatusInput,
                                    keterangan = suratNotesInput.ifBlank { defaultKeterangan },
                                    metodePengambilan = suratMetodeInput,
                                    context = context
                                )
                                showSuratManageDialog = false
                                Toast.makeText(context, "Status & Opsi Surat #${suratItem.id} diperbarui!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark)
                        ) {
                            Text("Simpan Status Surat", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showSuratManageDialog = false }) {
                            Text("Batal", color = Color.Gray)
                        }
                    }
                )
            }
        }
    }

// =========================================================================
// EXECUTIVE HELPER COMPOSABLES WITH STRICT NO-CLIPPING & CLEAN SPACING
// =========================================================================

@Composable
fun ProfessionalStatCard(
    title: String,
    count: String,
    sub: String,
    icon: ImageVector,
    colorBg: Color,
    colorAccent: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .heightIn(min = 124.dp)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colorBg),
        border = BorderStroke(1.dp, BorderSubtle),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(colorAccent.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = colorAccent,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = count,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colorAccent
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = sub,
                fontSize = 11.sp,
                color = Color(0xFF64748B),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ProfessionalNotificationCard(
    title: String,
    desc: String,
    timeAgo: String,
    category: String,
    colorAccent: Color,
    bgAccent: Color,
    onActionClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderSubtle),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Left Accent Border Strip
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(colorAccent)
            )

            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = colorAccent.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = category,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorAccent,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Text(text = timeAgo, fontSize = 11.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF0F172A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = desc,
                    fontSize = 11.sp,
                    color = Color(0xFF475569),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onActionClick,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorAccent),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("Tindak Lanjuti Cepat", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfessionalActionCard(
    title: String,
    desc: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .heightIn(min = 126.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderSubtle),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(iconBg, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
                }

                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFF0F172A),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                fontSize = 11.sp,
                color = Color(0xFF64748B),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ProfessionalReportProgressCard(
    judul: String,
    pelapor: String,
    tanggal: String,
    currentStep: Int, // 1: Menunggu, 2: Diproses, 3: Selesai
    statusText: String,
    statusBg: Color,
    statusColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderSubtle),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = judul,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF0F172A),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    color = statusBg,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = statusText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "$pelapor • $tanggal",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Step Progress Timeline
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExecutiveStepIndicator(step = 1, current = currentStep, label = "Diterima", modifier = Modifier.weight(1f))
                ExecutiveStepLine(active = currentStep >= 2)
                ExecutiveStepIndicator(step = 2, current = currentStep, label = "Pengerjaan", modifier = Modifier.weight(1f))
                ExecutiveStepLine(active = currentStep >= 3)
                ExecutiveStepIndicator(step = 3, current = currentStep, label = "Selesai", modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun ExecutiveStepIndicator(step: Int, current: Int, label: String, modifier: Modifier = Modifier) {
    val isActive = current >= step
    val activeColor = EmeraldMedium
    val inactiveColor = Color(0xFFCBD5E1)
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .background(if (isActive) activeColor else inactiveColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (isActive) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(Color.White, CircleShape)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
            color = if (isActive) activeColor else Color(0xFF64748B),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ExecutiveStepLine(active: Boolean) {
    Box(
        modifier = Modifier
            .width(32.dp)
            .height(2.5.dp)
            .background(if (active) EmeraldMedium else Color(0xFFE2E8F0))
    )
}

@Preview(showBackground = true)
@Composable
fun AdminDashboardPreview() {
    MaterialTheme {
        AdminDashboardScreen({}, {}, {})
    }
}



