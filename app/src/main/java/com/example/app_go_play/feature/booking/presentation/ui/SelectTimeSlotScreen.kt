package com.example.app_go_play.feature.booking.presentation.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.app_go_play.feature.booking.domain.model.SelectableDate
import com.example.app_go_play.feature.booking.domain.model.TimeSlot
import com.example.app_go_play.feature.booking.presentation.viewmodel.SelectTimeSlotState
import com.example.app_go_play.feature.booking.presentation.viewmodel.SelectTimeSlotViewModel
import com.example.app_go_play.ui.theme.App_GO_PLAYTheme
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private fun formatPrice(price: Int): String {
    return NumberFormat.getNumberInstance(Locale.GERMANY).format(price)
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
        topBar = {
            TopAppBar(
                title = { Text("Select Date & Time") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    // CẬP NHẬT ONCLICK: Chuẩn bị dữ liệu và điều hướng
                    val courtId = state.courtId ?: return@Button
                    if (state.selectedTimeSlots.isNotEmpty()) {
                        val selectedSlotsString = state.selectedTimeSlots.joinToString(separator = ",") { "${it.startTime}-${it.endTime}" }
                        // Mã hóa để đảm bảo an toàn khi truyền qua URL
                        val encodedSlots = URLEncoder.encode(selectedSlotsString, StandardCharsets.UTF_8.toString())
                        navController.navigate("confirm_booking/$courtId/$encodedSlots")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                enabled = state.selectedTimeSlots.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                val buttonText = if (state.totalPrice > 0) {
                    "Tạm tính: ${formatPrice(state.totalPrice)} VND"
                } else {
                    "Continue"
                }
                Text(buttonText)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading && state.dates.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                DateSelector(
                    dates = state.dates,
                    selectedDate = state.selectedDate,
                    onDateSelected = onDateSelected
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (state.selectedDate != null) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text("Available Time Slots", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        if(state.isLoading) {
                            Box(modifier = Modifier.fillMaxWidth().padding(top = 32.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        } else {
                            TimeSlotGrid(
                                timeSlots = state.timeSlots,
                                selectedTimeSlots = state.selectedTimeSlots,
                                onTimeSlotClicked = onTimeSlotToggled
                            )
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Please select a date to see available time slots.")
                    }
                }
            }
            state.error?.let {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(it, color = MaterialTheme.colorScheme.error)
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
    Column {
        Text(
            "Select Date",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(dates) { dateItem ->
                val isSelected = selectedDate == dateItem.date
                DateCard(
                    dayOfWeek = dateItem.dayOfWeek,
                    dayOfMonth = dateItem.dayOfMonth,
                    isSelected = isSelected,
                    onClick = { onDateSelected(dateItem.date) }
                )
            }
        }
    }
}

@Composable
private fun DateCard(dayOfWeek: String, dayOfMonth: String, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) Color.Red else Color.White
    val contentColor = if (isSelected) Color.White else Color.Black
    Card(
        modifier = Modifier
            .width(60.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(if (isSelected) 8.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(dayOfWeek, fontSize = 12.sp, color = contentColor.copy(alpha = 0.7f))
            Spacer(modifier = Modifier.height(4.dp))
            Text(dayOfMonth, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = contentColor)
        }
    }
}

@Composable
private fun TimeSlotGrid(
    timeSlots: List<TimeSlot>,
    selectedTimeSlots: List<TimeSlot>,
    onTimeSlotClicked: (TimeSlot) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 140.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(timeSlots) { slot ->
            TimeSlotItem(
                timeSlot = slot,
                isSelected = selectedTimeSlots.contains(slot),
                onClick = { onTimeSlotClicked(slot) }
            )
        }
    }
}

@Composable
private fun TimeSlotItem(timeSlot: TimeSlot, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = when {
        isSelected -> Color.Red
        timeSlot.isAvailable -> Color.White
        else -> Color.LightGray.copy(alpha = 0.5f)
    }
    val contentColor = when {
        isSelected -> Color.White
        timeSlot.isAvailable -> Color.Red
        else -> Color.Gray
    }
    val borderColor = if (isSelected || !timeSlot.isAvailable) Color.Transparent else Color.Red

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable(enabled = timeSlot.isAvailable, onClick = onClick)
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "${timeSlot.startTime} - ${timeSlot.endTime}",
            color = contentColor,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            textDecoration = if (!timeSlot.isAvailable) TextDecoration.LineThrough else null
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun SelectTimeSlotScreenPreview() {
    App_GO_PLAYTheme {
        val dummyDates = List(14) { i ->
            val date = LocalDate.now().plusDays(i.toLong())
            SelectableDate(
                date = date,
                dayOfWeek = date.format(DateTimeFormatter.ofPattern("E")),
                dayOfMonth = date.dayOfMonth.toString()
            )
        }
        val dummySlots = listOf(
            TimeSlot("08:00", "10:00", true),
            TimeSlot("10:00", "12:00", false),
            TimeSlot("12:00", "14:00", true),
            TimeSlot("14:00", "16:00", true),
            TimeSlot("16:00", "18:00", true),
            TimeSlot("18:00", "20:00", false),
        )
        val previewState = SelectTimeSlotState(
            dates = dummyDates,
            timeSlots = dummySlots,
            selectedDate = dummyDates[2].date,
            selectedTimeSlots = listOf(dummySlots[0], dummySlots[2]),
            totalPrice = 400000
        )

        SelectTimeSlotContent(
            navController = rememberNavController(),
            state = previewState,
            onDateSelected = {},
            onTimeSlotToggled = {}
        )
    }
}
