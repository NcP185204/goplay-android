package com.example.app_go_play.feature.court.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.app_go_play.feature.court.domain.model.Court
import com.example.app_go_play.feature.court.presentation.viewmodel.CourtListState
import com.example.app_go_play.feature.court.presentation.viewmodel.CourtListViewModel

@Composable
fun CourtListScreen(
    navController: NavController, // Thêm navController để điều hướng
    viewModel: CourtListViewModel = hiltViewModel(),
    onCourtClick: (Int) -> Unit
) {
    val state by viewModel.courtsState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedSport by viewModel.selectedSport.collectAsState()
    val selectedDistrict by viewModel.selectedDistrict.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // 1. Header (Thanh tìm kiếm)
        HeaderSection(
            query = searchQuery,
            onQueryChange = { viewModel.onQueryChanged(it) },
            onShopClick = { 
                // Thực hiện điều hướng đến màn hình lịch sử
                navController.navigate("booking_history") 
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 2. Banner
            item { PromotionBanner() }

            // 3. Bộ lọc (Bộ môn & Địa điểm)
            item {
                FilterBarSection(
                    selectedSport = selectedSport,
                    onSportSelected = { viewModel.onSportChanged(it) },
                    selectedDistrict = selectedDistrict,
                    onDistrictSelected = { viewModel.onDistrictChanged(it) }
                )
            }

            // 4. Danh Sách Sân
            when (val currentState = state) {
                is CourtListState.Loading -> {
                    item {
                        Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color.Red)
                        }
                    }
                }
                is CourtListState.Success -> {
                    if (currentState.courts.content.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("Không tìm thấy sân phù hợp.", color = Color.Gray)
                            }
                        }
                    } else {
                        items(currentState.courts.content) { court ->
                            CourtListItem(court = court, onClick = { onCourtClick(court.id) })
                        }
                    }
                }
                is CourtListState.Error -> {
                    item {
                        Text("Lỗi: ${currentState.message}", color = Color.Red, modifier = Modifier.padding(16.dp))
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun FilterBarSection(
    selectedSport: String?,
    onSportSelected: (String?) -> Unit,
    selectedDistrict: String?,
    onDistrictSelected: (String?) -> Unit
) {
    val sports = listOf("Tất cả", "FOOTBALL", "BASKETBALL", "TENNIS", "BADMINTON", "VOLLEYBALL")
    val districts = listOf(
        "Tất cả", "Quận 1", "Quận 3", "Quận 4", "Quận 5", "Quận 6", "Quận 7", "Quận 8", 
        "Quận 10", "Quận 11", "Quận 12", "Bình Tân", "Bình Thạnh", "Gò Vấp", 
        "Phú Nhuận", "Tân Bình", "Tân Phú", "Thủ Đức", "Hóc Môn", "Củ Chi", "Nhà Bè", "Bình Chánh"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FilterDropdownItem(
            label = selectedSport ?: "Bộ môn",
            options = sports,
            onOptionSelected = onSportSelected,
            icon = Icons.Default.SportsSoccer,
            modifier = Modifier.weight(1f)
        )
        FilterDropdownItem(
            label = selectedDistrict ?: "Địa điểm",
            options = districts,
            onOptionSelected = onDistrictSelected,
            icon = Icons.Default.LocationOn,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun FilterDropdownItem(
    label: String,
    options: List<String>,
    onOptionSelected: (String?) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .clickable { expanded = true },
            shape = RoundedCornerShape(10.dp),
            color = Color.White,
            border = BorderStroke(1.dp, if (label != "Bộ môn" && label != "Địa điểm" && label != "Tất cả") Color.Red else Color(0xFFEEEEEE)),
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = if (label != "Bộ môn" && label != "Địa điểm" && label != "Tất cả") Color.Red else Color.Gray)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        color = if (label != "Bộ môn" && label != "Địa điểm" && label != "Tất cả") Color.Black else Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = if (label != "Bộ môn" && label != "Địa điểm" && label != "Tất cả") FontWeight.Bold else FontWeight.Normal
                    )
                }
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray)
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White).heightIn(max = 300.dp)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, fontSize = 14.sp) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun HeaderSection(query: String, onQueryChange: (String) -> Unit, onShopClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Tìm tên sân...", fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
            modifier = Modifier.weight(1f).height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color.Red,
                unfocusedBorderColor = Color.LightGray
            ),
            singleLine = true
        )
        Spacer(modifier = Modifier.width(12.dp))
        IconButton(
            onClick = onShopClick,
            modifier = Modifier
                .size(48.dp)
                .background(Color.White, RoundedCornerShape(12.dp))
                .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
        ) {
            Icon(Icons.Outlined.History, contentDescription = "History", tint = Color.Black)
        }
    }
}

@Composable
fun PromotionBanner() {
    Card(
        modifier = Modifier.fillMaxWidth().height(140.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box {
            AsyncImage(
                model = "https://img.freepik.com/free-vector/badminton-tournament-banner-template_23-2149432103.jpg",
                contentDescription = "Promotion",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.2f)))
        }
    }
}

@Composable
fun CourtListItem(court: Court, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
            AsyncImage(
                model = court.thumbnailUrl,
                contentDescription = court.name,
                modifier = Modifier.size(90.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(court.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                    Icon(Icons.Outlined.FavoriteBorder, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                }
                Text(court.address, style = MaterialTheme.typography.bodySmall, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoTag(text = "${court.pricePerHour.toInt() / 1000}k/giờ", containerColor = Color(0xFFFFEBEE), textColor = Color.Red)
                    InfoTag(text = if (court.address.contains(",")) court.address.split(",").last().trim() else "Hồ Chí Minh", containerColor = Color(0xFFE3F2FD), textColor = Color(0xFF1976D2))
                }
            }
        }
    }
}

@Composable
fun InfoTag(text: String, containerColor: Color, textColor: Color) {
    Surface(color = containerColor, shape = RoundedCornerShape(6.dp)) {
        Text(text = text, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 11.sp, fontWeight = FontWeight.Medium, color = textColor)
    }
}
