package sfedu.ictis.walkOfInterest.presentation.poi.add

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
import sfedu.ictis.walkOfInterest.domain.usecase.CheckPoiNearbyUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.GetMapCenterUseCase

class AddPoiPickViewModel(
    private val checkPoiNearbyUseCase: CheckPoiNearbyUseCase,
    private val getMapCenterUseCase: GetMapCenterUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddPoiPickUiState())
    val uiState: StateFlow<AddPoiPickUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AddPoiPickEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    val defaultCenter: DomainPoint get() = getMapCenterUseCase()

    private var checkJob: Job? = null

    fun onConfirmPointClicked(lat: Double, lon: Double) {
        if (_uiState.value.isChecking) return

        val point = DomainPoint(lat, lon)
        checkJob?.cancel()
        checkJob = viewModelScope.launch {
            _uiState.update { it.copy(isChecking = true, lastCheckedPoint = point) }

            checkPoiNearbyUseCase(point).onSuccess { result ->
                _uiState.update { it.copy(isChecking = false) }

                if (result.existsNearby && result.pois.isNotEmpty()) {
                    _uiState.update {
                        it.copy(similarPois = result.pois, showSimilarSheet = true)
                    }
                } else {
                    _events.emit(AddPoiPickEvent.OpenForm(point))
                }
            }.onFailure { error ->
                if (error is kotlinx.coroutines.CancellationException) return@onFailure
                handleFailure(error)
            }
        }
    }

    fun onSimilarSheetDismissed() {
        _uiState.update { it.copy(showSimilarSheet = false) }
    }

    fun onShowSimilarSheetAgain() {
        if (_uiState.value.similarPois.isNotEmpty()) {
            _uiState.update { it.copy(showSimilarSheet = true) }
        }
    }

    fun onAlwaysCreateNew() {
        val point = _uiState.value.lastCheckedPoint ?: return
        _uiState.update { it.copy(showSimilarSheet = false) }
        viewModelScope.launch {
            _events.emit(AddPoiPickEvent.OpenForm(point))
        }
    }

    fun onAddInfoToExisting(poiId: Long) {
        val point = _uiState.value.lastCheckedPoint ?: return
        _uiState.update { it.copy(showSimilarSheet = false) }
        viewModelScope.launch {
            _events.emit(AddPoiPickEvent.OpenForm(point = point, targetPoiId = poiId))
        }
    }

    fun onOpenExistingPoi(poiId: Long) {
        _uiState.update { it.copy(showSimilarSheet = false) }
        viewModelScope.launch {
            _events.emit(AddPoiPickEvent.OpenExistingPoi(poiId))
        }
    }

    private fun handleFailure(error: Throwable) {
        _uiState.update { it.copy(isChecking = false) }

        val message = when (error) {
            is ServerException -> error.message ?: "Ошибка сервера"
            is java.net.SocketTimeoutException -> "Превышено время ожидания. Проверьте сеть"
            is java.net.UnknownHostException -> "Нет соединения с интернетом"
            is java.net.ConnectException -> "Не удалось подключиться к серверу"
            else -> "Не удалось проверить точку"
        }
        viewModelScope.launch {
            _events.emit(AddPoiPickEvent.ShowError(message))
        }
    }
}