package com.example.aplikasinagarikkn.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val EmeraldDark = Color(0xFF064E3B)
private val EmeraldMedium = Color(0xFF047857)
private val BorderSubtle = Color(0xFFE2E8F0)
private val UrgentRed = Color(0xFFDC2626)

@Composable
fun AkunScreen(
    onNavigateToRiwayatLaporan: () -> Unit,
    onLogout: () -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // Real-time state from Database Repository
    val currentUser by com.example.aplikasinagarikkn.data.FirebaseRepository.currentUserState.collectAsState()
    val laporanList by com.example.aplikasinagarikkn.data.FirebaseRepository.laporanListState.collectAsState()
    val suratList by com.example.aplikasinagarikkn.data.FirebaseRepository.suratListState.collectAsState()

    val totalLaporanSaya = laporanList.count { it.userId == currentUser.id || currentUser.role == "admin" }
    val totalSuratSaya = suratList.count { it.userId == currentUser.id || currentUser.role == "admin" }

    // Dialog control states
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showNotificationDialog by remember { mutableStateOf(false) }
    var showSecurityDialog by remember { mutableStateOf(false) }
    var showHelpCenterDialog by remember { mutableStateOf(false) }
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Header Section (Gradient Background) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            EmeraldDark,
                            EmeraldMedium
                        )
                    )
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = if (currentUser.role == "admin") "Profil Panel Admin" else "Profil Akun Warga",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 40.dp)
            )
        }

        // --- Profile Info (Overlapping Header) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .offset(y = (-65).dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BorderSubtle),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar Box
                    Box(
                        modifier = Modifier
                            .size(86.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFECFDF5))
                            .border(3.dp, EmeraldMedium, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (currentUser.nama.isNotBlank()) currentUser.nama.first().uppercase() else "W",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = EmeraldDark
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = currentUser.nama,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F172A)
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "NIK: ${currentUser.nik}",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "📱 ${currentUser.noHp}  •  📍 ${currentUser.alamatJorong}",
                        fontSize = 11.sp,
                        color = Color(0xFF475569)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        color = EmeraldMedium.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = if (currentUser.role == "admin") "Ibu Wali / Perangkat Nagari Sako Selatan" else "Warga Nagari Sako Selatan Pasia Talang",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDark,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Summary Stats Badges (Laporan & Surat)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "$totalLaporanSaya",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = EmeraldDark
                                )
                                Text(
                                    text = "Pengaduan Saya",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "$totalSuratSaya",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = EmeraldDark
                                )
                                Text(
                                    text = "Surat Diajukan",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- Options Menu Section ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .offset(y = (-45).dp)
        ) {
            Text(
                text = "Pengaturan & Aktivitas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BorderSubtle),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    AkunMenuItem(
                        icon = Icons.Outlined.History,
                        title = "Riwayat Laporan Saya",
                        subtitle = "Cek status pengaduan yang dikirim",
                        onClick = onNavigateToRiwayatLaporan
                    )
                    HorizontalDivider(color = BorderSubtle, thickness = 0.8.dp)

                    AkunMenuItem(
                        icon = Icons.Outlined.Edit,
                        title = "Ubah Profil",
                        subtitle = "Perbarui nomor telepon & alamat",
                        onClick = { showEditProfileDialog = true }
                    )
                    HorizontalDivider(color = BorderSubtle, thickness = 0.8.dp)

                    AkunMenuItem(
                        icon = Icons.Outlined.Notifications,
                        title = "Pengaturan Notifikasi",
                        subtitle = "Atur pemberitahuan aplikasi",
                        onClick = { showNotificationDialog = true }
                    )
                    HorizontalDivider(color = BorderSubtle, thickness = 0.8.dp)

                    AkunMenuItem(
                        icon = Icons.Outlined.Lock,
                        title = "Keamanan Akun",
                        subtitle = "Ganti kata sandi & PIN",
                        onClick = { showSecurityDialog = true }
                    )
                    HorizontalDivider(color = BorderSubtle, thickness = 0.8.dp)

                    AkunMenuItem(
                        icon = Icons.AutoMirrored.Filled.HelpOutline,
                        title = "Bantuan & Layanan Nagari",
                        subtitle = "Pusat bantuan & kontak kantor Wali Nagari",
                        onClick = { showHelpCenterDialog = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Logout Button
            OutlinedButton(
                onClick = { showLogoutConfirmDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, UrgentRed.copy(alpha = 0.5f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = UrgentRed)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Keluar dari Akun Saya",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // =========================================================================
    // 🔀 INTERACTIVE FUNCTIONAL DIALOGS
    // =========================================================================

    // 1. Ubah Profil Dialog
    if (showEditProfileDialog) {
        var tempNama by remember { mutableStateOf(currentUser.nama) }
        var tempNoHp by remember { mutableStateOf(currentUser.noHp) }
        var tempAlamat by remember { mutableStateOf(currentUser.alamatJorong) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = {
                Text("Ubah Profil Akun", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Perbarui rincian profil warga Anda:", fontSize = 12.sp, color = Color(0xFF64748B))
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = tempNama,
                        onValueChange = { tempNama = it },
                        label = { Text("Nama Lengkap") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = tempNoHp,
                        onValueChange = { tempNoHp = it },
                        label = { Text("Nomor Telepon / WA") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = tempAlamat,
                        onValueChange = { tempAlamat = it },
                        label = { Text("Alamat Jorong / RT") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tempNama.isNotBlank()) {
                            com.example.aplikasinagarikkn.data.FirebaseRepository.updateProfile(
                                tempNama,
                                tempNoHp,
                                tempAlamat
                            )
                            showEditProfileDialog = false
                            Toast.makeText(context, "Profil $tempNama berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark)
                ) {
                    Text("Simpan Perubahan", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Batal", color = Color.Gray)
                }
            }
        )
    }

    // 2. Pengaturan Notifikasi Dialog
    if (showNotificationDialog) {
        var notifPengaduan by remember { mutableStateOf(true) }
        var notifSurat by remember { mutableStateOf(true) }
        var notifPengumuman by remember { mutableStateOf(true) }

        AlertDialog(
            onDismissRequest = { showNotificationDialog = false },
            title = {
                Text("Pengaturan Notifikasi", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Atur pemberitahuan yang ingin Anda terima:", fontSize = 12.sp, color = Color(0xFF64748B))
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Notifikasi Status Pengaduan", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Switch(checked = notifPengaduan, onCheckedChange = { notifPengaduan = it })
                    }

                    HorizontalDivider(color = BorderSubtle, thickness = 0.8.dp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Notifikasi Status Surat Digital", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Switch(checked = notifSurat, onCheckedChange = { notifSurat = it })
                    }

                    HorizontalDivider(color = BorderSubtle, thickness = 0.8.dp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Kabar & Pengumuman Nagari", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        Switch(checked = notifPengumuman, onCheckedChange = { notifPengumuman = it })
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showNotificationDialog = false
                        Toast.makeText(context, "Pengaturan Notifikasi berhasil disimpan!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark)
                ) {
                    Text("Simpan", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // 3. Keamanan Akun Dialog
    if (showSecurityDialog) {
        var sandiLama by remember { mutableStateOf("") }
        var sandiBaru by remember { mutableStateOf("") }
        var konfirmasiSandi by remember { mutableStateOf("") }
        var securityError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showSecurityDialog = false },
            title = {
                Text("Keamanan & Kata Sandi", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Ubah kata sandi akun Anda:", fontSize = 12.sp, color = Color(0xFF64748B))
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = sandiLama,
                        onValueChange = { sandiLama = it; securityError = null },
                        label = { Text("Kata Sandi Saat Ini") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = sandiBaru,
                        onValueChange = { sandiBaru = it; securityError = null },
                        label = { Text("Kata Sandi Baru") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = konfirmasiSandi,
                        onValueChange = { konfirmasiSandi = it; securityError = null },
                        label = { Text("Konfirmasi Kata Sandi Baru") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (securityError != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(securityError ?: "", color = MaterialTheme.colorScheme.error, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (sandiLama.isBlank() || sandiBaru.isBlank()) {
                            securityError = "Semua bidang kata sandi wajib diisi."
                        } else if (sandiBaru != konfirmasiSandi) {
                            securityError = "Konfirmasi kata sandi baru tidak cocok."
                        } else {
                            showSecurityDialog = false
                            Toast.makeText(context, "Kata Sandi Akun berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark)
                ) {
                    Text("Perbarui Sandi", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSecurityDialog = false }) {
                    Text("Batal", color = Color.Gray)
                }
            }
        )
    }

    // 4. Bantuan & Layanan Nagari Dialog (Hubungi Kami)
    if (showHelpCenterDialog) {
        AlertDialog(
            onDismissRequest = { showHelpCenterDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                    contentDescription = null,
                    tint = EmeraldMedium,
                    modifier = Modifier.size(44.dp)
                )
            },
            title = {
                Text("Hubungi Kami", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Kantor Wali Nagari Sako Selatan Pasia Talang",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Kecamatan Sungai Pagu, Kabupaten Solok Selatan, Sumatera Barat.",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = EmeraldDark),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Hubungi Kami",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Call, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("082174899901", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Email, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("sakoselatan2025@gmail.com", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.White)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/6282174899901"))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                    ) {
                        Text("Chat WhatsApp (082174899901)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showHelpCenterDialog = false }) {
                    Text("Tutup", fontWeight = FontWeight.Bold, color = EmeraldDark)
                }
            }
        )
    }

    // 5. Konfirmasi Logout Dialog
    if (showLogoutConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    tint = UrgentRed,
                    modifier = Modifier.size(44.dp)
                )
            },
            title = {
                Text("Keluar dari Akun?", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            },
            text = {
                Text("Apakah Anda yakin ingin keluar dari akun Aplikasi Nagari Sako Selatan Pasia Talang?", fontSize = 13.sp)
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirmDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = UrgentRed)
                ) {
                    Text("Ya, Keluar Akun", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmDialog = false }) {
                    Text("Batal", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
fun AkunMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFF1F5F9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = EmeraldDark,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color(0xFF0F172A)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFF94A3B8)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AkunScreenPreview() {
    MaterialTheme {
        AkunScreen({}, {})
    }
}
