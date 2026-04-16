package sfedu.ictis.walkOfInterest.presentation.categories

import sfedu.ictis.walkOfInterest.domain.model.DomainCategory

data class CategoriesUiState(
    val categories: List<DomainCategory> = emptyList(),
    val addressFrom: String = "",
    val addressTo: String = "",
    val totalAvailableTime: Int = 0
) {
    val currentSelectedTime: Int
        get() = categories.filter { it.isSelect && it.selected > 0 }.sumOf { it.time }
}