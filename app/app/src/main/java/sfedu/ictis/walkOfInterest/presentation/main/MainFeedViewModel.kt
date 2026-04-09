package sfedu.ictis.walkOfInterest.presentation.main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainFeedViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MainFeedUiState())
    val uiState: StateFlow<MainFeedUiState> = _uiState.asStateFlow()

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
        // TODO: UseCase из Domain
        val mockTrips = listOf(FeedItem.Trip("1", "Прогулка по набережной"))
        _uiState.update { it.copy(items = mockTrips) }
    }

    private fun loadSpots() {
        // TODO: UseCase из Domain
        val mockSpots = listOf(FeedItem.Spot("1", "Памятник Чехову"))
        _uiState.update { it.copy(items = mockSpots) }
    }
}