package sfedu.ictis.walkOfInterest.presentation.categories

import sfedu.ictis.walkOfInterest.domain.model.DomainCategory
import sfedu.ictis.walkOfInterest.domain.model.DomainPoint

enum class CategoriesSortMode {
    SELECTED_FIRST,
    BY_TIME;

    fun toggle(): CategoriesSortMode =
        if (this == SELECTED_FIRST) BY_TIME else SELECTED_FIRST
}

data class CategoriesUiState(
    val categories: List<DomainCategory> = emptyList(),
    val sortMode: CategoriesSortMode = CategoriesSortMode.SELECTED_FIRST,
    val addressFrom: String = "",
    val addressTo: String = "",

    val from: DomainPoint = DomainPoint(0.0, 0.0),
    val to: DomainPoint = DomainPoint(0.0, 0.0),

    val userSelectedTime: Int = 0
) {
    val sortedCategories: List<DomainCategory>
        get() = when (sortMode) {
            CategoriesSortMode.SELECTED_FIRST ->
                categories.sortedWith(
                    compareByDescending<DomainCategory> { it.isSelect }
                        .thenBy { it.time }
                )
            CategoriesSortMode.BY_TIME ->
                categories.sortedBy { it.time }
        }

    val countSpotsCurrent: Int
        get() = categories.filter { it.isSelect && it.selected > 0 }.sumOf { it.selected }
}