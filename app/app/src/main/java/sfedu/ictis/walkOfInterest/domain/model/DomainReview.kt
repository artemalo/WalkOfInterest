package sfedu.ictis.walkOfInterest.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DomainReview(
    val id: String,
    val authorUsername: String,
    val authorAvatarUrl: String?,
    val poiId: String,
    val poiName: String,
    val content: String,
    val rating: Int,
    val likes: Int,
    val dislikes: Int,
    val createdAtMillis: Long
) : Parcelable