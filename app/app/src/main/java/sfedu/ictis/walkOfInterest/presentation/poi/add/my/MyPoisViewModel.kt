package sfedu.ictis.walkOfInterest.presentation.poi.add.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sfedu.ictis.walkOfInterest.domain.exception.ServerException
import sfedu.ictis.walkOfInterest.domain.model.PoiStatus
import sfedu.ictis.walkOfInterest.domain.usecase.GetMyPoisUseCase

class MyPoisViewModel(
    private val getMyPoisUseCase: GetMyPoisUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(MyPoisUiState())
    val uiState: StateFlow<MyPoisUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun onTabSelected(status: PoiStatus) {
        if (_uiState.value.selectedTab == status) return
        _uiState.update { it.copy(selectedTab = status) }
    }

    fun refresh() = load()

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            getMyPoisUseCase().onSuccess { list ->
                _uiState.update {
                    it.copy(isLoading = false, all = list)
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = when (error) {
                            is ServerException -> error.message ?: "Ошибка сервера"
                            is java.net.UnknownHostException -> "Нет соединения с интернетом"
                            else -> "Не удалось загрузить ваши места"
                        }
                    )
                }
            }
        }
    }
}
