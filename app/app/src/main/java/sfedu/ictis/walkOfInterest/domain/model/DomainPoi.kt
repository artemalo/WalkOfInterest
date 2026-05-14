package sfedu.ictis.walkOfInterest.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DomainPoi(
    val id: Long,
    val name: String?,
    val description: String?,
    val lang: String?,
    val lat: Double,
    val lon: Double,
    val selected: Boolean,
    val rate: Double?,
    val count: Int?,
    val photo: String?,

    val order: Int? = null
) : Parcelable