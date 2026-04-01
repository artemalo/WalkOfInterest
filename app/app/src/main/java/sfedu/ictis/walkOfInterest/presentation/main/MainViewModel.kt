package sfedu.ictis.walkOfInterest.presentation.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import sfedu.ictis.walkOfInterest.data.model.Coordinates
import sfedu.ictis.walkOfInterest.data.repository.RouteRepository
import java.io.IOException

class MainViewModel(private val repository: RouteRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private var minTimeJob: Job? = null

    // Вызывается при выборе точки
    fun onPointSelected(isFrom: Boolean, lat: Double, lon: Double, address: String) {
        _uiState.update { state ->
            if (isFrom) state.copy(pointFrom = Coordinates(lat, lon), addressFrom = address)
            else state.copy(pointTo = Coordinates(lat, lon), addressTo = address)
        }
        checkAndFetchMinTime()
        Log.i("MainViewModel","onPointSelected(): ${lat},${lon}")
    }

    private fun checkAndFetchMinTime() {
        val from = _uiState.value.pointFrom
        val to = _uiState.value.pointTo

        if (from != null && to != null) {
            // Отменяем предыдущий запрос, если пользователь быстро сменил точку
            Log.i("MainViewModel","checkAndFetchMinTime(): ${from},${to}")
            minTimeJob?.cancel()
            minTimeJob = viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true) }

                repository.getMinTime(from, to).onSuccess { response ->
                    _uiState.update { it.copy(
                        minTimeMinutes = response.minMinutes,
                        selectedTimeMinutes = response.minMinutes, // По умолчанию ставим минимум
                        isTimePickerEnabled = true,
                        isLoading = false
                    )
                    }
                    validateCalculateButton()
                    Log.i("MainViewModel","checkAndFetchMinTime: repository.getMinTime - OK")
                }.onFailure { error ->
                    val message = when (error) {
                        is IOException -> "Проблема с сетью"
                        is HttpException -> "Ошибка сервера"
                        else -> "Неизвестная ошибка"
                    }

                    _uiState.update {
                        it.copy(
                            isLoading = false
//                            errorMessage = message
                        )
                    }
                    Log.e("MainViewModel","checkAndFetchMinTime: $message")
                }
            }
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

    fun onCalculateClicked() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val state = _uiState.value
            // Отправляем POST /search
            repository.searchRoute(state.pointFrom!!, state.pointTo!!, state.selectedTimeMinutes)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    // Здесь будет навигация к категориям
                }
        }
    }
}