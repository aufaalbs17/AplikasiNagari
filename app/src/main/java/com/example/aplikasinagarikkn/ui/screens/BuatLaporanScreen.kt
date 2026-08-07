package com.example.aplikasinagarikkn.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuatLaporanScreen(
    onNavigateBack: () -> Unit,
    onSubmit: () -> Unit
) {
    val context = LocalContext.current
    var judul by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var kategoriTerpilih by remember { mutableStateOf("Fasilitas Umum") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // GPS Geotagging & Manual Location state
    var metodeLokasi by remember { mutableStateOf("GPS") } // "GPS" or "Manual"
    var manualLokasiText by remember { mutableStateOf("") }
    var lokasiAlamat by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }
    var isGpsPinned by remember { mutableStateOf(false) }

    // System Image Picker Launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            Toast.makeText(context, "Foto Bukti Terpilih!", Toast.LENGTH_SHORT).show()
        }
    }

    val kategoriList = listOf(
        "Fasilitas Umum",
        "Kebersihan",
        "Keamanan",
        "Kebencanaan",
        "Lainnya"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Buat Laporan Pengaduan",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Layanan Aspirasi & Pengaduan Nagari",
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
                modifier = Modifier.padding(20.dp)
            ) {
                // Form Card Container
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BorderSubtle),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        // Kategori Selector
                        Text(
                            text = "Pilih Kategori Laporan",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(kategoriList) { kategori ->
                                val isSelected = kategori == kategoriTerpilih
                                val bg = if (isSelected) EmeraldDark else Color(0xFFF1F5F9)
                                val textColor = if (isSelected) Color.White else Color(0xFF475569)

                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { kategoriTerpilih = kategori },
                                    color = bg,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = kategori,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = textColor,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Judul Laporan
                        Text(
                            text = "Judul Pengaduan",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = judul,
                            onValueChange = { judul = it },
                            placeholder = { Text("misal: Lampu jalan mati di Jorong Pasia", fontSize = 13.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EmeraldMedium,
                                unfocusedBorderColor = BorderSubtle
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Deskripsi Laporan
                        Text(
                            text = "Detail Laporan & Lokasi Kejadian",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = deskripsi,
                            onValueChange = { deskripsi = it },
                            placeholder = { Text("Jelaskan rincian keluhan & patokan lokasi kejadian secara lengkap...", fontSize = 13.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EmeraldMedium,
                                unfocusedBorderColor = BorderSubtle
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // --- GPS Geotagging & Manual Location Card ---
                        Text(
                            text = "Penentuan Lokasi Kejadian",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Location Method Choice Tabs (GPS vs Manual)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        metodeLokasi = "GPS"
                                        latitude = -1.4522
                                        longitude = 101.3211
                                        isGpsPinned = true
                                        lokasiAlamat = "Jorong Pasia, Nagari Sako Selatan (-1.4522, 101.3211)"
                                    },
                                color = if (metodeLokasi == "GPS") EmeraldDark else Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                 Text(
                                    text = "📍 GPS Presisi",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (metodeLokasi == "GPS") Color.White else Color(0xFF475569),
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        metodeLokasi = "Manual"
                                    },
                                color = if (metodeLokasi == "Manual") EmeraldDark else Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "✏️ Ketik Manual",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (metodeLokasi == "Manual") Color.White else Color(0xFF475569),
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (metodeLokasi == "GPS") {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        latitude = -1.4522
                                        longitude = 101.3211
                                        isGpsPinned = true
                                        lokasiAlamat = "Jorong Pasia, Nagari Sako Selatan (-1.4522, 101.3211)"
                                        Toast.makeText(context, "📍 Lokasi GPS Terdeteksi (-1.4522, 101.3211)", Toast.LENGTH_SHORT).show()
                                    },
                                color = if (isGpsPinned) Color(0xFFECFDF5) else Color(0xFFF8FAFC),
                                border = BorderStroke(1.5.dp, if (isGpsPinned) EmeraldMedium else BorderSubtle),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(if (isGpsPinned) EmeraldDark else Color(0xFFE2E8F0)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = if (isGpsPinned) Color.White else Color(0xFF64748B),
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isGpsPinned) "📍 Lokasi Presisi Disematkan ✓" else "Ketuk untuk Deteksi Koordinat GPS",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (isGpsPinned) EmeraldDark else Color(0xFF334155)
                                        )
                                        Text(
                                            text = if (isGpsPinned) lokasiAlamat else "Jorong Pasia, Nagari Sako Selatan (-1.4522, 101.3211)",
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }
                            }
                        } else {
                            // Manual Address Input Field
                            OutlinedTextField(
                                value = manualLokasiText,
                                onValueChange = { manualLokasiText = it },
                                placeholder = { Text("Ketikkan alamat/patokan lokasi (misal: Pasia Talang RT 02 dekat Surau Pasia)...", fontSize = 12.sp) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = EmeraldMedium
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EmeraldMedium,
                                    unfocusedBorderColor = BorderSubtle
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Photo Upload Container (System Gallery Picker)
                        Text(
                            text = "Bukti Foto (Opsional)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { imagePickerLauncher.launch("image/*") },
                            color = Color(0xFFF8FAFC),
                            border = BorderStroke(1.5.dp, BorderSubtle),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (selectedImageUri != null) {
                                    AsyncImage(
                                        model = selectedImageUri,
                                        contentDescription = "Foto Terpilih",
                                        modifier = Modifier
                                            .size(70.dp)
                                            .clip(RoundedCornerShape(10.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(CircleShape)
                                            .background(EmeraldMedium.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AddAPhoto,
                                            contentDescription = "Pilih Foto",
                                            tint = EmeraldMedium,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                }

                                Column {
                                    Text(
                                        text = if (selectedImageUri != null) "Foto Bukti Terpilih ✓" else "Ambil / Unggah Foto Bukti",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (selectedImageUri != null) EmeraldDark else Color(0xFF334155)
                                    )
                                    Text(
                                        text = if (selectedImageUri != null) "Buka Galeri HP • Ketuk untuk ubah foto" else "Format: JPG, PNG (Max 5MB)",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Submit Button
                        Button(
                            onClick = {
                                if (judul.isNotBlank()) {
                                    val finalLokasiAlamat = if (metodeLokasi == "GPS") {
                                        lokasiAlamat.ifBlank { "Jorong Pasia, Nagari Sako Selatan (-1.4522, 101.3211)" }
                                    } else {
                                        manualLokasiText.ifBlank { "Jorong Pasia, Nagari Sako Selatan" }
                                    }

                                    // Save to Firebase Firestore / Repository
                                    FirebaseRepository.tambahLaporan(
                                        judul = judul,
                                        kategori = kategoriTerpilih,
                                        deskripsi = deskripsi,
                                        fotoUri = selectedImageUri?.toString(),
                                        latitude = latitude ?: -1.4522,
                                        longitude = longitude ?: 101.3211,
                                        lokasiAlamat = finalLokasiAlamat,
                                        context = context
                                    ) { success ->
                                        if (success) {
                                            Toast.makeText(context, "Laporan & Lokasi tersimpan!", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    showSuccessDialog = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            enabled = judul.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark)
                        ) {
                            Text(
                                text = "Kirim Pengaduan Sekarang",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }
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
                    text = "Laporan Berhasil Dikirim!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "Laporan Anda telah diteruskan ke Panel Ibu Wali Nagari Sako Selatan Pasia Talang untuk ditindaklanjuti.",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onSubmit()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark)
                ) {
                    Text("OK", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BuatLaporanPreview() {
    MaterialTheme {
        BuatLaporanScreen({}, {})
    }
}
