package com.vanthien113.tiancamera.utils

import android.content.Context
import android.util.TypedValue
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

fun Float.toPx(context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this,
        context.resources.displayMetrics
    ).toInt()
}

fun Float.toPxFloat(context: Context): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this,
        context.resources.displayMetrics
    )
}

fun Float.dpToPx(context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        context.resources.displayMetrics
    ).toInt()
}

fun Int.toPx(context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this.toFloat(),
        context.resources.displayMetrics
    ).toInt()
}

fun Boolean?.getDefault(): Boolean {
    return this ?: false
}

fun String?.getDefault(): String {
    return this ?: ""
}

fun Long?.getDefault(): Long {
    return this ?: 0L
}

fun Long?.getDefaultToString(): String {
    return this?.toString() ?: ""
}

fun Double?.getDefault(): Double {
    return this ?: 0.0
}

fun Double?.getDefaultToString(): String {
    return this?.toString() ?: ""
}

fun Int?.getDefault(): Int {
    return this ?: 0
}

fun Float?.getDefault(): Float {
    return this ?: 0f
}

fun Int?.getDefaultToString(): String {
    return this?.toString() ?: ""
}

/**
 * return
 * 12345 -> 12.345
 */
fun Int.formatIntegerToString(): String {
    val integerFormatter = DecimalFormat("#,###", DecimalFormatSymbols(Locale.US))
    val parsed = BigDecimal(this.toString())
    return integerFormatter.format(parsed)
        .replace(regex = " ".toRegex(), replacement = "")
        .replace(regex = ",".toRegex(), replacement = ".")
}

/**
 * return
 * 12 -> 12
 * 1234 -> 1234
 * 12345 -> 12k3
 * TODO -> formatToStringValue()
 */
fun Int.formatToString(): String {
    return if (this < 10000) {
        "$this"
    } else {
        val thousand = this / 1000
        val thousandFormat = "${thousand}k "
        val hundred = (thousand - thousand * 1000) / 100
        val hundredFormat = if (hundred > 0) "$hundred" else ""
        "${thousandFormat}${hundredFormat}"
    }
}

fun Long.formatToString(): String {
    return if (this < 10000) {
        "$this"
    } else {
        val thousand = this / 1000
        val thousandFormat = "${thousand}k "
        val hundred = (thousand - thousand * 1000) / 100
        val hundredFormat = if (hundred > 0) "$hundred" else ""
        "${thousandFormat}${hundredFormat}"
    }
}


private val magnitudes = arrayOf("k", "Tr", "Tỷ", "T", "P", "E") // enough for long

fun Int.formatToStringValue(): String {
    var valueNumber = this
    val ret: String
    when {
        valueNumber >= 0 -> {
            ret = ""
        }
        valueNumber <= -9200000000000000000L -> {
            return "-9E"
        }
        else -> {
            ret = "-"
            valueNumber = -valueNumber
        }
    }
    if (valueNumber < 1000) return "$ret$valueNumber"
    var i = 0
    while (true) {
        if (valueNumber < 10000 && valueNumber % 1000 >= 100) return ret + valueNumber / 1000 + magnitudes[i]
        valueNumber /= 1000
        if (valueNumber < 1000) return ret + valueNumber + magnitudes[i]
        i++
    }
}