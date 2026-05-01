package sfedu.ictis.walkOfInterest.data.model.dto

data class ReviewDto(
    val id: Long,
    val authorUsername: String?,
    val authorAvatarUrl: String?,
    val poiId: Long?,
    val poiName: String?,
    val content: String?,
    val rating: Int?,
    val likes: Int?,
    val dislikes: Int?,
    val createdAt: String?
)