package com.example.app_go_play

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.app_go_play.feature.auth.presentation.ui.LoginScreen
import com.example.app_go_play.feature.auth.presentation.ui.RegisterScreen
import com.example.app_go_play.feature.auth.presentation.viewmodel.AuthViewModel
import com.example.app_go_play.feature.auth.presentation.viewmodel.AuthState
import com.example.app_go_play.feature.booking.presentation.ui.ConfirmBookingScreen
import com.example.app_go_play.feature.booking.presentation.ui.SelectTimeSlotScreen
import com.example.app_go_play.feature.court.presentation.ui.CourtDetailScreen
import com.example.app_go_play.feature.court.presentation.ui.CourtListScreen
import com.example.app_go_play.feature.home.presentation.ui.HomeScreen
import com.example.app_go_play.ui.theme.App_GO_PLAYTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App_GO_PLAYTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            navController.navigate("home") {
                popUpTo(navController.graph.findStartDestination().id) {
                    inclusive = true
                }
            }
        }
    }

    NavHost(navController = navController, startDestination = "login", modifier = modifier) {
        composable("login") {
            LoginScreen(authViewModel = authViewModel, navController = navController)
        }
        composable("register") {
            RegisterScreen(authViewModel = authViewModel, navController = navController)
        }
        composable("home") {
            HomeScreen(navController = navController)
        }

        composable(
            route = "court_list?query={query}",
            arguments = listOf(navArgument("query") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) {
            // SỬA LỖI: Gọi hàm với chữ ký chính xác
            CourtListScreen(
                onCourtClick = { courtId ->
                    navController.navigate("court_detail/$courtId")
                }
            )
        }

        composable(
            route = "court_detail/{courtId}",
            // SỬA LỖI: Đảm bảo kiểu dữ liệu là Int để khớp với model
            arguments = listOf(navArgument("courtId") { type = NavType.IntType })
        ) {
            CourtDetailScreen() // viewModel và courtId sẽ được Hilt và SavedStateHandle tự động xử lý
        }

        composable(
            route = "booking_screen/{courtId}",
            arguments = listOf(navArgument("courtId") { type = NavType.StringType })
        ) {
            SelectTimeSlotScreen(navController = navController)
        }

        composable(
            route = "confirm_booking/{courtId}/{selectedSlots}",
            arguments = listOf(
                navArgument("courtId") { type = NavType.StringType },
                navArgument("selectedSlots") { type = NavType.StringType }
            )
        ) {
            ConfirmBookingScreen(navController = navController)
        }

        // === CÁC MÀN HÌNH MỚI CHO THANH ĐIỀU HƯỚNG ===
        composable("my_events") {
            PlaceholderScreen("My Events Screen")
        }
        composable("profile") {
            PlaceholderScreen("Profile Screen")
        }
        composable("friends") {
            PlaceholderScreen("Friends Screen")
        }
        composable("booking_search") {
            PlaceholderScreen("Booking Search Screen")
        }
    }
}

@Composable
fun PlaceholderScreen(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text)
    }
}
