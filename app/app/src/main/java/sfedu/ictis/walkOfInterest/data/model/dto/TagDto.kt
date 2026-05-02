package sfedu.ictis.walkOfInterest.data.model.dto

import com.google.gson.annotations.SerializedName

data class TagDto(
    @SerializedName("id") val id: Int,
    @SerializedName("weight") val weight: Double?
)