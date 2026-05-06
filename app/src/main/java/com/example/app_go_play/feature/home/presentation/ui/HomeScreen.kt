package com.example.app_go_play.feature.home.presentation.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.app_go_play.feature.booking.domain.model.Booking
import com.example.app_go_play.feature.court.domain.model.Court
import com.example.app_go_play.feature.home.presentation.viewmodel.HomeState
import com.example.app_go_play.feature.home.presentation.viewmodel.HomeViewModel
import java.util.Locale

// Palette màu Thể thao mới
val SportPrimary = Color(0xFF1A237E) // Deep Blue
val SportAccent = Color(0xFFC6FF00)  // Neon Lime
val SportBackground = Color(0xFFF4F7FA)

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    Scaffold(
        containerColor = SportBackground
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item { 
                PersonalizedHeader(
                    state = uiState,
                    onNotificationClick = { navController.navigate("notification_list") }
                ) 
            }

            item { 
                uiState.upcomingBooking?.let { booking ->
                    UpcomingMatchWidget(
                        booking = booking,
                        onDirectionClick = {
                            openGoogleMaps(context, booking.latitude, booking.longitude, booking.courtAddress)
                        }
                    )
                }
            }

            item { SearchBarSection(navController = navController) }
            
            item { PromotionCarousel() }

            item {
                SectionHeader("🔥 Sân bóng hàng đầu")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.topCourts) { court ->
                        FacilityCard(court = court) {
                            navController.navigate("court_detail/${court.id}")
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                SectionHeader("📍 Sân gần bạn nhất")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.nearestCourts) { court ->
                        FacilityCard(court = court, distance = "1.2 KM") {
                            navController.navigate("court_detail/${court.id}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PersonalizedHeader(state: HomeState, onNotificationClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box {
                AsyncImage(
                    model = state.userAvatar ?: "https://cdn-icons-png.flaticon.com/512/147/147144.png",
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .border(2.5.dp, SportAccent, CircleShape),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(Color.Green)
                        .border(2.dp, Color.White, CircleShape)
                        .align(Alignment.BottomEnd)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = "Xin chào 👋",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = state.userName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SportPrimary
                )
            }
        }
        
        IconButton(
            onClick = onNotificationClick,
            modifier = Modifier
                .shadow(4.dp, CircleShape)
                .background(Color.White, CircleShape)
                .size(44.dp)
        ) {
            Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = SportPrimary)
        }
    }
}

@Composable
fun UpcomingMatchWidget(booking: Booking, onDirectionClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(12.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(SportPrimary, Color(0xFF3F51B5))
                    )
                )
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        color = SportAccent.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "TRẬN ĐẤU SẮP TỚI",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = SportAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        booking.courtName,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            booking.timeSlotDetails.joinToString(" • "),
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                    }
                }
                
                Button(
                    onClick = onDirectionClick,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SportAccent),
                    contentPadding = PaddingValues(12.dp)
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = null, tint = SportPrimary, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun FacilityCard(court: Court, distance: String? = null, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(240.dp)
            .padding(bottom = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Box(modifier = Modifier.height(140.dp)) {
                AsyncImage(
                    model = court.thumbnailUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                // Rating Badge
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .background(Color.White, RoundedCornerShape(8.dp))
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        String.format(Locale.US, "%.1f", court.averageRating ?: 0.0),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                if (distance != null) {
                    Surface(
                        modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp),
                        color = SportPrimary,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(distance, color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    court.name,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = SportPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        court.address,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Từ ${String.format(Locale.US, "%,.0f", court.pricePerHour ?: 0.0)}đ",
                        color = Color(0xFFE91E63),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = SportPrimary, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(18.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(SportAccent)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = SportPrimary
        )
    }
}

@Composable
fun SearchBarSection(navController: NavController) {
    var text by remember { mutableStateOf("") }
    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        placeholder = { Text("Bạn muốn chơi ở đâu hôm nay?", fontSize = 14.sp, color = Color.Gray) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SportPrimary) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = SportPrimary,
            unfocusedBorderColor = Color.Transparent
        ),
        singleLine = true
    )
}

@Composable
fun PromotionCarousel() {
    val images = listOf(
        "https://img.freepik.com/free-vector/sport-promotion-banner-template_23-2149429447.jpg",
        "https://img.freepik.com/free-vector/soccer-stadium-advertising-banner_23-2148633390.jpg"
    )
    val pagerState = rememberPagerState(pageCount = { images.size })

    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            pageSpacing = 12.dp
        ) { page ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .shadow(8.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Box {
                    AsyncImage(
                        model = images[page],
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))))
                    )
                    Text(
                        "GIẢM GIÁ 30%\nKHUNG GIỜ VÀNG",
                        color = SportAccent,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(20.dp),
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

fun openGoogleMaps(context: Context, lat: Double?, lng: Double?, address: String) {
    val uri = if (lat != null && lng != null) {
        Uri.parse("google.navigation:q=$lat,$lng")
    } else {
        Uri.parse("geo:0,0?q=${Uri.encode(address)}")
    }
    val mapIntent = Intent(Intent.ACTION_VIEW, uri)
    mapIntent.setPackage("com.google.android.apps.maps")
    try {
        context.startActivity(mapIntent)
    } catch (e: Exception) {
        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }
}
