package sfedu.ictis.walkOfInterest.data.model.dto

import com.google.gson.annotations.SerializedName

data class RouteDto(
    @SerializedName("minTime") val minTime: Long,
    @SerializedName("distance") val distance: Double,
    @SerializedName("steps") val steps: Long,
    @SerializedName("route") val route: List<PointDto>,
)