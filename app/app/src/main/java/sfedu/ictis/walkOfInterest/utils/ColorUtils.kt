package sfedu.ictis.walkOfInterest.utils

import android.graphics.Color

fun calculateColorByCategory(categoryId: Int): Int {
    if (categoryId == 0) return Color.GRAY

    val hue = (categoryId * 45f) % 360f
    val hsv = floatArrayOf(hue, 0.8f, 0.65f)
    return Color.HSVToColor(hsv)
}