package sfedu.ictis.walkOfInterest.presentation.categories

import android.util.Log
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

    fun initData(
        categories: List<DomainCategory>,
        addressFrom: String,
        addressTo: String,
        from: DomainPoint,
        to: DomainPoint,
        userSelectedTime: Int
    ) {
        _uiState.update {
            it.copy(
                categories = categories,
                addressFrom = addressFrom,
                addressTo = addressTo,
                from = from,
                to = to,
                userSelectedTime = userSelectedTime
            )
        }
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
            viewModelScope.launch {
                _events.emit(CategoriesEvent.ShowError("Выберите хотя бы одну категорию"))
            }
            return
        }

        val routePoints = selectedCategories.asSequence()
            .flatMap { category ->
                category.subcategories.asSequence().flatMap { subCategory ->
                    subCategory.pois.asSequence()
                        .filter { poi -> poi.selected && poi.order != null } // TODO: bag - fix order = +reorder() in backend
                        .map { poi ->
                            RoutePoint(
                                id = poi.id,
                                lat = poi.lat,
                                lon = poi.lon,
                                categoryId = category.id,
                                name = poi.name ?: "-",
                                nameCat = category.name,
                                nameSubcat = subCategory.name,
                                order = poi.order!!
                            )
                        }
                }
            }
            .sortedBy { it.order }
            .toList()

        Log.i("CategoriesVM", "Trip selectedPois (sorted by order): ${routePoints.size} points")
        routePoints.forEach {
            Log.i("CategoriesVM", "  [${it.order}] ${it.name} cat=${it.nameCat}")
        }

        if (routePoints.isEmpty()) {
            viewModelScope.launch {
                _events.emit(CategoriesEvent.ShowError("Нет выбранных точек для маршрута"))
            }
            return
        }

        val trip = DomainTrip(
            id = UUID.randomUUID().toString(),
            addressFrom = state.addressFrom,
            addressTo = state.addressTo,
            from = state.from,
            to = state.to,

            bestRouteTime = null,
            userSelectedTime = state.userSelectedTime,

            totalPois = routePoints.size,
            selectedPois = routePoints
        )


        viewModelScope.launch {
            saveTripUseCase(trip)

            _events.emit(CategoriesEvent.NavigateToRoutes)
        }
    }

    fun updateCategoryPois(category: DomainCategory, time: Int, allPoisCount: Int, selectedCount: Int) {
        _uiState.update { state ->
            val updatedCategories = state.categories.map { cat ->
                if (cat.id == category.id)
                    cat.copy(
                        subcategories = category.subcategories,
                        totalPois = allPoisCount,
                        selected = selectedCount,
                        time = time,
                        isSelect = if (selectedCount <= 0) false else cat.isSelect
                    )
                else cat
            }
            state.copy(categories = updatedCategories)
        }
    }
}