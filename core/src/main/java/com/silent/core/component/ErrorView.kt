package com.silent.core.component

import android.content.Context
import android.util.AttributeSet
import android.view.ViewAnimationUtils
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import com.ilustris.animations.fadeIn
import com.silent.core.R
import com.silent.ilustriscore.core.utilities.visible
import kotlinx.android.synthetic.main.error_view.view.*

/**
 * Error view with Lottie Animation that can be reused on any app screen
 */
class ErrorView : FrameLayout {


    constructor(context: Context) : super(context) {
        init(null, 0)
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
        initView()
    }

    private fun initView() {
        inflate(context, R.layout.error_view, this)
    }

    fun showError() {
        if (ViewCompat.isAttachedToWindow(this)) {
            val x = width / 2
            val y = height / 2

            val radius = width.coerceAtLeast(height) / 2

            val anim = ViewAnimationUtils.createCircularReveal(this, x, y, 0f, radius.toFloat())

            visible()
            anim.start()
        } else {
            fadeIn()
        }
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        initView()
        val attributes = context.obtainStyledAttributes(
            attrs, R.styleable.ErrorView, defStyle, 0
        )
        try {
            val message = attributes.getString(R.styleable.ErrorView_message)
            val animation = attributes.getResourceId(R.styleable.ErrorView_animation, R.raw.sleep)
            error_message.text = message
            error_animation.setAnimation(animation)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            attributes.recycle()
        }
    }
}