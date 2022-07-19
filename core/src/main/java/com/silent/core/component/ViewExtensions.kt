package com.silent.core.component

import com.ilustris.animations.fadeIn
import com.ilustris.animations.fadeOut
import com.silent.core.databinding.ErrorViewBinding

fun ErrorViewBinding.showError(message: String, tryAgainClick: () -> Unit) {
    errorAnimation.playAnimation()
    errorMessage.text = message
    errorButton.setOnClickListener {
        tryAgainClick()
        root.fadeOut()
    }
    root.fadeIn()
}