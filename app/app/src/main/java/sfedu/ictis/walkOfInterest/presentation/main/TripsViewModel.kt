package sfedu.ictis.walkOfInterest.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TripsViewModel(
    private val getRoutesUseCase: GetRoutesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TripsUiState())
    val state: StateFlow<TripsUiState> = _state

    fun loadTrips() {
        viewModelScope.launch {
            val routes = getRoutesUseCase()
            _state.value = TripsUiState(routes)
        }
    }
}