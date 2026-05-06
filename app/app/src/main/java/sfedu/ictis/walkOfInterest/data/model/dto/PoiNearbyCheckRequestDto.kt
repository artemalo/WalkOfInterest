package sfedu.ictis.walkOfInterest.data.model.dto

import com.google.gson.annotations.SerializedName

data class PoiNearbyCheckRequestDto(
    @SerializedName("point") val point: PointDto
)
