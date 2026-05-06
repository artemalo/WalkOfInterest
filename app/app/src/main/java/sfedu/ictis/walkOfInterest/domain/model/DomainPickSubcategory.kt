package sfedu.ictis.walkOfInterest.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DomainPickSubcategory(
    val id: Int,
    val name: String,

    val categoryId: Int? = null
) : Parcelable
