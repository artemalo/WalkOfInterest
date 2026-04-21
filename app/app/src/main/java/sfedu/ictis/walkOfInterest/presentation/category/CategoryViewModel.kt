package sfedu.ictis.walkOfInterest.presentation.category

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sfedu.ictis.walkOfInterest.domain.model.DomainCategory
import sfedu.ictis.walkOfInterest.domain.model.DomainPoi


class CategoryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    private var backupCategory: DomainCategory? = null

    fun initCategory(category: DomainCategory) {
        val sortedCategory = sortCategoryPois(category)
        updateCategoryState(sortedCategory)

        _uiState.update { it.copy(time = category.time) }
    }

    fun toggleEditMode() {
        val current = _uiState.value
        if (current.isEditMode) {
            _uiState.update {
                it.copy(
                    isEditMode = false,
                    category = backupCategory,
                    time = current.time ?: backupCategory?.time
                )
            }
            backupCategory = null
        } else {
            backupCategory = current.category
            _uiState.update { it.copy(isEditMode = true) }
        }
    }

    private fun updateCategoryState(category: DomainCategory) {
        val allCount = category.subcategories.sumOf { it.pois.size }
        val selectedCount = category.subcategories.sumOf { subcat -> subcat.pois.count { it.selected } }

        _uiState.update {
            it.copy(
                category = category,
                allPoisCount = allCount,
                selectedCount = selectedCount
            )
        }
        Log.d("CategoryViewModel", "${category.id}: allCount: $allCount, selectedCount: $selectedCount")
    }

    fun saveChanges(onSaved: (DomainCategory, Int, Int, Int) -> Unit) {
        val currentState = _uiState.value
        val currentCategory = currentState.category ?: return
        val updatedCategory = sortCategoryPois(currentCategory)

        _uiState.update {
            it.copy(
                category = updatedCategory,
                isEditMode = false,
                isTimeLoading = true,
                time = null
            )
        }
        backupCategory = null

        viewModelScope.launch {
            val newTime = fetchTimeFromServer(updatedCategory)
            _uiState.update { it.copy(time = newTime, isTimeLoading = false) }

            onSaved(updatedCategory, newTime, currentState.allPoisCount, currentState.selectedCount)
        }
    }

    private suspend fun fetchTimeFromServer(category: DomainCategory): Int {
        kotlinx.coroutines.delay(1500) // TODO: GET /time
        return 1
    }

    fun onPoiClicked(subcategoryId: Int, poiId: Long) {
        val currentState = _uiState.value
        if (!currentState.isEditMode) return

        val category = currentState.category ?: return

        val updatedSubcategories = category.subcategories.map { subcat ->
            if (subcat.id == subcategoryId) {
                val updatedPois = subcat.pois.map { poi ->
                    if (poi.id == poiId) poi.copy(selected = !poi.selected) else poi
                }
                subcat.copy(pois = updatedPois)
            } else subcat
        }

        updateCategoryState(category.copy(subcategories = updatedSubcategories))
    }

    private fun sortCategoryPois(category: DomainCategory): DomainCategory {
        val sortedSubcategories = category.subcategories.map { subCat ->
            val sortedPois = subCat.pois.sortedWith(
                compareByDescending<DomainPoi> { it.selected }
                    .thenByDescending { it.rate ?: 0.0 }
                    .thenByDescending { it.count ?: 0 }
            )
            subCat.copy(pois = sortedPois)
        }
        return category.copy(subcategories = sortedSubcategories)
    }
}