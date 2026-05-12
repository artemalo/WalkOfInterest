package sfedu.ictis.walkOfInterest.utils

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import sfedu.ictis.walkOfInterest.R

fun calculateColorByCategory(categoryId: Int): Int {
    if (categoryId == 0) return Color.GRAY

    val hue = (categoryId * 45f) % 360f
    val hsv = floatArrayOf(hue, 0.8f, 0.65f)
    return Color.HSVToColor(hsv)
}

fun calculateRouteColor(context: Context, timeDiffMinutes: Int): Int {
    val green = ContextCompat.getColor(context, R.color.green)
    val orange = ContextCompat.getColor(context, R.color.object_orange)
    val redOrange = ContextCompat.getColor(context, R.color.object_red_orange)
    val red = ContextCompat.getColor(context, R.color.object_red)

    val maxDiff = 60f
    val ratio = (timeDiffMinutes / maxDiff).coerceIn(0f, 1f)

    return when {
        ratio <= 0.33f -> {
            // 0 - 20 минут: Зеленого к Оранжевому
            val localRatio = ratio / 0.33f
            ColorUtils.blendARGB(green, orange, localRatio)
        }
        ratio <= 0.66f -> {
            // 20 - 40 минут: Оранжевого к Красно-Оранжевому
            val localRatio = (ratio - 0.33f) / 0.33f
            ColorUtils.blendARGB(orange, redOrange, localRatio)
        }
        else -> {
            // 40 - 60 минут: Красно-Оранжевого к Красному
            val localRatio = (ratio - 0.66f) / 0.34f
            ColorUtils.blendARGB(redOrange, red, localRatio)
        }
    }
}

fun formatLargeNumber(num: Int): String = when {
    num < 1000 -> num.toString()
    num < 1_000_000 -> {
        val thousands = num / 1000
        val remainder = num % 1000
        if (remainder > 0) "$thousands к $remainder" else "$thousands к"
    }
    num < 1_000_000_000 -> {
        val millions = num / 1_000_000
        val remainder = num % 1_000_000
        if (remainder > 0) "$millions кк ${formatLargeNumber(remainder)}" else "$millions кк"
    }
    else -> {
        val billions = num / 1_000_000_000
        val remainder = num % 1_000_000_000
        if (remainder > 0) "$billions ккк ${formatLargeNumber(remainder)}" else "$billions ккк"
    }
}