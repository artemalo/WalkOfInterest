package sfedu.ictis.walkOfInterest.data.model.dto

import com.google.gson.annotations.SerializedName

data class PoiInfoDto(
    @SerializedName("id") val id: Long,
    @SerializedName("point") val point: PointDto?,

    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?,

    @SerializedName("tags") val tags: List<TagNameDto>?,

    @SerializedName("status") val status: String?,

    @SerializedName("rating") val rating: Double?,
    @SerializedName("countRate") val countRate: Int?,

    @SerializedName("photoUrl") val photoUrl: String? = null
)