package com.silent.core.stickers.response

data class Status(
    val error: Boolean,
    val message: String,
    val reason: Any
)