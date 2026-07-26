package com.example.aplikasinagarikkn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aplikasinagarikkn.ui.screens.*
import com.example.aplikasinagarikkn.ui.theme.AplikasiNagariKKNTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplikasiNagariKKNTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NagariApp()
                }
            }
        }
    }
}

@Composable
fun NagariApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onNavigateToUserDashboard = { navController.navigate("user_dashboard") },
                onNavigateToAdminDashboard = { navController.navigate("admin_dashboard") }
            )
        }
        
        composable("user_dashboard") {
            UserMainScreen(
                onNavigateToBuatLaporan = { navController.navigate("buat_laporan") },
                onNavigateToRiwayatLaporan = { navController.navigate("riwayat_laporan") },
                onNavigateToNotifikasi = { navController.navigate("notifikasi") },
                onLogout = { 
                    navController.navigate("login") {
                        popUpTo(0)
                    }
                }
            )
        }
        
        composable("buat_laporan") {
            BuatLaporanScreen(
                onNavigateBack = { navController.popBackStack() },
                onSubmit = { navController.popBackStack() }
            )
        }
        
        composable("riwayat_laporan") {
            RiwayatLaporanScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable("admin_dashboard") {
            AdminDashboardScreen(
                onNavigateToKelolaLaporan = { navController.navigate("kelola_laporan") },
                onNavigateToNotifikasi = { navController.navigate("notifikasi") },
                onLogout = { 
                    navController.navigate("login") {
                        popUpTo(0)
                    }
                }
            )
        }
        
        composable("kelola_laporan") {
            KelolaLaporanScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { id -> navController.navigate("detail_laporan/$id") }
            )
        }
        
        composable("detail_laporan/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
            DetailLaporanAdminScreen(
                laporanId = id,
                onNavigateBack = { navController.popBackStack() },
                onSubmitTanggapan = { navController.popBackStack() }
            )
        }
        
        composable("notifikasi") {
            NotifikasiScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}