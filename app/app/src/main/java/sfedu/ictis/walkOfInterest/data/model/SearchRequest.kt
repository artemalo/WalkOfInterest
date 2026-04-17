package sfedu.ictis.walkOfInterest.data.model

import com.google.gson.annotations.SerializedName
import sfedu.ictis.walkOfInterest.data.model.dto.PointDto

data class SearchRequest(
    @SerializedName("p1") val p1: PointDto,
    @SerializedName("p2") val p2: PointDto,
    @SerializedName("maxTime") val time: Int,
    @SerializedName("lang") val lang: String,
    @SerializedName("requestId") val requestId: String
)