package com.example.app_go_play

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.app_go_play.feature.auth.presentation.ui.LoginScreen
import com.example.app_go_play.feature.auth.presentation.ui.RegisterScreen
import com.example.app_go_play.feature.auth.presentation.viewmodel.AuthViewModel
import com.example.app_go_play.feature.auth.presentation.viewmodel.AuthState
import com.example.app_go_play.feature.booking.presentation.ui.BookingHistoryScreen
import com.example.app_go_play.feature.booking.presentation.ui.BookingSuccessScreen
import com.example.app_go_play.feature.booking.presentation.ui.ConfirmBookingScreen
import com.example.app_go_play.feature.court.presentation.ui.CourtDetailScreen
import com.example.app_go_play.feature.court.presentation.ui.CourtListScreen
import com.example.app_go_play.feature.home.presentation.ui.HomeScreen
import com.example.app_go_play.feature.notification.presentation.ui.NotificationListScreen
import com.example.app_go_play.feature.profile.presentation.ui.ProfileScreen
import com.example.app_go_play.ui.theme.App_GO_PLAYTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Launcher để xin quyền thông báo
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Quyền đã được cấp
        } else {
            // Người dùng từ chối
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        askNotificationPermission()
        setContent {
            App_GO_PLAYTheme {
                AppNavigation()
            }
        }
    }

    private fun askNotificationPermission() {
        // Chỉ yêu cầu từ Android 13 (TIRAMISU) trở lên
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Home")
    object Courts : BottomNavItem("courts", Icons.Default.SportsBasketball, "Courts")
    object Profile : BottomNavItem("profile", Icons.Default.Person, "Profile")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(Unit) { authViewModel.checkLoginStatus() }
    AuthNavigationEffects(authState = authState, navController = navController)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.route in listOf("home", "courts", "profile")

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = Color.White) {
                    val items = listOf(BottomNavItem.Home, BottomNavItem.Courts, BottomNavItem.Profile)
                    items.forEach { item ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                selectedTextColor = Color.Black,
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray,
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (authState) {
                is AuthState.Unknown -> SplashScreen()
                else -> {
                    val startDestination = if (authState is AuthState.Success) "home" else "login"
                    NavHost(navController = navController, startDestination = startDestination) {
                        composable("login") { LoginScreen(authViewModel = authViewModel, navController = navController) }
                        composable("register") { RegisterScreen(authViewModel = authViewModel, navController = navController) }
                        composable("home") { HomeScreen(navController = navController) }
                        composable("profile") { ProfileScreen(navController = navController, authViewModel = authViewModel) }
                        composable("courts") { 
                            CourtListScreen(
                                navController = navController,
                                onCourtClick = { courtId -> navController.navigate("court_detail/$courtId") }
                            ) 
                        }
                        
                        composable(
                            route = "court_list?query={query}",
                            arguments = listOf(navArgument("query") { nullable = true; defaultValue = null })
                        ) { backStackEntry ->
                            CourtListScreen(
                                navController = navController,
                                onCourtClick = { courtId -> navController.navigate("court_detail/$courtId") }
                            )
                        }
                        
                        composable("booking_history") {
                            BookingHistoryScreen(navController = navController)
                        }

                        composable("notification_list") {
                            NotificationListScreen(navController = navController)
                        }

                        composable(
                            route = "court_detail/{courtId}",
                            arguments = listOf(navArgument("courtId") { type = NavType.IntType })
                        ) { CourtDetailScreen(navController = navController) }

                        composable(
                            route = "confirm_booking/{selectedSlots}/{courtName}/{courtAddress}",
                            arguments = listOf(
                                navArgument("selectedSlots") { type = NavType.StringType },
                                navArgument("courtName") { type = NavType.StringType },
                                navArgument("courtAddress") { type = NavType.StringType }
                            )
                        ) { ConfirmBookingScreen(navController = navController) }

                        composable(
                            route = "booking_success/{bookingId}",
                            arguments = listOf(navArgument("bookingId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val bookingId = backStackEntry.arguments?.getInt("bookingId")
                            BookingSuccessScreen(navController = navController, bookingId = bookingId)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AuthNavigationEffects(authState: AuthState, navController: NavHostController) {
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                if (navController.currentDestination?.route in listOf("login", "register")) {
                    navController.navigate("home") { popUpTo("login") { inclusive = true } }
                }
            }
            is AuthState.Unauthenticated -> {
                if (navController.currentDestination?.route !in listOf("login", "register")) {
                    navController.navigate("login") { popUpTo(0) { inclusive = true } }
                }
            }
            else -> Unit
        }
    }
}

@Composable
fun SplashScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
