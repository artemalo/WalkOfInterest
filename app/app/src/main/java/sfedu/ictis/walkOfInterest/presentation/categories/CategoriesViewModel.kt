package sfedu.ictis.walkOfInterest.presentation.categories

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import sfedu.ictis.walkOfInterest.domain.model.DomainCategory

class CategoriesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CategoriesUiState())
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    fun initData(categories: List<DomainCategory>, from: String, to: String, totalTime: Int) {
        _uiState.update { it.copy(
            categories = categories,
            addressFrom = from,
            addressTo = to,
            totalAvailableTime = totalTime
        ) }
    }

    fun toggleCategorySelection(categoryId: Int) {
        _uiState.update { state ->
            val updatedList = state.categories.map { category ->
                if (category.id == categoryId) {
                    category.copy(isSelect = !category.isSelect)
                } else {
                    category
                }
            }
            state.copy(categories = updatedList)
        }
    }
}