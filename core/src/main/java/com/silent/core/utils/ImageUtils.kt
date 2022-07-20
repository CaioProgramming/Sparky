package com.silent.core.utils

import android.R.drawable
import android.content.Context
import com.silent.core.R


object ImageUtils {

    fun getRandomIcon(): Int {
        val pics = listOf(com.silent.core.R.drawable.eye, com.silent.core.R.drawable.cat)
        return pics.random()
    }

    fun getRandomHostPlaceHolder(): Int {
        val pics = listOf(R.drawable.man, R.drawable.teacher, R.drawable.woman)
        return pics.random()
    }

    fun getYoutubeThumb(videoId: String) : String {
        return "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
    }

    fun getNotificationIcon(iconName: String?): IconData {
        return getIcons().find { it.name == iconName } ?: IconData("Sparky", R.drawable.ic_sparky_notify)
    }

    fun getIcons() : List<IconData>{
        val icons = ArrayList<IconData>()
        val fields = (com.pavelsikun.materialdesignicons.R.drawable::class.java.declaredFields).filter { it.name.startsWith("zzz_") }
        fields.forEach { icon ->
            val drawableRes = drawable()
            icons.add(IconData(icon.name, icon.getInt(drawableRes)))
        }
        return ArrayList(icons.sortedBy { it.name })
    }



}



data class IconData(val name:String, val drawable: Int)