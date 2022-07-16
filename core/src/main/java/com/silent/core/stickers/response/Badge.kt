package com.silent.core.stickers.response

data class Badge(
    val code: String,
    val created_at: String,
    val creator_profile: Any,
    val description: String,
    val expires_at: String,
    val high: String,
    val hq: Boolean,
    val name: String,
    val pinned: Boolean,
    val serial_number: Int,
    val src: String,
    val starts_on: String,
    val unique_id: String,
    val updated_at: String,
    val user_badge_id: String
)