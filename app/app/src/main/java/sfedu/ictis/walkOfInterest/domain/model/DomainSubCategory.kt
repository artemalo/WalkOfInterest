package sfedu.ictis.walkOfInterest.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DomainSubCategory(
    val id: Int,
    val name: String,
    val description: String?,
    val icon: String?,
    val pois: List<DomainPoi>
) : Parcelable