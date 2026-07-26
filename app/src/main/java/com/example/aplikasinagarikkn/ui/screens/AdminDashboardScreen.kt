package com.example.aplikasinagarikkn.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigateToKelolaLaporan: () -> Unit,
    onNavigateToNotifikasi: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wali Nagari", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onNavigateToNotifikasi) {
                        Icon(Icons.Filled.Notifications, contentDescription = "Notifikasi")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Selamat Datang,",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Bapak Wali Nagari",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Statistik Laporan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start),
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatistikCard(title = "Total Masuk", count = "12", colorBg = MaterialTheme.colorScheme.primaryContainer, colorText = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.weight(1f))
                StatistikCard(title = "Menunggu", count = "5", colorBg = Color(0xFFEEEEEE), colorText = Color(0xFF757575), modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatistikCard(title = "Diproses", count = "4", colorBg = Color(0xFFFFF3E0), colorText = Color(0xFFF57C00), modifier = Modifier.weight(1f))
                StatistikCard(title = "Selesai", count = "3", colorBg = Color(0xFFE8F5E9), colorText = Color(0xFF388E3C), modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onNavigateToKelolaLaporan,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.Settings, contentDescription = null, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text("Kelola Laporan Masyarakat", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Logout", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun StatistikCard(title: String, count: String, colorBg: Color, colorText: Color, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = colorBg),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = count, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = colorText)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = title, style = MaterialTheme.typography.bodyMedium, color = colorText.copy(alpha = 0.8f))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminDashboardPreview() {
    MaterialTheme {
        AdminDashboardScreen({}, {}, {})
    }
}
