package com.example.app_go_play.feature.auth.domain.model

import com.google.gson.annotations.SerializedName

// SỬA LỖI: Chỉ định tường minh tên của enum khi gửi đến backend
enum class Role {
    @SerializedName("PLAYER")
    PLAYER,

    @SerializedName("OWNER")
    OWNER
}
