package com.example.app_go_play.feature.court.presentation.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.app_go_play.R
import com.example.app_go_play.feature.court.domain.model.TimeSlot
import com.example.app_go_play.feature.court.presentation.viewmodel.CourtDetailState
import com.example.app_go_play.feature.court.presentation.viewmodel.CourtDetailViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CourtDetailScreen(
    navController: NavController,
    viewModel: CourtDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        bottomBar = {
            // STEP 4: Add a booking button at the bottom
            if (state.court != null) { // Only show if court details are loaded
                BookingBottomBar(state = state, onBookClick = {
                    // TODO: Navigate to confirmation screen
                })
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Loading state
            if (state.isLoadingCourt) {
                item { Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
                return@LazyColumn
            }

            // Error state
            state.error?.let {
                item { Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) { Text("Error: $it") } }
                return@LazyColumn
            }

            // Court Details Section
            state.court?.let { court ->
                item {
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = court.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    ImageSlider(thumbnailUrl = court.thumbnailUrl, imageUrls = court.imageUrls)
                    Spacer(modifier = Modifier.height(16.dp))

                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(text = court.address, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Rating: ${court.averageRating?.let { rating -> String.format(Locale.US, "%.1f", rating) } ?: "N/A"}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            // Date Picker Section
            item {
                DateSelector(selectedDate = state.selectedDate, onDateSelected = { viewModel.loadAvailableTimeSlots(it) })
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Time Slots Section
            item {
                TimeSlotSection(
                    state = state,
                    onSlotClick = { viewModel.toggleTimeSlotSelection(it) } // Connect to ViewModel
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Reviews Section
            state.reviews?.let {
                item {
                    Text("Reviews", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(horizontal = 16.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(it.content) { review ->
                    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), elevation = CardDefaults.cardElevation(2.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(review.reviewerName, fontWeight = FontWeight.Bold)
                            Text("Rated: ${review.rating}/5")
                            review.comment?.let { comment -> Text(comment) }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ImageSlider(thumbnailUrl: String?, imageUrls: List<String>) {
    val allImages = remember(thumbnailUrl, imageUrls) { (listOfNotNull(thumbnailUrl) + imageUrls).distinct() }
    if (allImages.isEmpty()) {
        Box(modifier = Modifier.fillMaxWidth().height(220.dp).background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
            Text("No images available")
        }
        return
    }
    val pagerState = rememberPagerState(pageCount = { allImages.size })
    val scope = rememberCoroutineScope()
    Box(contentAlignment = Alignment.BottomCenter) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth().height(220.dp), contentPadding = PaddingValues(horizontal = 16.dp), pageSpacing = 8.dp) { page ->
            Card(modifier = Modifier.fillMaxSize(), shape = MaterialTheme.shapes.medium) {
                AsyncImage(model = allImages[page], contentDescription = "Court Image ${page + 1}", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize(), placeholder = painterResource(id = R.drawable.ic_launcher_background), error = painterResource(id = R.drawable.ic_launcher_background))
            }
        }
        Row(Modifier.padding(bottom = 12.dp), horizontalArrangement = Arrangement.Center) {
            repeat(allImages.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else Color.LightGray
                Box(modifier = Modifier.padding(4.dp).clip(CircleShape).background(color).size(10.dp).clickable { scope.launch { pagerState.animateScrollToPage(iteration) } })
            }
        }
    }
}

@Composable
private fun DateSelector(selectedDate: LocalDate, onDateSelected: (LocalDate) -> Unit) {
    val dates = (0..7).map { LocalDate.now().plusDays(it.toLong()) }
    val formatter = DateTimeFormatter.ofPattern("E, dd/MM")
    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) { items(dates) { date ->
        val isSelected = date == selectedDate
        Button(onClick = { onDateSelected(date) }, colors = if (isSelected) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors()) { Text(text = date.format(formatter)) }
    } }
}

@Composable
private fun TimeSlotSection(state: CourtDetailState, onSlotClick: (TimeSlot) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text("Available Time Slots", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        if (state.isLoadingSlots) {
            CircularProgressIndicator()
        } else if (state.timeSlots.isEmpty()) {
            Text("No available slots for this day.")
        } else {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(horizontal = 1.dp)) {
                items(state.timeSlots) { slot ->
                    TimeSlotItem(slot = slot, isSelected = slot in state.selectedTimeSlots, onClick = { onSlotClick(slot) })
                }
            }
        }
    }
}

@Composable
private fun TimeSlotItem(slot: TimeSlot, isSelected: Boolean, onClick: () -> Unit) {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val startTime = slot.startTime.format(formatter)
    val endTime = slot.endTime.format(formatter)

    val containerColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        slot.isAvailable -> MaterialTheme.colorScheme.surface
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    }
    val contentColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        slot.isAvailable -> MaterialTheme.colorScheme.onSurface
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    }
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline

    Box(
        modifier = Modifier
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .background(containerColor, RoundedCornerShape(8.dp))
            .clickable(enabled = slot.isAvailable, onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("$startTime - $endTime", color = contentColor, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
private fun BookingBottomBar(state: CourtDetailState, onBookClick: () -> Unit) {
    val isEnabled = state.selectedTimeSlots.isNotEmpty()
    val totalPrice = state.selectedTimeSlots.sumOf { it.price }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Total Price", style = MaterialTheme.typography.labelMedium)
                Text("đ${"%,.0f".format(totalPrice)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = onBookClick,
                enabled = isEnabled,
                modifier = Modifier.height(48.dp)
            ) {
                Text("Book Now")
            }
        }
    }
}
