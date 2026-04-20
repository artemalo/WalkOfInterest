package sfedu.ictis.walkOfInterest.presentation.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sfedu.ictis.walkOfInterest.domain.model.DomainCategory
import sfedu.ictis.walkOfInterest.domain.model.DomainPoint
import sfedu.ictis.walkOfInterest.domain.model.DomainTrip
import sfedu.ictis.walkOfInterest.domain.model.RoutePoint
import sfedu.ictis.walkOfInterest.domain.usecase.SaveTripUseCase
import java.util.UUID

class CategoriesViewModel(
    private val saveTripUseCase: SaveTripUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(CategoriesUiState())
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CategoriesEvent>(
        extraBufferCapacity = 1
    )
    val events = _events.asSharedFlow()

    fun initData(categories: List<DomainCategory>, addressFrom: String, addressTo: String, from: DomainPoint, to: DomainPoint, totalTime: Int) {
        _uiState.update { it.copy(
            categories = categories,
            addressFrom = addressFrom,
            addressTo = addressTo,

            from = from,
            to = to,

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

    fun onGenerateRouteClicked() {
        val state = _uiState.value

        val selectedCategories = state.categories.filter { it.isSelect }

        if (selectedCategories.isEmpty()) {
            CategoriesEvent.ShowError("Выберите хотя бы одну категорию") // TODO TEST
            return
        }

        val routePoints = selectedCategories.flatMap { category ->
            category.subcategories.flatMap { subCategory ->
                subCategory.pois
                    .filter { poi -> poi.selected }
                    .map { poi ->
                        RoutePoint(
                            id = poi.id,
                            lat = poi.lat,
                            lon = poi.lon,
                            categoryId = category.id,
                            name = poi.name ?: "-",
                            nameCat = category.name,
                            nameSubcat = subCategory.name
                        )
                    }
            }
        }

        val trip = DomainTrip(
            id = UUID.randomUUID().toString(),
            addressFrom = state.addressFrom,
            addressTo = state.addressTo,
            from = state.from,
            to = state.to,

            totalTime = state.currentSelectedTime,
            totalPois = routePoints.size,
            selectedPois = routePoints
        )

        saveTripUseCase(trip)

        viewModelScope.launch {
            _events.emit(CategoriesEvent.NavigateToRoutes)
        }
    }
}