package sfedu.ictis.walkOfInterest.data.model.dto

import com.google.gson.annotations.SerializedName

data class CategoryWithSubcategoriesDto(
    @SerializedName("categoryId") val categoryId: Int,
    @SerializedName("categoryName") val categoryName: String?,
    @SerializedName("categoryIcon") val categoryIcon: String?,
    @SerializedName("subcategories") val subcategories: List<SubcategoryShortDto>?
)
