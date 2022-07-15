package com.silent.core.utils

import android.content.Context
import android.graphics.Color

object ColorUtils {

    fun getColors(context: Context): List<String> {
        val colors = ArrayList<String>()
        val fields = Class.forName("com.github.mcginty" + ".R\$color").declaredFields
        fields.forEach {
            if (it.getInt(null) != Color.TRANSPARENT) {
                val colorId = it.getInt(null)
                val color = context.resources.getColor(colorId)
                colors.add(toHex(color))
            }
        }
        return colors
    }

    fun toHex(intColor: Int): String {
        return java.lang.String.format("#%06X", 0xFFFFFF and intColor)
    }

}