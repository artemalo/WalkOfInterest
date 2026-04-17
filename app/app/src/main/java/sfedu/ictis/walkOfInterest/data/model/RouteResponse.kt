package sfedu.ictis.walkOfInterest.data.model

import com.google.gson.annotations.SerializedName
import sfedu.ictis.walkOfInterest.data.model.dto.PointDto

data class RouteResponse(
    @SerializedName("minTime") val minTime: Int,
    @SerializedName("distance") val distance: Double,
    @SerializedName("route") val route: List<PointDto>
)