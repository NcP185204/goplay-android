package com.example.app_go_play

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
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
import com.example.app_go_play.feature.profile.presentation.ui.ProfileScreen
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

    // 1. Kiểm tra trạng thái đăng nhập MỘT LẦN DUY NHẤT khi app khởi động
    LaunchedEffect(Unit) {
        authViewModel.checkLoginStatus()
    }

    // 2. Xử lý điều hướng khi trạng thái xác thực thay đổi
    AuthNavigationEffects(authState = authState, navController = navController)

    // 3. Quyết định hiển thị màn hình nào dựa trên trạng thái
    when (authState) {
        is AuthState.Unknown -> {
            // Hiển thị màn hình chờ trong khi đang kiểm tra token
            SplashScreen()
        }
        else -> {
            val startDestination = if (authState is AuthState.Success) "home" else "login"
            NavHost(navController = navController, startDestination = startDestination, modifier = modifier) {
                composable("login") {
                    LoginScreen(authViewModel = authViewModel, navController = navController)
                }
                composable("register") {
                    RegisterScreen(authViewModel = authViewModel, navController = navController)
                }
                composable("home") {
                    HomeScreen(navController = navController)
                }
                composable("profile") { 
                    ProfileScreen(navController = navController, authViewModel = authViewModel)
                }
                composable("courts") { // Route mới cho Courts
                    CourtListScreen(onCourtClick = { courtId -> navController.navigate("court_detail/$courtId") })
                }
                composable(
                    route = "court_list?query={query}",
                    arguments = listOf(navArgument("query") { type = NavType.StringType; nullable = true; defaultValue = null })
                ) {
                    CourtListScreen(onCourtClick = { courtId -> navController.navigate("court_detail/$courtId") })
                }
                composable(
                    route = "court_detail/{courtId}",
                    arguments = listOf(navArgument("courtId") { type = NavType.IntType })
                ) {
                    CourtDetailScreen()
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
                composable("my_events") { PlaceholderScreen("My Events Screen") }
                composable("friends") { PlaceholderScreen("Friends Screen") }
                composable("booking_search") { PlaceholderScreen("Booking Search Screen") }
            }
        }
    }
}

@Composable
fun AuthNavigationEffects(authState: AuthState, navController: NavHostController) {
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                navController.navigate("home") { popUpTo("login") { inclusive = true } }
            }
            is AuthState.Unauthenticated -> {
                navController.navigate("login") { popUpTo(0) { inclusive = true } }
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

@Composable
fun PlaceholderScreen(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text)
    }
}
