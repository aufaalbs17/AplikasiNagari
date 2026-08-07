package com.example.aplikasinagarikkn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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

        // Initialize System Notification Channel
        com.example.aplikasinagarikkn.utils.NagariNotificationHelper.createNotificationChannel(this)

        // Request POST_NOTIFICATIONS permission for Android 13+ (API 33)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

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

    NavHost(
        navController = navController,
        startDestination = "login",
        enterTransition = { slideInHorizontally(initialOffsetX = { 300 }, animationSpec = tween(240)) + fadeIn(animationSpec = tween(240)) },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -300 }, animationSpec = tween(240)) + fadeOut(animationSpec = tween(240)) },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -300 }, animationSpec = tween(240)) + fadeIn(animationSpec = tween(240)) },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { 300 }, animationSpec = tween(240)) + fadeOut(animationSpec = tween(240)) }
    ) {
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
                onNavigateToNotifikasi = { navController.navigate("admin_notifikasi") },
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
        
        composable("admin_notifikasi") {
            NotifikasiScreen(
                onNavigateBack = { navController.popBackStack() },
                isAdminMode = true
            )
        }

        composable("notifikasi") {
            NotifikasiScreen(
                onNavigateBack = { navController.popBackStack() },
                isAdminMode = false
            )
        }
    }
}