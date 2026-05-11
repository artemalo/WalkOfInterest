package sfedu.ictis.walkOfInterest.presentation.poi.add.form

import sfedu.ictis.walkOfInterest.domain.model.DomainPickSubcategory

data class SubcategoryPickUiState(
    val items: List<DomainPickSubcategory> = emptyList(),
    val isLoading: Boolean = false,
    val isLastPage: Boolean = false,
    val error: String? = null
)