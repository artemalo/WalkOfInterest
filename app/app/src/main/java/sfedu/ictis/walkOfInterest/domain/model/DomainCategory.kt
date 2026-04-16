package sfedu.ictis.walkOfInterest.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DomainCategory(
    val id: Int,
    val name: String,
    val description: String?,
    val icon: String?,
    val selected: Int,
    val totalPois: Int,
    val time: Int,
    val subcategories: List<DomainSubCategory>,
    val isSelect: Boolean = false
) : Parcelable