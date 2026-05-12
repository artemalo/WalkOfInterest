package sfedu.ictis.walkOfInterest.data.model.dto

import com.google.gson.annotations.SerializedName

data class PoiNearbyDto(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String?,
    @SerializedName("categoryId") val categoryId: Int?,
    @SerializedName("categoryName") val categoryName: String?,
    @SerializedName("subcategoryName") val subcategoryName: String?,
    @SerializedName("point") val point: PointDto?,
    @SerializedName("distanceMeters") val distanceMeters: Double?
)
