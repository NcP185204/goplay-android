package com.example.app_go_play.feature.booking.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.app_go_play.feature.booking.domain.model.SelectableDate
import com.example.app_go_play.feature.court.domain.model.TimeSlot
import com.example.app_go_play.feature.booking.presentation.viewmodel.SelectTimeSlotState
import com.example.app_go_play.feature.booking.presentation.viewmodel.SelectTimeSlotViewModel
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// Palette màu Thể thao Năng động
private val SportPrimary = Color(0xFF2563EB)
private val SportPrimaryVariant = Color(0xFF1D4ED8)
private val SportAccent = Color(0xFFF97316)
private val SportBackground = Color(0xFFF8FAFC)

private fun formatPrice(price: Double): String {
    return NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(price)
}

@Composable
fun SelectTimeSlotScreen(
    navController: NavController,
    viewModel: SelectTimeSlotViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    SelectTimeSlotContent(navController, uiState, viewModel::onDateSelected, viewModel::onTimeSlotToggled)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectTimeSlotContent(
    navController: NavController,
    state: SelectTimeSlotState,
    onDateSelected: (LocalDate) -> Unit,
    onTimeSlotToggled: (TimeSlot) -> Unit
) {
    Scaffold(
        containerColor = SportBackground,
        topBar = {
            TopAppBar(
                title = { Text("Chọn ngày & giờ chơi", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            if (state.selectedTimeSlots.isNotEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth().shadow(24.dp),
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp).navigationBarsPadding(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Đã chọn ${state.selectedTimeSlots.size} khung giờ", color = Color.Gray, fontSize = 11.sp)
                            Text(formatPrice(state.totalPrice.toDouble()), fontWeight = FontWeight.Black, color = SportPrimary, fontSize = 20.sp)
                        }
                        Button(
                            onClick = { /* Navigate to confirm */ },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SportPrimary),
                            modifier = Modifier.height(50.dp).width(130.dp)
                        ) {
                            Text("Tiếp tục", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            DateSelector(
                dates = state.dates,
                selectedDate = state.selectedDate,
                onDateSelected = onDateSelected
            )

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = SportPrimary)
                }
            } else {
                val morningSlots = state.timeSlots.filter { it.startTime.hour < 12 }
                val afternoonSlots = state.timeSlots.filter { it.startTime.hour in 12..17 }
                val eveningSlots = state.timeSlots.filter { it.startTime.hour > 17 }

                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    if (morningSlots.isNotEmpty()) TimeSection("🌅 Buổi sáng", morningSlots, state.selectedTimeSlots, onTimeSlotToggled)
                    if (afternoonSlots.isNotEmpty()) TimeSection("☀️ Buổi chiều", afternoonSlots, state.selectedTimeSlots, onTimeSlotToggled)
                    if (eveningSlots.isNotEmpty()) TimeSection("🌙 Buổi tối", eveningSlots, state.selectedTimeSlots, onTimeSlotToggled)
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}

@Composable
fun TimeSection(title: String, slots: List<TimeSlot>, selectedSlots: List<TimeSlot>, onToggled: (TimeSlot) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 10.dp)) {
        Text(
            text = title, 
            fontWeight = FontWeight.ExtraBold, 
            color = Color(0xFF1E293B),
            fontSize = 15.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        
        // CẬP NHẬT: Chia thành 3 cột theo ý bạn
        slots.chunked(3).forEach { rowSlots ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowSlots.forEach { slot ->
                    TimeSlotCard(
                        modifier = Modifier.weight(1f).padding(vertical = 4.dp),
                        timeSlot = slot,
                        isSelected = selectedSlots.contains(slot),
                        onClick = { onToggled(slot) }
                    )
                }
                // Thêm Spacer để các Card không bị giãn ra khi hàng cuối có ít hơn 3 mục
                repeat(3 - rowSlots.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun TimeSlotCard(
    modifier: Modifier = Modifier,
    timeSlot: TimeSlot, 
    isSelected: Boolean, 
    onClick: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val isAvailable = timeSlot.available
    
    val backgroundColor = when {
        isSelected -> Brush.linearGradient(listOf(SportPrimary, SportPrimaryVariant))
        !isAvailable -> Brush.linearGradient(listOf(Color(0xFFF1F5F9), Color(0xFFF1F5F9)))
        else -> Brush.linearGradient(listOf(Color.White, Color.White))
    }

    Surface(
        modifier = modifier
            .shadow(if (isSelected) 4.dp else 1.dp, RoundedCornerShape(12.dp))
            .clickable(enabled = isAvailable, onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent
    ) {
        Box(modifier = Modifier.background(backgroundColor).padding(vertical = 10.dp, horizontal = 4.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Icon(
                    imageVector = if (isSelected) Icons.Default.CheckCircle else if (isAvailable) Icons.Default.Schedule else Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else if (isAvailable) SportPrimary else Color.LightGray,
                    modifier = Modifier.size(14.dp)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = "${timeSlot.startTime.format(formatter)}",
                    color = if (isSelected) Color.White else if (isAvailable) Color(0xFF1E293B) else Color.LightGray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    textDecoration = if (!isAvailable) TextDecoration.LineThrough else null,
                    textAlign = TextAlign.Center
                )
                
                if (isAvailable) {
                    Text(
                        text = formatPrice(timeSlot.price.toDouble()).replace(" ₫", "đ"),
                        color = if (isSelected) Color.White.copy(alpha = 0.9f) else SportAccent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun DateSelector(
    dates: List<SelectableDate>,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 14.dp).background(Color.White).padding(vertical = 12.dp)) {
        Text(
            "Chọn ngày thi đấu",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 15.sp,
            modifier = Modifier.padding(start = 16.dp, bottom = 12.dp),
            color = Color(0xFF1E293B)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(dates) { dateItem ->
                val isSelected = selectedDate == dateItem.date
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onDateSelected(dateItem.date) }
                ) {
                    Surface(
                        modifier = Modifier.size(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) SportPrimary else SportBackground,
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                dateItem.dayOfMonth, 
                                fontWeight = FontWeight.Black, 
                                fontSize = 17.sp, 
                                color = if (isSelected) Color.White else Color(0xFF1E293B)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        dateItem.dayOfWeek.uppercase(), 
                        fontSize = 9.sp, 
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) SportPrimary else Color.Gray
                    )
                }
            }
        }
    }
}
