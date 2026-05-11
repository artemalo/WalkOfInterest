package sfedu.ictis.walkOfInterest.presentation.poi.add.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sfedu.ictis.walkOfInterest.domain.usecase.GetSubcategoriesByCategoryUseCase

class SubcategoryPickViewModel(
    private val getSubcategoriesByCategoryUseCase: GetSubcategoriesByCategoryUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(SubcategoryPickUiState())
    val state: StateFlow<SubcategoryPickUiState> = _state.asStateFlow()

    private var categoryId: Int? = null
    private var currentPage: Int = 0
    private var searchQuery: String = ""

    private var loadJob: Job? = null
    private var searchDebounceJob: Job? = null

    fun init(catId: Int) {
        if (categoryId == catId && _state.value.items.isNotEmpty()) return
        categoryId = catId
        reset()
        loadNextPage()
    }

    fun onSearch(query: String) {
        if (query == searchQuery) return
        searchQuery = query

        searchDebounceJob?.cancel()
        searchDebounceJob = viewModelScope.launch {
            delay(350L)
            reset()
            loadNextPage()
        }
    }

    fun loadNextPage() {
        val state = _state.value
        if (state.isLoading || state.isLastPage) return
        val catId = categoryId ?: return

        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            getSubcategoriesByCategoryUseCase(
                categoryId = catId,
                search     = searchQuery.takeIf { it.isNotBlank() },
                page       = currentPage
            ).onSuccess { page ->
                _state.update { prev ->
                    prev.copy(
                        items      = prev.items + page.content,
                        isLoading  = false,
                        isLastPage = page.isLast,
                        error      = null
                    )
                }
                currentPage++
            }.onFailure { err ->
                _state.update { it.copy(isLoading = false, error = err.message) }
            }
        }
    }

    private fun reset() {
        loadJob?.cancel()
        currentPage = 0
        _state.value = SubcategoryPickUiState(isLoading = false)
    }
}