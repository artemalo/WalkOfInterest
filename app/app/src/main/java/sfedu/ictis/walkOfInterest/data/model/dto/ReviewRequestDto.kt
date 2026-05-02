package sfedu.ictis.walkOfInterest.data.model.dto

import com.google.gson.annotations.SerializedName

data class ReviewRequestDto(
    @SerializedName("rating") val rating: Int,
    @SerializedName("content") val content: String?
)