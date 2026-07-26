package com.example.aplikasinagarikkn.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.outlined.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuratMenyuratScreen(
    onNavigateBack: () -> Unit
) {
    var keperluan by remember { mutableStateOf("") }
    var tipeSuratTerpilih by remember { mutableStateOf("") }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var dokumenTerpilih by remember { mutableStateOf(false) }

    val tipeSuratList = listOf(
        "Surat Keterangan Domisili",
        "Surat Keterangan Usaha (SKU)",
        "Surat Keterangan Tidak Mampu (SKTM)",
        "Surat Pengantar Nikah",
        "Surat Keterangan Kematian",
        "Lainnya"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // --- Header Section ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Column(modifier = Modifier.padding(top = 24.dp)) {
                Text(
                    text = "Layanan Surat",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ajukan pembuatan surat pengantar/keterangan langsung dari genggaman Anda.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            }
        }

        // --- Form Section ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text(
                text = "Form Pengajuan",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Dropdown Tipe Surat
            ExposedDropdownMenuBox(
                expanded = isDropdownExpanded,
                onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = tipeSuratTerpilih,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Pilih Jenis Surat") },
                    placeholder = { Text("Pilih jenis surat yang dibutuhkan") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )
                ExposedDropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false }
                ) {
                    tipeSuratList.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                tipeSuratTerpilih = selectionOption
                                isDropdownExpanded = false
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.Description,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = keperluan,
                onValueChange = { keperluan = it },
                label = { Text("Keperluan / Keterangan") },
                placeholder = { Text("Misal: Untuk melamar pekerjaan di PT XYZ") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Upload Dokumen
            Text(
                text = "Dokumen Pendukung (KTP/KK)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { dokumenTerpilih = true },
                contentAlignment = Alignment.Center
            ) {
                if (dokumenTerpilih) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.Description,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("KTP_Budi.jpg", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Outlined.UploadFile,
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Tap untuk Upload Dokumen", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { /* TODO: Submit Surat */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = tipeSuratTerpilih.isNotBlank() && keperluan.isNotBlank()
            ) {
                Text("Ajukan Surat", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SuratMenyuratPreview() {
    MaterialTheme { SuratMenyuratScreen({}) }
}
