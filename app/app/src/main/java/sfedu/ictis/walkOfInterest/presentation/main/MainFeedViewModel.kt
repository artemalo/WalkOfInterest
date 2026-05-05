package sfedu.ictis.walkOfInterest.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sfedu.ictis.walkOfInterest.domain.usecase.GetTripsUseCase

class MainFeedViewModel(
    private val getTripsUseCase: GetTripsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainFeedUiState())
    val uiState: StateFlow<MainFeedUiState> = _uiState.asStateFlow()

    fun refreshData() {
        if (uiState.value.selectedTab == MainTab.TRIPS) {
            loadTrips()
        } else {
            loadSpots()
        }
    }

    init {
        loadTrips()
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
            state.copy(
                isCreateMenuVisible = !state.isCreateMenuVisible
            )
        }
    }

    fun hideCreateMenu() {
        _uiState.update { it.copy(isCreateMenuVisible = false, selectedTab = MainTab.TRIPS) }
    }

    private fun loadTrips() {
        viewModelScope.launch {
            val domainTrips = getTripsUseCase()

            val uiTrips = domainTrips.map { trip ->
                FeedItem.Trip(
                    id = trip.id,
                    title = "${trip.addressFrom} → ${trip.addressTo}",
                    addressFrom = trip.addressFrom,
                    addressTo = trip.addressTo,
                    totalTime = trip.bestRouteTime ?: trip.userSelectedTime,
                    totalPois = trip.totalPois
                )
            }
            _uiState.update { it.copy(items = uiTrips) }
        }
    }

    private fun loadSpots() {
        // TODO: UseCase из Domain
        val mockSpots = listOf(
            FeedItem.Spot("1", "", "Памятник Чехову", "1"),
            FeedItem.Spot("2", "", "Памятник Чехову2", "2"),
            FeedItem.Spot("3", "", "Памятник Чехову3", "3")
        )
        _uiState.update { it.copy(items = mockSpots) }
    }
}