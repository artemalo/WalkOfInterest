package sfedu.ictis.walkOfInterest.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sfedu.ictis.walkOfInterest.domain.usecase.GetSavedPoisUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.GetTripsUseCase

class MainFeedViewModel(
    private val getTripsUseCase: GetTripsUseCase,
    private val getSavedPoisUseCase: GetSavedPoisUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainFeedUiState())
    val uiState: StateFlow<MainFeedUiState> = _uiState.asStateFlow()

    init {
        loadTrips()
    }

    fun refreshData() {
        if (uiState.value.selectedTab == MainTab.TRIPS) {
            loadTrips()
        } else {
            loadSpots()
        }
    }

    fun onTabClicked(tab: MainTab) {
        if (tab == MainTab.TRIPS) {
            _uiState.update { it.copy(selectedTab = tab, isCreateMenuVisible = false) }
            loadTrips()
        } else if (tab == MainTab.SPOTS) {
            _uiState.update { it.copy(selectedTab = tab, isCreateMenuVisible = false) }
            loadSpots()
        }
    }

    fun onPlusClicked() {
        _uiState.update { state ->
            state.copy(isCreateMenuVisible = !state.isCreateMenuVisible)
        }
    }

//    fun hideCreateMenu() {
//        _uiState.update { it.copy(isCreateMenuVisible = false, selectedTab = MainTab.TRIPS) }
//    }

    private fun loadTrips() {
        viewModelScope.launch {
            val domainTrips = getTripsUseCase()

            val uiTrips = domainTrips.map { trip ->
                FeedItem.Trip(
                    id = trip.id,
                    title = "${trip.addressFrom} → ${trip.addressTo}",
                    addressFrom = trip.addressFrom,
                    addressTo = trip.addressTo,
                    totalTime = trip.userSelectedTime,
                    totalPois = trip.totalPois,
                    bestTime = trip.bestRouteTime,
                    photo = trip.photo
                )
            }
            _uiState.update { it.copy(items = uiTrips) }
        }
    }

    private fun loadSpots() {
        viewModelScope.launch {
            val spots = getSavedPoisUseCase().map { poi ->
                FeedItem.Spot(
                    id = poi.poiId,
                    photo = poi.photo,
                    title = poi.name,
                    address = poi.address
                )
            }
            _uiState.update { it.copy(items = spots) }
        }
    }
}