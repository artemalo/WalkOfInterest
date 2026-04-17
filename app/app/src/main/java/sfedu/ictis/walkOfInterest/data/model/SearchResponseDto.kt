package sfedu.ictis.walkOfInterest.data.model

import com.google.gson.annotations.SerializedName
import sfedu.ictis.walkOfInterest.data.model.dto.CategoryDto

data class SearchResponse(
    @SerializedName("requestId") val requestId: String,
    @SerializedName("categories") val categories: List<CategoryDto>
)