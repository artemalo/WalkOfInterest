package sfedu.ictis.walkOfInterest.presentation.categories

import sfedu.ictis.walkOfInterest.domain.model.DomainCategory
import sfedu.ictis.walkOfInterest.domain.model.DomainPoint

data class CategoriesUiState(
    val categories: List<DomainCategory> = emptyList(),
    val addressFrom: String = "",
    val addressTo: String = "",

    val from: DomainPoint = DomainPoint(0.0, 0.0),
    val to: DomainPoint = DomainPoint(0.0, 0.0),

    val userSelectedTime: Int = 0
) {
    val countSpotsCurrent: Int
        get() = categories.filter { it.isSelect && it.selected > 0 }.sumOf { it.selected }
}