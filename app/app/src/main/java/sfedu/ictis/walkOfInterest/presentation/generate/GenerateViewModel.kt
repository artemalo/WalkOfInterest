package sfedu.ictis.walkOfInterest.presentation.generate

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sfedu.ictis.walkOfInterest.domain.exception.ServerException
import sfedu.ictis.walkOfInterest.domain.model.DomainPoint
import sfedu.ictis.walkOfInterest.domain.repository.MapRepository
import sfedu.ictis.walkOfInterest.domain.usecase.CalculateWalkUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.GetBaseRouteUseCase
import sfedu.ictis.walkOfInterest.utils.formatMinutes

class GenerateViewModel(
    private val repositoryMap: MapRepository,
    private val getBaseRouteUseCase: GetBaseRouteUseCase,
    private val calculateWalkUseCase: CalculateWalkUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(GenerateUiState())
    val uiState: StateFlow<GenerateUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<GenerateEvent>(
        extraBufferCapacity = 1
    )
    val events = _events.asSharedFlow()

    val defaultCenter: DomainPoint get() = repositoryMap.getMapCenter()

    private var routeJob: Job? = null
    private var calculationJob: Job? = null

    private var selectingFrom: Boolean? = null

    fun onSelectFromClicked() {
        if (_uiState.value.isLoading) return
        selectingFrom = true
    }

    fun onSelectToClicked() {
        if (_uiState.value.isLoading) return
        selectingFrom = false
    }

    fun onMapPointClicked(lat: Double, lon: Double) {
        val isFrom = selectingFrom ?: return

        val address = "${lat.toString().take(6)}, ${lon.toString().take(6)}"
        val point = DomainPoint(lat, lon)

        _uiState.update { state ->
            if (isFrom) {
                state.copy(pointFrom = point, addressFrom = address)
            } else {
                state.copy(pointTo = point, addressTo = address)
            }
        }

        selectingFrom = null

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
                )}

                validateCalculateButton()
            }.onFailure {
                if (it !is kotlinx.coroutines.CancellationException) {
                    handleFailure("getRoute", it)
                }
            }
        }
    }

    fun onCalculateClicked() {
        val state = _uiState.value
        if (state.isLoading) return

        if (!state.isCalculateEnabled) {
            viewModelScope.launch {
                _events.emit(
                    GenerateEvent.ShowError(
                        if (state.pointFrom == null || state.pointTo == null)
                            "Выберите обе точки на карте"
                        else
                            "Неизвестная ошибка. Попробуйте выбрать другие точки"
                    )
                )
            }
            return
        }

        val from = state.pointFrom ?: return
        val to = state.pointTo ?: return
        val time = state.selectedTimeMinutes

        calculationJob?.cancel()

        calculationJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            calculateWalkUseCase(from, to, time).onSuccess { categories ->
                _uiState.update { it.copy(isLoading = false) }
                _events.emit(GenerateEvent.NavigateToCategories(categories))
            }.onFailure {
                if (it !is kotlinx.coroutines.CancellationException) {
                    handleFailure("onCalculate", it)
                }
            }
        }
    }

    fun onClockClicked() {
        val state = _uiState.value
        if (state.isLoading) return

        viewModelScope.launch {
            if (state.isTimePickerEnabled) {
                _events.emit(GenerateEvent.OpenTimePicker)
            } else {
                _events.emit(
                    GenerateEvent.ShowError(
                        if (state.pointFrom == null || state.pointTo == null)
                            "Выберите обе точки на карте"
                        else
                            "Что-то пошло не так"
                    )
                )
            }
        }
    }

    fun onTimeSelected(minutes: Int) {
        val min = _uiState.value.minTimeMinutes ?: 0

        val finalMinutes = if (minutes < min) min else minutes

        if (minutes < min) {
            viewModelScope.launch {
                _events.emit(GenerateEvent.ShowError("Время не может быть меньше минимального маршрута (${formatMinutes(min)})"))
            }
        }

        _uiState.update { it.copy(selectedTimeMinutes = finalMinutes) }
        validateCalculateButton()
    }

    fun onBackClicked() {
        if (_uiState.value.isLoading) {
            cancelAllRequests()
        }
    }

    fun cancelAllRequests() {
        routeJob?.cancel()
        calculationJob?.cancel()

        routeJob = null
        calculationJob = null

        _uiState.update { it.copy(isLoading = false) }

        Log.i("GenerateViewModel", "All requests cancelled by user")
    }

    private fun validateCalculateButton() {
        val state = _uiState.value
        val isEnabled = state.pointFrom != null && state.pointTo != null &&
                state.selectedTimeMinutes >= (state.minTimeMinutes ?: 0)

        _uiState.update { it.copy(isCalculateEnabled = isEnabled) }
    }

    private fun handleFailure(tag: String, error: Throwable) {
        _uiState.update { it.copy(isLoading = false) }

        Log.e("GenerateViewModel", "$tag: ${error.message}")

        // временно
        val userMessage = when (error) {
            is ServerException -> error.message ?: "Ошибка данных"
            is java.net.SocketTimeoutException -> "Превышено время ожидания. Проверьте подключение к сети"
            is java.net.UnknownHostException -> "Нет соединения с интернетом"
            is java.net.ConnectException -> "Не удалось подключиться к серверу"
            is retrofit2.HttpException -> {
                when (error.code()) {
                    404 -> "Маршрут не найден"
                    500 -> "Ошибка на сервере. Попробуйте позже"
                    else -> "Ошибка сервера (${error.code()})"
                }
            }
            else -> "Что-то пошло не так. Попробуйте снова"
        }

        viewModelScope.launch {
            _events.emit(GenerateEvent.ShowError(userMessage))
        }
    }
}