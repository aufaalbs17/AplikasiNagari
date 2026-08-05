package com.example.aplikasinagarikkn.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.UploadFile
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

private val EmeraldDark = Color(0xFF064E3B)
private val EmeraldMedium = Color(0xFF047857)
private val BorderSubtle = Color(0xFFE2E8F0)

data class SuratAktif(
    val id: Int,
    val jenisSurat: String,
    val tanggal: String,
    val status: String,
    val keterangan: String
)

val initialSuratAktifList = mutableStateListOf(
    SuratAktif(
        id = 101,
        jenisSurat = "Surat Keterangan Usaha (SKU)",
        tanggal = "27 Juli 2026",
        status = "Ditinjau",
        keterangan = "Sedang verifikasi berkas oleh Ibu Wali Nagari"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuratMenyuratScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val suratList by FirebaseRepository.suratListState.collectAsState()
    val currentUser by FirebaseRepository.currentUserState.collectAsState()

    // Filter user's active letters
    val userSuratList = suratList.filter {
        currentUser.role == "admin" || it.userId == currentUser.id || it.pemohonNama == currentUser.nama
    }

    var keperluan by remember { mutableStateOf("") }
    var tipeSuratTerpilih by remember { mutableStateOf("") }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var selectedDocumentUri by remember { mutableStateOf<Uri?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // System File Picker Launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedDocumentUri = uri
            Toast.makeText(context, "Dokumen KTP/KK Terlampir!", Toast.LENGTH_SHORT).show()
        }
    }

    val tipeSuratList = listOf(
        "Surat Keterangan Domisili",
        "Surat Keterangan Usaha (SKU)",
        "Surat Keterangan Tidak Mampu (SKTM)",
        "Surat Pengantar Nikah",
        "Surat Keterangan Kematian",
        "Surat Pengantar KTP / KK",
        "Lainnya"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Layanan Surat Digital",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Pengajuan Administrasi Nagari Sako Selatan",
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
                .background(Color(0xFFF8FAFC))
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // --- 1. Info Banner ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
                    border = BorderStroke(1.dp, EmeraldMedium.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            tint = EmeraldMedium,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Permohonan surat diproses resmi oleh Ibu Wali Nagari Sako Selatan Pasia Talang. Hasil surat dapat diunduh/diambil di Kantor Nagari.",
                            fontSize = 12.sp,
                            color = EmeraldDark,
                            lineHeight = 17.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // --- 2. Live Active Letter Tracking Card ---
                Text(
                    text = "Status Pengajuan Surat Saya",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (userSuratList.isEmpty()) {
                    Text(
                        text = "Belum ada pengajuan surat aktif.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    userSuratList.forEach { surat ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, BorderSubtle),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = surat.jenisSurat,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color(0xFF0F172A)
                                    )
                                    val (badgeBg, badgeText) = when (surat.status) {
                                        "Selesai", "Disetujui" -> Pair(Color(0xFFECFDF5), Color(0xFF047857))
                                        "Ditinjau", "Ditinjau Wali" -> Pair(Color(0xFFFFFBEB), Color(0xFFD97706))
                                        "Ditolak" -> Pair(Color(0xFFFEF2F2), Color(0xFFDC2626))
                                        else -> Pair(Color(0xFFEFF6FF), Color(0xFF2563EB))
                                    }

                                    Surface(
                                        color = badgeBg,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.HourglassTop,
                                                contentDescription = null,
                                                tint = badgeText,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = surat.status,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = badgeText
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Diajukan: ${surat.tanggal}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                                if (surat.keterangan.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = surat.keterangan,
                                        fontSize = 12.sp,
                                        color = Color(0xFF334155)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Step Timeline Indicator
                                val step1Active = true
                                val step2Active = surat.status == "Ditinjau" || surat.status == "Ditinjau Wali" || surat.status == "Selesai" || surat.status == "Disetujui"
                                val step3Active = surat.status == "Selesai" || surat.status == "Disetujui"

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    StepPill(step = "1. Diajukan", active = step1Active)
                                    Text("──", fontSize = 10.sp, color = Color.Gray)
                                    StepPill(step = "2. Ditinjau Wali", active = step2Active)
                                    Text("──", fontSize = 10.sp, color = Color.Gray)
                                    StepPill(step = "3. Selesai", active = step3Active)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- 3. Form Pengajuan Surat Baru ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BorderSubtle),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Form Pengajuan Surat Digital Baru",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Dropdown Tipe Surat
                        Text(
                            text = "Pilih Jenis Surat:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        ExposedDropdownMenuBox(
                            expanded = isDropdownExpanded,
                            onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = if (tipeSuratTerpilih.isEmpty()) "Pilih jenis surat yang dibutuhkan" else tipeSuratTerpilih,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EmeraldMedium,
                                    unfocusedBorderColor = BorderSubtle
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = isDropdownExpanded,
                                onDismissRequest = { isDropdownExpanded = false }
                            ) {
                                tipeSuratList.forEach { selectionOption ->
                                    DropdownMenuItem(
                                        text = { Text(selectionOption, fontWeight = FontWeight.SemiBold) },
                                        onClick = {
                                            tipeSuratTerpilih = selectionOption
                                            isDropdownExpanded = false
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Filled.Description,
                                                contentDescription = null,
                                                tint = EmeraldMedium
                                            )
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Keperluan Input
                        Text(
                            text = "Keperluan Pengajuan Surat:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = keperluan,
                            onValueChange = { keperluan = it },
                            placeholder = { Text("misal: Untuk kelengkapan administrasi pinjaman KUR / Beasiswa", fontSize = 13.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EmeraldMedium,
                                unfocusedBorderColor = BorderSubtle
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Upload Dokumen Pendukung (Real Launcher System Picker)
                        Text(
                            text = "Lampiran Dokumen Pendukung (KTP/KK):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    filePickerLauncher.launch("image/*")
                                },
                            color = if (selectedDocumentUri != null) Color(0xFFECFDF5) else Color(0xFFF8FAFC),
                            border = BorderStroke(1.5.dp, if (selectedDocumentUri != null) EmeraldMedium else BorderSubtle),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                if (selectedDocumentUri != null) {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.LightGray)
                                    ) {
                                        AsyncImage(
                                            model = selectedDocumentUri,
                                            contentDescription = "Preview Lampiran",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                } else {
                                    Icon(
                                        imageVector = Icons.Outlined.UploadFile,
                                        contentDescription = null,
                                        tint = Color.Gray,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = if (selectedDocumentUri != null) "Dokumen KTP/KK Terlampir ✓" else "Unggah Foto KTP / Kartu Keluarga",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (selectedDocumentUri != null) EmeraldDark else Color(0xFF334155)
                                    )
                                    Text(
                                        text = if (selectedDocumentUri != null) "Buka Galeri HP • Ketuk untuk ubah" else "Format: JPG, PNG, PDF (Max 5MB)",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                if (tipeSuratTerpilih.isNotBlank() && keperluan.isNotBlank()) {
                                    // Save to Firebase Firestore / Database Repository
                                    FirebaseRepository.tambahSurat(
                                        jenisSurat = tipeSuratTerpilih,
                                        keperluan = keperluan,
                                        lampiranUri = selectedDocumentUri?.toString()
                                    ) { success ->
                                        if (success) {
                                            Toast.makeText(context, "Surat Digital tersimpan di Database!", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    showSuccessDialog = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            enabled = tipeSuratTerpilih.isNotBlank() && keperluan.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark)
                        ) {
                            Text(
                                text = "Kirim Pengajuan Surat Digital",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = EmeraldMedium,
                        modifier = Modifier.size(48.dp)
                    )
                },
                title = {
                    Text(
                        text = "Pengajuan Surat Berhasil Dikirim!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Text(
                        text = "Permohonan $tipeSuratTerpilih Anda telah masuk ke Panel Ibu Wali Nagari Sako Selatan Pasia Talang untuk ditinjau.",
                        fontSize = 13.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showSuccessDialog = false
                            onNavigateBack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark)
                    ) {
                        Text("Kembali ke Beranda", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

@Composable
fun StepPill(step: String, active: Boolean) {
    Surface(
        color = if (active) EmeraldMedium else Color(0xFFE2E8F0),
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = step,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (active) Color.White else Color(0xFF64748B),
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SuratMenyuratPreview() {
    MaterialTheme {
        SuratMenyuratScreen({})
    }
}
