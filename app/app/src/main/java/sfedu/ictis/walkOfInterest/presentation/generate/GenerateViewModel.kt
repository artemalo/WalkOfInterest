package sfedu.ictis.walkOfInterest.presentation.generate

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sfedu.ictis.walkOfInterest.domain.model.DomainPoint
import sfedu.ictis.walkOfInterest.domain.usecase.CalculateWalkUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.GetBaseRouteUseCase

class GenerateViewModel(private val getBaseRouteUseCase: GetBaseRouteUseCase,
                        private val calculateWalkUseCase: CalculateWalkUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(GenerateUiState())
    val uiState: StateFlow<GenerateUiState> = _uiState.asStateFlow()

    private var routeJob: Job? = null

    fun onPointSelected(isFrom: Boolean, lat: Double, lon: Double, address: String) {
        val newPoint = DomainPoint(lat, lon)
        _uiState.update { state ->
            if (isFrom) state.copy(pointFrom = newPoint, addressFrom = address)
            else state.copy(pointTo = newPoint, addressTo = address)
        }
        checkAndFetchRoute()
        Log.i("MainViewModel","onPointSelected(): ${lat},${lon}")
    }

    private fun checkAndFetchRoute() {
        val from = _uiState.value.pointFrom ?: return
        val to = _uiState.value.pointTo ?: return

        Log.i("MainViewModel","checkAndFetchMinTime(): ${from},${to}")
        routeJob?.cancel()
        routeJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            getBaseRouteUseCase(from, to).onSuccess { result ->
                _uiState.update { it.copy(
                    minTimeMinutes = result.minTime,
                    selectedTimeMinutes = result.minTime,
                    route = result.points,
                    isTimePickerEnabled = true,
                    isLoading = false
                )
                }
                validateCalculateButton()
            }.onFailure { handleFailure("getRoute", it) }
        }
    }

    fun onCalculateClicked() {
        val state = _uiState.value
        val from = state.pointFrom ?: return
        val to = state.pointTo ?: return
        val time = state.selectedTimeMinutes

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            calculateWalkUseCase(from, to, time).onSuccess {
                _uiState.update { it.copy(isLoading = false) }
                // TODO: Активити Категории
            }.onFailure { handleFailure("onCalculate", it) }
        }
    }

    fun onTimeSelected(minutes: Int) {
        _uiState.update { it.copy(selectedTimeMinutes = minutes) }
        validateCalculateButton()
    }

    private fun validateCalculateButton() {
        val state = _uiState.value
        val isEnabled = state.pointFrom != null &&
                state.pointTo != null &&
                state.minTimeMinutes != null &&
                state.selectedTimeMinutes >= state.minTimeMinutes
        _uiState.update { it.copy(isCalculateEnabled = isEnabled) }
    }

    private fun handleFailure(tag: String, error: Throwable) {
        _uiState.update { it.copy(isLoading = false) }
        Log.e("MainViewModel", "$tag: ${error.message}")
    }
}