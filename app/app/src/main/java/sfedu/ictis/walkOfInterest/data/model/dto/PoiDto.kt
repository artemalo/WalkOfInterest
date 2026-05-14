package sfedu.ictis.walkOfInterest.data.model.dto

import com.google.gson.annotations.SerializedName

data class PoiDto(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("lang") val lang: String?,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double,
    @SerializedName("selected") val selected: Boolean = false,
    @SerializedName("rate") val rate: Double?,
    @SerializedName("count") val count: Int?,
    @SerializedName("photo") val photo: String?,

    @SerializedName("order") val order: Int? = null
)