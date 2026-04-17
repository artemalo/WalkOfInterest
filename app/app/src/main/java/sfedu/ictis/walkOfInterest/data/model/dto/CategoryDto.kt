package sfedu.ictis.walkOfInterest.data.model.dto

import com.google.gson.annotations.SerializedName

data class CategoryDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("icon") val icon: String?,
    @SerializedName("selected") val selected: Int = 0,
    @SerializedName("totalPois") val totalPois: Int = 0,
    @SerializedName("time") val time: Int = 0,
    @SerializedName("subcategories") val subcategories: List<SubCategoryDto>? = null
)