package sfedu.ictis.walkOfInterest.data.model.dto

import com.google.gson.annotations.SerializedName

data class SubcategoryShortDto(
    @SerializedName("subcategoryId") val subcategoryId: Int,
    @SerializedName("subcategoryName") val subcategoryName: String?
)
