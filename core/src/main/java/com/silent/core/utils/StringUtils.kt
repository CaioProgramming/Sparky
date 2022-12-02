package com.silent.core.utils

private const val alphabet = "abcdefghijklmnopqrstuvwxyz"

fun String.toAlphabetInt(): Int {
    try {
        val regex = Regex("[^A-Za-z0-9 ]")
        val char = this.lowercase().replace(regex, "")
        var code = ""
        char.forEach {
            code += alphabet.indexOf(it, 0, true)
        }
        return code.substring(0, 10).replace("\\uFEFF", "").filter { it.isDigit() }.toInt()
    } catch (e: Exception) {
        e.printStackTrace()
        return 0
    }
}