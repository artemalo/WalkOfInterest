package sfedu.ictis.walkOfInterest.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DomainPoiInfo(
    val id: Long,
    val point: DomainPoint?,

    val name: String?,
    val description: String?,

    val tags: List<DomainPoiTag>,

    val status: PoiStatus,

    val rating: Double,
    val countRate: Int
) : Parcelable