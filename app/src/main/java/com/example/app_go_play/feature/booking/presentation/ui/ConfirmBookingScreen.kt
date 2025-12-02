package com.example.app_go_play.feature.booking.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.app_go_play.ui.theme.App_GO_PLAYTheme
import java.text.NumberFormat
import java.util.Locale

// Hàm tiện ích để định dạng giá tiền
private fun formatPriceVND(price: Int): String {
    return "${NumberFormat.getNumberInstance(Locale.GERMANY).format(price)} vnd"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmBookingScreen(navController: NavController) {
    // TODO: Khởi tạo ViewModel và lấy State

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirm Booking", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = { Footer(navController) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFF5F5F5))
        ) {
            LocationSection()
            Spacer(modifier = Modifier.height(8.dp))
            BookingDetailSection()
            Spacer(modifier = Modifier.height(8.dp))
            PaymentSection()
        }
    }
}

@Composable
private fun LocationSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // TODO: Lấy dữ liệu sân từ State
        Text("Celadon", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text("31/2 Kenh Muoi Chin Thang Nam, Son Ky, Tan Phu, Ho Chi Minh City", color = Color.Gray, fontSize = 14.sp)
    }
}

@Composable
private fun BookingDetailSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("BOOKING DETAIL", fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.weight(1f)) {
                Text("DATE", color = Color.Gray, fontSize = 14.sp)
                // TODO: Lấy dữ liệu ngày từ State
                Text("Nov 12, 2019", fontWeight = FontWeight.SemiBold)
            }
            Column(Modifier.weight(1f)) {
                Text("TIME", color = Color.Gray, fontSize = 14.sp)
                // TODO: Lấy dữ liệu giờ từ State
                Text("18:00 - 20:00", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun PaymentSection() {
    var paymentMethod by remember { mutableStateOf("card") }
    var cardPaymentOption by remember { mutableStateOf("deposit") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("PAYMENT", fontWeight = FontWeight.Bold)

        // -- Deposit & Total Price --
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            InfoBox(title = "DEPOSIT", value = formatPriceVND(64000), modifier = Modifier.weight(1f))
            InfoBox(title = "TOTAL PRICE", value = formatPriceVND(280000), modifier = Modifier.weight(1f))
        }

        // -- Payment Method --
        Text("PAYMENT METHOD", fontWeight = FontWeight.SemiBold)
        Column {
            // Option: Visa/Master card
            PaymentOptionRow(text = "Visa/Master card", selected = paymentMethod == "card", onClick = { paymentMethod = "card" })
            if (paymentMethod == "card") {
                Column(modifier = Modifier.padding(start = 32.dp)) {
                    PaymentOptionRow(text = "Total: ${formatPriceVND(280000)}", selected = cardPaymentOption == "total", onClick = { cardPaymentOption = "total" })
                    PaymentOptionRow(text = "Deposit: ${formatPriceVND(28000)}", selected = cardPaymentOption == "deposit", onClick = { cardPaymentOption = "deposit" })
                }
            }

            // Option: At the facility
            PaymentOptionRow(text = "At the facility", selected = paymentMethod == "facility", onClick = { paymentMethod = "facility" })
        }

        Text("The deposit must be paid within 2 hours", color = Color(0xFF4CAF50), fontSize = 12.sp)
    }
}

@Composable
private fun Footer(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // -- Discount Code --
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = "",
                onValueChange = {}, // TODO: Handle discount code change
                label = { Text("Discount code") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { /* TODO: Apply discount */ },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.height(56.dp)
            ) {
                Text("Apply")
            }
        }

        // -- Final Price & Pay Button --
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("DEPOSIT", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                // TODO: Tính toán lại deposit cuối cùng
                Text(formatPriceVND(160000), color = Color(0xFF007BFF), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Button(
                onClick = { /* TODO: Navigate to payment gateway */ },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.weight(1f).height(50.dp)
            ) {
                Text("Pay now", fontWeight = FontWeight.Bold)
            }
        }
    }
}


@Composable
private fun InfoBox(title: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.LightGray.copy(alpha = 0.3f))
            .padding(12.dp)
    ) {
        Column {
            Text(title, color = Color.Gray, fontSize = 12.sp)
            Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
private fun PaymentOptionRow(text: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = Color.Red)
        )
        Text(text, modifier = Modifier.padding(start = 8.dp))
    }
}


@Preview(showBackground = true)
@Composable
fun ConfirmBookingScreenPreview() {
    App_GO_PLAYTheme {
        ConfirmBookingScreen(rememberNavController())
    }
}
