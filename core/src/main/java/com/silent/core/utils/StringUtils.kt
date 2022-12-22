package com.silent.core.utils

private const val alphabet = "abcdefghijklmnopqrstuvwxyz"

fun String.toAlphabetInt(): Int {
    return try {
        val regex = Regex("[^A-Za-z0-9 ]")
        val char = this.lowercase().replace(regex, "")
        var code = ""
        char.forEach {
            code += alphabet.indexOf(it, 0, true)
        }
        code.substring(0, 10).replace("\\uFEFF", "").filter { it.isDigit() }.substring(5).toInt()
    } catch (e: Exception) {
        e.printStackTrace()
        0
    }
}