package com.example.app_go_play.feature.booking.presentation.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.app_go_play.R
import com.example.app_go_play.feature.booking.presentation.viewmodel.ConfirmBookingViewModel
import java.time.format.DateTimeFormatter

@Composable
fun ConfirmBookingScreen(
    navController: NavController,
    viewModel: ConfirmBookingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var note by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Xử lý kết quả đặt sân
    LaunchedEffect(state.bookingResult) {
        state.bookingResult?.let { result ->
            result.onSuccess { booking ->
                // Nếu là tiền mặt thì đi thẳng tới màn hình thành công
                if (state.paymentMethod == "CASH") {
                    navController.navigate("booking_success/${booking.id}") { popUpTo("home") }
                    viewModel.clearBookingResult()
                }
            }.onFailure {
                Toast.makeText(context, "Lỗi: ${it.message}", Toast.LENGTH_LONG).show()
                viewModel.clearBookingResult()
            }
        }
    }

    // Xử lý mở link MoMo (Kịch bản B)
    LaunchedEffect(state.paymentUrl) {
        state.paymentUrl?.let { url ->
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
                // Sau khi mở link, có thể chuyển sang màn hình chờ thanh toán hoặc thành công tùy logic
                // Ở đây ta có thể tạm thời clear để không mở lại liên tục
            } catch (e: Exception) {
                Toast.makeText(context, "Không thể mở ứng dụng thanh toán", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text("Xác nhận đặt sân", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tổng cộng", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "đ${"%,.0f".format(state.totalPrice)}",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.Red,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.onConfirmBookingClicked(note.ifBlank { null }) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = !state.isBooking,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (state.isBooking) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        } else {
                            val btnText = if (state.paymentMethod == "MOMO") "Thanh toán qua MoMo" else "Xác nhận đặt sân"
                            Text(btnText, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Thông tin sân
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(state.courtName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(state.courtAddress, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    }
                }
            }

            // Danh sách khung giờ đã chọn
            item {
                Text("Khung giờ đã chọn", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
            }
            items(state.selectedSlots) { slot ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "${slot.startTime.format(DateTimeFormatter.ofPattern("HH:mm"))} - ${slot.endTime.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                        color = Color.DarkGray
                    )
                    Text("đ${"%,.0f".format(slot.price)}", fontWeight = FontWeight.Medium)
                }
            }

            // Chọn phương thức thanh toán
            item {
                Spacer(Modifier.height(24.dp))
                Text("Phương thức thanh toán", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                
                PaymentMethodItem(
                    title = "Tiền mặt (Thanh toán tại sân)",
                    subtitle = "Trả tiền trực tiếp cho chủ sân",
                    iconRes = R.drawable.ic_launcher_foreground, // Thay bằng icon tiền mặt thực tế
                    isSelected = state.paymentMethod == "CASH",
                    onSelect = { viewModel.onPaymentMethodChanged("CASH") }
                )
                
                Spacer(Modifier.height(8.dp))
                
                PaymentMethodItem(
                    title = "Ví điện tử MoMo",
                    subtitle = "Thanh toán online an toàn",
                    iconRes = R.drawable.ic_launcher_foreground, // Thay bằng logo MoMo thực tế
                    isSelected = state.paymentMethod == "MOMO",
                    onSelect = { viewModel.onPaymentMethodChanged("MOMO") }
                )
            }

            // Ghi chú
            item {
                Spacer(Modifier.height(24.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Ghi chú cho chủ sân") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun PaymentMethodItem(
    title: String,
    subtitle: String,
    iconRes: Int,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val borderColor = if (isSelected) Color.Red else Color.LightGray
    val backgroundColor = if (isSelected) Color.Red.copy(alpha = 0.05f) else Color.White

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor), RoundedCornerShape(12.dp))
            .clickable(onClick = onSelect)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon giả lập (Bạn nên thay bằng logo thực tế)
        Box(
            modifier = Modifier.size(40.dp).background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = if (isSelected) Color.Red else Color.Gray)
        }
        
        Spacer(Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(subtitle, fontSize = 12.sp, color = Color.Gray)
        }
        
        if (isSelected) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Red, modifier = Modifier.size(20.dp))
        } else {
            Box(modifier = Modifier.size(20.dp).border(1.dp, Color.LightGray, RoundedCornerShape(10.dp)))
        }
    }
}
