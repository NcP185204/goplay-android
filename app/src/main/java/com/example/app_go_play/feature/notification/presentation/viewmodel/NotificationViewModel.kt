package com.example.app_go_play.feature.notification.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_go_play.feature.notification.data.remote.NotificationApi
import com.example.app_go_play.feature.notification.data.remote.dto.toDomain
import com.example.app_go_play.feature.notification.domain.model.Notification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationApi: NotificationApi
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    init {
        loadNotifications()
        loadUnreadCount()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = notificationApi.getNotifications(page = 0, size = 50)
                _notifications.value = response.content.map { it.toDomain() }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadUnreadCount() {
        viewModelScope.launch {
            try {
                _unreadCount.value = notificationApi.getUnreadCount()
            } catch (e: Exception) {}
        }
    }

    fun markAsRead(notificationId: Long) {
        viewModelScope.launch {
            try {
                notificationApi.markAsRead(notificationId)
                // Cập nhật local state
                _notifications.value = _notifications.value.map {
                    if (it.id == notificationId) it.copy(isRead = true) else it
                }
                loadUnreadCount()
            } catch (e: Exception) {}
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            try {
                notificationApi.markAllAsRead()
                _notifications.value = _notifications.value.map { it.copy(isRead = true) }
                _unreadCount.value = 0
            } catch (e: Exception) {}
        }
    }
}
