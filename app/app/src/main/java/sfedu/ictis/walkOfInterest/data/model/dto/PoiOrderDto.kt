package sfedu.ictis.walkOfInterest.data.model.dto

import com.google.gson.annotations.SerializedName

data class PoiOrderDto(
    @SerializedName("id") val id: Long,
    @SerializedName("order") val order: Int
)