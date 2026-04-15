package sfedu.ictis.walkOfInterest.utils

fun formatMinutes(totalMinutes: Int): String {
    val h = totalMinutes / 60
    val m = totalMinutes % 60
    return if (h > 0) "${h}ч ${m}м" else "${m}м"
}