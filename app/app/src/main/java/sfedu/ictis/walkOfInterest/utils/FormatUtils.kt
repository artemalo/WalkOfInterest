package sfedu.ictis.walkOfInterest.utils

fun formatMinutes(totalMinutes: Int): String {
    val h = totalMinutes / 60
    val m = totalMinutes % 60
    return if (h > 0) "${h}ч ${m}м" else "${m}м"
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