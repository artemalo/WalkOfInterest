package sfedu.ictis.walkOfInterest.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DomainPickCategory(
    val id: Int,
    val name: String,
    val icon: String?,
    val subcategories: List<DomainPickSubcategory>
) : Parcelable