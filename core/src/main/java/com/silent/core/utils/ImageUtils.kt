package com.silent.core.utils

import com.silent.core.R

object ImageUtils {

    fun getRandomIcon(): Int {
        val pics = listOf(R.drawable.eye, R.drawable.cat)
        return pics.random()
    }

}