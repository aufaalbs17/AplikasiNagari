package com.example.aplikasinagarikkn.ui.screens

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : BottomNavItem("home_tab", "Beranda", Icons.Filled.Home, Icons.Outlined.Home)
    object Surat : BottomNavItem("surat_tab", "Surat", Icons.Filled.Description, Icons.Outlined.Description)
    object UMKM : BottomNavItem("umkm_tab", "UMKM", Icons.Filled.Store, Icons.Outlined.Storefront)
    object Akun : BottomNavItem("akun_tab", "Akun", Icons.Filled.Person, Icons.Outlined.Person)
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
        BottomNavItem.UMKM,
        BottomNavItem.Akun
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                contentDescription = screen.title
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        },
                        selected = isSelected,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
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
            modifier = Modifier.padding(innerPadding),
            enterTransition = { slideInHorizontally(initialOffsetX = { 250 }, animationSpec = tween(260)) + fadeIn(animationSpec = tween(260)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -250 }, animationSpec = tween(260)) + fadeOut(animationSpec = tween(260)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { -250 }, animationSpec = tween(260)) + fadeIn(animationSpec = tween(260)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { 250 }, animationSpec = tween(260)) + fadeOut(animationSpec = tween(260)) }
        ) {
            composable(BottomNavItem.Home.route) {
                UserDashboardScreen(
                    onNavigateToBuatLaporan = onNavigateToBuatLaporan,
                    onNavigateToSurat = { navController.navigate(BottomNavItem.Surat.route) },
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
