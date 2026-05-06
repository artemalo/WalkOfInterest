package sfedu.ictis.walkOfInterest.presentation.poi.add.form

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
import sfedu.ictis.walkOfInterest.domain.exception.PoiAlreadyExistsException
import sfedu.ictis.walkOfInterest.domain.exception.RateLimitException
import sfedu.ictis.walkOfInterest.domain.exception.ServerException
import sfedu.ictis.walkOfInterest.domain.model.DomainPickSubcategory
import sfedu.ictis.walkOfInterest.domain.model.DomainPoiInfo
import sfedu.ictis.walkOfInterest.domain.model.DomainPoint
import sfedu.ictis.walkOfInterest.domain.usecase.CreatePoiUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.GetAllCategoriesUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.GetPoiByIdUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.SupplementPoiUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.UpdatePoiUseCase

class AddPoiFormViewModel(
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val createPoiUseCase: CreatePoiUseCase,
    @Suppress("unused") private val updatePoiUseCase: UpdatePoiUseCase,
    private val supplementPoiUseCase: SupplementPoiUseCase,
    private val getPoiByIdUseCase: GetPoiByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddPoiFormUiState())
    val uiState: StateFlow<AddPoiFormUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AddPoiFormEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    private var submitJob: Job? = null
    private var prefillJob: Job? = null

    private companion object {
        private const val TAG = "AddPoiFormViewModel"
    }

    fun init(point: DomainPoint, targetPoiId: Long?) {
        if (_uiState.value.point != null) return

        _uiState.update { it.copy(point = point, targetPoiId = targetPoiId) }
        loadCategories()
        if (targetPoiId != null) loadExistingPoi(targetPoiId)
    }

    fun onNameChanged(value: String) {
        _uiState.update { it.copy(name = value) }
    }

    fun onDescriptionChanged(value: String) {
        _uiState.update { it.copy(description = value) }
    }

    fun onLangSelected(lang: FormLang) {
        _uiState.update { it.copy(lang = lang) }
    }

    fun onSubcategoryToggled(sub: DomainPickSubcategory) {
        _uiState.update { state ->
            val newIds = state.selectedSubcategoryIds.toMutableSet().apply {
                if (contains(sub.id)) remove(sub.id) else add(sub.id)
            }
            val newList = if (newIds.contains(sub.id)) {
                state.selectedSubcategories + sub
            } else {
                state.selectedSubcategories.filter { it.id != sub.id }
            }
            state.copy(
                selectedSubcategoryIds = newIds,
                selectedSubcategories = newList.sortedByCategory()
            )
        }
    }

    fun onSubcategoryRemoved(subId: Int) {
        _uiState.update { state ->
            state.copy(
                selectedSubcategoryIds = state.selectedSubcategoryIds - subId,
                selectedSubcategories = state.selectedSubcategories.filter { it.id != subId }
            )
        }
    }

    fun retryLoadCategories() {
        loadCategories()
    }

    fun onSubmitClicked() {
        val state = _uiState.value
        if (state.isSubmitting) return

        if (!state.isFormValid) {
            viewModelScope.launch {
                _events.emit(
                    AddPoiFormEvent.ShowError(
                        when {
                            state.name.isBlank() -> "Введите название места"
                            state.selectedSubcategoryIds.isEmpty() -> "Выберите хотя бы одну подкатегорию"
                            else -> "Заполните форму корректно"
                        }
                    )
                )
            }
            return
        }

        val point = state.point ?: return
        val name = state.name.trim()
        val description = state.description.trim().takeIf { it.isNotEmpty() }
        val lang = if (state.lang == FormLang.EN) "en" else "ru"
        val subIds = state.selectedSubcategoryIds.toList()
        val targetId = state.targetPoiId

        submitJob?.cancel()
        submitJob = viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorBanner = null) }

            val result = if (targetId != null) {
                supplementPoiUseCase(
                    id = targetId,
                    name = name,
                    description = description,
                    lang = lang,
                    subcategoryIds = subIds
                )
            } else {
                createPoiUseCase(
                    point = point,
                    name = name,
                    description = description,
                    lang = lang,
                    subcategoryIds = subIds,
                    force = false
                )
            }

            result.onSuccess {
                _uiState.update { it.copy(isSubmitting = false) }
                _events.emit(AddPoiFormEvent.SubmittedSuccessfully)
            }.onFailure { error ->
                if (error is kotlinx.coroutines.CancellationException) return@onFailure
                handleFailure(error)
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingCategories = true, errorBanner = null) }

            getAllCategoriesUseCase().onSuccess { list ->
                _uiState.update { it.copy(isLoadingCategories = false, categories = list) }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoadingCategories = false,
                        errorBanner = "Не удалось загрузить категории"
                    )
                }
                _events.emit(
                    AddPoiFormEvent.ShowError(
                        when (error) {
                            is ServerException -> error.message ?: "Ошибка сервера"
                            else -> "Не удалось загрузить категории"
                        }
                    )
                )
            }
        }
    }

    private fun loadExistingPoi(targetPoiId: Long) {
        prefillJob?.cancel()
        prefillJob = viewModelScope.launch {
            getPoiByIdUseCase(targetPoiId).onSuccess { poi ->
                Log.d(
                    TAG,
                    "loadExistingPoi(id=$targetPoiId) -> tags=${poi.tags.size}: " +
                            poi.tags.joinToString { "${it.subcategoryId}:${it.subcategoryName}" }
                )
                applyPrefill(poi)
            }.onFailure { error ->
                if (error is kotlinx.coroutines.CancellationException) return@onFailure
                Log.e(TAG, "loadExistingPoi failed", error)
                _events.emit(
                    AddPoiFormEvent.ShowError(
                        when (error) {
                            is ServerException -> error.message ?: "Не удалось загрузить место"
                            else -> "Не удалось загрузить данные о месте"
                        }
                    )
                )
            }
        }
    }

    private suspend fun applyPrefill(poi: DomainPoiInfo) {
        val prefillSubs = poi.tags
            .map {
                DomainPickSubcategory(
                    id = it.subcategoryId,
                    name = it.subcategoryName,
                    categoryId = it.categoryId
                )
            }
            .distinctBy { it.id }
        Log.d(TAG, "applyPrefill: prefillSubs=${prefillSubs.size} -> $prefillSubs")

        _uiState.update { state ->
            val mergedIds = state.selectedSubcategoryIds + prefillSubs.map { it.id }
            val mergedSubs = (state.selectedSubcategories + prefillSubs)
                .distinctBy { it.id }
                .sortedByCategory()

            state.copy(
                name = state.name.ifBlank { poi.name.orEmpty() },
                description = state.description.ifBlank { poi.description.orEmpty() },
                selectedSubcategoryIds = mergedIds,
                selectedSubcategories = mergedSubs
            )
        }

        _events.emit(AddPoiFormEvent.PrefillApplied(
            name = poi.name.orEmpty(),
            description = poi.description.orEmpty()
        ))
    }

    private suspend fun handleFailure(error: Throwable) {
        _uiState.update { it.copy(isSubmitting = false) }

        when (error) {
            is RateLimitException -> {
                val retryAfter = error.retryAfterSeconds
                val message = buildString {
                    append("Вы добавили слишком много мест сегодня")
                    if (retryAfter != null) {
                        append(". Попробуйте через ")
                        append(formatRetry(retryAfter))
                    }
                }
                _events.emit(AddPoiFormEvent.ShowRateLimit(message))
            }
            is PoiAlreadyExistsException -> {
                _events.emit(
                    AddPoiFormEvent.ShowError(
                        error.message ?: "Похожее место уже существует рядом"
                    )
                )
            }
            is ServerException -> {
                _events.emit(AddPoiFormEvent.ShowError(error.message ?: "Ошибка сервера"))
            }
            is java.net.SocketTimeoutException ->
                _events.emit(AddPoiFormEvent.ShowError("Превышено время ожидания. Проверьте сеть"))
            is java.net.UnknownHostException ->
                _events.emit(AddPoiFormEvent.ShowError("Нет соединения с интернетом"))
            is java.net.ConnectException ->
                _events.emit(AddPoiFormEvent.ShowError("Не удалось подключиться к серверу"))
            else ->
                _events.emit(AddPoiFormEvent.ShowError("Не удалось отправить место"))
        }
    }

    private fun formatRetry(seconds: Long): String {
        val minutes = seconds / 60
        return when {
            seconds < 60 -> "${seconds} сек"
            minutes < 60 -> "${minutes} мин"
            else -> "${minutes / 60} ч"
        }
    }
}

private fun List<DomainPickSubcategory>.sortedByCategory(): List<DomainPickSubcategory> =
    sortedWith(
        compareBy(
            { it.categoryId ?: Int.MAX_VALUE },
            { it.id }
        )
    )

