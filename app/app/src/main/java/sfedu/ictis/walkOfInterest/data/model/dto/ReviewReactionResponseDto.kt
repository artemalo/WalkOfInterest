package sfedu.ictis.walkOfInterest.data.model.dto

import com.google.gson.annotations.SerializedName

data class ReviewReactionRequestDto(
    @SerializedName("type") val type: String
)

data class ReviewReactionResponseDto(
    val reviewId: Long,
    val likes: Int,
    val dislikes: Int,
    val myReaction: String?
)