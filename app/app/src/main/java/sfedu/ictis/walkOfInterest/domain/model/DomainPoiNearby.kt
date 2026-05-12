package sfedu.ictis.walkOfInterest.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DomainPoiNearby(
    val id: Long,
    val name: String,
    val categoryId: Int?,
    val categoryName: String?,
    val subcategoryName: String?,
    val point: DomainPoint?,
    val distanceMeters: Double?
) : Parcelable
