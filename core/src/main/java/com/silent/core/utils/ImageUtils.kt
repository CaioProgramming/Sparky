package com.silent.core.utils

import android.R.drawable
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.silent.core.R
import java.io.IOException
import java.net.URL


object ImageUtils {

    fun getRandomHostPlaceHolder(): Int {
        val pics = listOf(R.drawable.man, R.drawable.teacher, R.drawable.woman)
        return pics.random()
    }

    fun getYoutubeThumb(videoId: String, quality: Quality = Quality.HIGH): String {
        return "https://img.youtube.com/vi/$videoId/${quality.suffix}"
    }

    fun getNotificationIcon(iconName: String?): IconData {
        return getIcons().find { it.name == iconName } ?: IconData(
            "Sparky",
            R.drawable.ic_sparky_notify
        )
    }

    fun getIcons(): List<IconData> {
        val icons = ArrayList<IconData>()
        val fields =
            (com.pavelsikun.materialdesignicons.R.drawable::class.java.declaredFields).filter {
                it.name.startsWith("zzz_")
            }
        fields.forEach { icon ->
            val drawableRes = drawable()
            icons.add(IconData(icon.name, icon.getInt(drawableRes)))
        }
        return ArrayList(icons.sortedBy { it.name })
    }

    enum class Quality(val suffix: String) {
        HIGH("hqdefault.jpg"), DEFAULT("default.jpg"), MEDIUM("mqdefault.jpg"), STANDARD("sddefault.jpg"), MAX(
            "maxresdefault.jpg"
        )
    }

    fun getBitmap(url: String?, context: Context): Bitmap? {
        return try {
            val url = URL(url)
            BitmapFactory.decodeStream(url.openConnection().getInputStream())
        } catch (e: IOException) {
            return null
        }
    }

}


data class IconData(val name: String, val drawable: Int)