package sfedu.ictis.walkOfInterest.presentation.category

import sfedu.ictis.walkOfInterest.domain.model.DomainCategory

data class CategoryUiState(
    val category: DomainCategory? = null,

    val allPoisCount: Int = 0,
    val selectedCount: Int = 0,
    val time: Int? = null,
    val isTimeLoading: Boolean = false,

    val isEditMode: Boolean = false
)