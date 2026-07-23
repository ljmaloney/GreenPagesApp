package com.green.yp.app.utils

import kotlin.math.roundToLong

/**
 * Formats a Double as a USD currency string with thousand separators and 2 decimal places.
 * Example: 1500.0 -> "1,500.00"
 */
fun Double.formatCurrency(): String {
    val absoluteValue = kotlin.math.abs(this)
    val roundedCents = (absoluteValue * 100.0).roundToLong()
    val dollars = roundedCents / 100
    val cents = roundedCents % 100

    val dollarString = dollars.toString()
    val formattedDollars = StringBuilder()
    
    var count = 0
    for (i in dollarString.length - 1 downTo 0) {
        formattedDollars.append(dollarString[i])
        count++
        if (count % 3 == 0 && i > 0) {
            formattedDollars.append(',')
        }
    }
    
    val sign = if (this < 0) "-" else ""
    val centsString = cents.toString().padStart(2, '0')
    
    return "$sign${formattedDollars.reverse()}.$centsString"
}
