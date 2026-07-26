package com.example.aplikasinagarikkn.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomNavItem("home_tab", "Beranda", Icons.Filled.Home)
    object Surat : BottomNavItem("surat_tab", "Surat", Icons.Filled.Description)
    object UMKM : BottomNavItem("umkm_tab", "UMKM", Icons.Filled.Store)
    object Akun : BottomNavItem("akun_tab", "Akun", Icons.Filled.Person)
}

@Composable
fun UserMainScreen(
    onNavigateToBuatLaporan: () -> Unit,
    onNavigateToRiwayatLaporan: () -> Unit,
    onNavigateToNotifikasi: () -> Unit,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Surat,
        BottomNavItem.UMKM,
        BottomNavItem.Akun
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) {
                // Halaman Dashboard Utama
                UserDashboardScreen(
                    onNavigateToBuatLaporan = onNavigateToBuatLaporan,
                    onNavigateToNotifikasi = onNavigateToNotifikasi
                )
            }
            composable(BottomNavItem.Surat.route) {
                SuratMenyuratScreen(onNavigateBack = { navController.navigate(BottomNavItem.Home.route) })
            }
            composable(BottomNavItem.UMKM.route) {
                UMKMScreen(onNavigateBack = { navController.navigate(BottomNavItem.Home.route) })
            }
            composable(BottomNavItem.Akun.route) {
                AkunScreen(
                    onNavigateToRiwayatLaporan = onNavigateToRiwayatLaporan,
                    onLogout = onLogout
                )
            }
        }
    }
}
