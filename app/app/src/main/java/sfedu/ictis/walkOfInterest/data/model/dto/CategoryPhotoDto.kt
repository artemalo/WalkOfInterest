package sfedu.ictis.walkOfInterest.data.model.dto

import com.google.gson.annotations.SerializedName

data class CategoryPhotoDto(
    @SerializedName("categoryId") val categoryId: Int,
    @SerializedName("photoUrl") val photoUrl: String?
)
