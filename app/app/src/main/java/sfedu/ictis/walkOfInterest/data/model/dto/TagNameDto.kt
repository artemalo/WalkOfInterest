package sfedu.ictis.walkOfInterest.data.model.dto

import com.google.gson.annotations.SerializedName

data class TagNameDto(
    @SerializedName("categoryId") val categoryId: Int?,
    @SerializedName("subcategoryName") val subcategoryName: String?,
    @SerializedName("tag") val tag: TagDto?
)