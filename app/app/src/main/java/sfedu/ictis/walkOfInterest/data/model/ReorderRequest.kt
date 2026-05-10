package sfedu.ictis.walkOfInterest.data.model

import com.google.gson.annotations.SerializedName
import sfedu.ictis.walkOfInterest.data.model.dto.PointDto

data class ReorderRequest(
    @SerializedName("p1") val p1: PointDto,
    @SerializedName("p2") val p2: PointDto,
    @SerializedName("pois") val pois: List<ReorderPoiItem>
)

data class ReorderPoiItem(
    @SerializedName("id") val id: Long,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double
)