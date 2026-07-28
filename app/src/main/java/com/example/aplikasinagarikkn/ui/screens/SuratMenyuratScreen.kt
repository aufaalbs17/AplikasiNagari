package com.example.aplikasinagarikkn.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

val mockSuratAktifList = listOf(
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
    var keperluan by remember { mutableStateOf("") }
    var tipeSuratTerpilih by remember { mutableStateOf("") }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var dokumenTerpilih by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

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

                mockSuratAktifList.forEach { surat ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
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
                                Surface(
                                    color = Color(0xFFFFFBEB),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.HourglassTop,
                                            contentDescription = null,
                                            tint = Color(0xFFD97706),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = surat.status,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFD97706)
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
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = surat.keterangan,
                                fontSize = 12.sp,
                                color = Color(0xFF334155)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Step Timeline Indicator
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StepPill(step = "1. Diajukan", active = true)
                                Text("──", fontSize = 10.sp, color = Color.Gray)
                                StepPill(step = "2. Ditinjau Wali", active = true)
                                Text("──", fontSize = 10.sp, color = Color.Gray)
                                StepPill(step = "3. Selesai", active = false)
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

                        // Upload Dokumen Pendukung
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
                                .clickable { dokumenTerpilih = !dokumenTerpilih },
                            color = if (dokumenTerpilih) Color(0xFFECFDF5) else Color(0xFFF8FAFC),
                            border = BorderStroke(1.5.dp, if (dokumenTerpilih) EmeraldMedium else BorderSubtle),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = if (dokumenTerpilih) Icons.Filled.CheckCircle else Icons.Outlined.UploadFile,
                                    contentDescription = null,
                                    tint = if (dokumenTerpilih) EmeraldMedium else Color.Gray,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = if (dokumenTerpilih) "KTP_KK_BudiSantoso.jpg" else "Unggah Foto KTP / Kartu Keluarga",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (dokumenTerpilih) EmeraldDark else Color(0xFF334155)
                                    )
                                    Text(
                                        text = if (dokumenTerpilih) "Dokumen terlampir • Ketuk untuk ubah" else "Format: JPG, PNG, PDF (Max 5MB)",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = { showSuccessDialog = true },
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
