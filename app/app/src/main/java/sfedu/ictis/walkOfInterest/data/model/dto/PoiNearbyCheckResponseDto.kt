package sfedu.ictis.walkOfInterest.data.model.dto

import com.google.gson.annotations.SerializedName

data class PoiNearbyCheckResponseDto(
    @SerializedName("existsNearby") val existsNearby: Boolean,
    @SerializedName("pois") val pois: List<PoiNearbyDto>?
)
