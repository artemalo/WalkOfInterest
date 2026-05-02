package sfedu.ictis.walkOfInterest.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DomainPoiTag(
    val subcategoryId: Int,
    val subcategoryName: String,
    val categoryId: Int?,
    val weight: Double?
) : Parcelable