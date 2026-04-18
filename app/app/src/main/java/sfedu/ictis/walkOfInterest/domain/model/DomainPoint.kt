package sfedu.ictis.walkOfInterest.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DomainPoint(
    val lat: Double,
    val lon: Double
) : Parcelable