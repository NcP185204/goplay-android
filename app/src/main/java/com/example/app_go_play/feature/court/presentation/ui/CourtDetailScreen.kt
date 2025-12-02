package com.example.app_go_play.feature.court.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.app_go_play.feature.court.domain.model.TimeSlot
import com.example.app_go_play.feature.court.presentation.viewmodel.CourtDetailState
import com.example.app_go_play.feature.court.presentation.viewmodel.CourtDetailViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun CourtDetailScreen(
    viewModel: CourtDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        // Loading state for the whole screen
        if (state.isLoadingCourt) {
            item {
                Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            return@LazyColumn
        }

        // Error state
        state.error?.let {
            item {
                Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Error: $it", color = MaterialTheme.colorScheme.error)
                }
            }
            return@LazyColumn
        }

        // Court Details Section
        state.court?.let { court ->
            item {
                Text(text = court.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = court.address, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Rating: ${court.rating ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Date Picker Section
        item {
            DateSelector(selectedDate = state.selectedDate, onDateSelected = {
                viewModel.loadAvailableTimeSlots(it)
            })
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Time Slots Section
        item {
            TimeSlotSection(state = state)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Reviews Section
        state.reviews?.let { reviewPage -> // SỬA LỖI: Xóa khoảng trắng sau dấu {
            item {
                Text("Reviews", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(reviewPage.content) { review ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(review.reviewerName, fontWeight = FontWeight.Bold)
                        Text("Rated: ${review.rating}/5")
                        review.comment?.let { Text(it) }
                    }
                }
            }
        }
    }
}

@Composable
private fun DateSelector(selectedDate: LocalDate, onDateSelected: (LocalDate) -> Unit) {
    val dates = (-3..7).map { LocalDate.now().plusDays(it.toLong()) }
    val formatter = DateTimeFormatter.ofPattern("E, dd/MM")

    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(dates) {
            val isSelected = it == selectedDate
            Button(
                onClick = { onDateSelected(it) },
                colors = if (isSelected) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors()
            ) {
                Text(text = it.format(formatter))
            }
        }
    }
}

@Composable
private fun TimeSlotSection(state: CourtDetailState) {
    Column {
        Text("Available Time Slots", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        if (state.isLoadingSlots) {
            CircularProgressIndicator()
        } else if (state.timeSlots.isEmpty()) {
            Text("No available slots for this day.")
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.timeSlots) { slot ->
                    TimeSlotItem(slot = slot)
                }
            }
        }
    }
}

@Composable
private fun TimeSlotItem(slot: TimeSlot) {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    Button(onClick = { /* TODO: Handle booking */ }, enabled = slot.isAvailable) {
        Text(text = slot.startTime.format(formatter))
    }
}
