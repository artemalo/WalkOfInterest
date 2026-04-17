package sfedu.ictis.walkOfInterest.data.model

import com.google.gson.annotations.SerializedName
import sfedu.ictis.walkOfInterest.data.model.dto.PointDto

data class RouteRequest (
    @SerializedName("p1") val p1: PointDto,
    @SerializedName("p2") val p2: PointDto
)