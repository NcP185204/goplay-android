package com.example.app_go_play.feature.court.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.app_go_play.feature.court.domain.model.Court
import com.example.app_go_play.feature.court.presentation.viewmodel.CourtListState
import com.example.app_go_play.feature.court.presentation.viewmodel.CourtListViewModel

@Composable
fun CourtListScreen(
    viewModel: CourtListViewModel = hiltViewModel(),
    onCourtClick: (Int) -> Unit // Callback to navigate to detail screen
) {
    val state by viewModel.courtsState.collectAsState()
    // SỬA LỖI: Khởi tạo searchQuery với giá trị từ ViewModel
    var searchQuery by remember { mutableStateOf(viewModel.initialQuery ?: "") }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                viewModel.searchCourts(name = it)
            },
            label = { Text("Search for courts...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            singleLine = true
        )

        Box(modifier = Modifier.weight(1f)) {
            when (val currentState = state) {
                is CourtListState.Idle -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Start typing to search for courts")
                    }
                }
                is CourtListState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is CourtListState.Success -> {
                    if (currentState.courts.content.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = "No courts found for your search.")
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(currentState.courts.content) { court ->
                                CourtListItem(court = court, onClick = { onCourtClick(court.id) })
                            }
                        }
                    }
                }
                is CourtListState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Error: ${currentState.message}")
                    }
                }
            }
        }
    }
}

@Composable
fun CourtListItem(court: Court, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(text = court.name, style = MaterialTheme.typography.titleMedium)
        Text(text = court.address, style = MaterialTheme.typography.bodySmall)
        Text(text = "Price: ${court.pricePerHour} VND/h", style = MaterialTheme.typography.bodyMedium)
    }
}
