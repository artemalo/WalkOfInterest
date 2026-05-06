package sfedu.ictis.walkOfInterest.data.model.dto

import com.google.gson.annotations.SerializedName

data class PoiAddDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("point") val point: PointDto,
    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("lang") val lang: String,
    @SerializedName("subcategoriesId") val subcategoriesId: List<Int>,
    /**
     * true - сервер пропускает 5-метровую защиту от дублей
     */
    @SerializedName("force") val force: Boolean? = null
)
