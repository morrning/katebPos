package com.hesabix.katebposapp.utils

object PersianNumberConverter {
    private val persianNumbers = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    private val englishNumbers = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')

    fun convertToPersian(text: String): String {
        if (text.isEmpty()) return text
        var result = text
        for (i in englishNumbers.indices) {
            result = result.replace(englishNumbers[i], persianNumbers[i])
        }
        return result
    }

    fun convertToEnglish(text: String): String {
        if (text.isEmpty()) return text
        var result = text
        for (i in persianNumbers.indices) {
            result = result.replace(persianNumbers[i], englishNumbers[i])
        }
        return result
    }

    fun formatNumber(number: Number): String {
        return convertToPersian(number.toString())
    }

    fun formatCurrency(amount: Double): String {
        return convertToPersian(String.format("%,.0f", amount))
    }
} 