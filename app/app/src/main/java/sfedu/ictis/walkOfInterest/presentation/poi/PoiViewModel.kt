package sfedu.ictis.walkOfInterest.presentation.poi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sfedu.ictis.walkOfInterest.domain.model.DomainPoiInfo
import sfedu.ictis.walkOfInterest.domain.model.DomainReview
import sfedu.ictis.walkOfInterest.domain.model.PoiStatus
import sfedu.ictis.walkOfInterest.domain.model.ReactionType
import sfedu.ictis.walkOfInterest.domain.model.DomainSavedPoi
import sfedu.ictis.walkOfInterest.domain.repository.ReviewReactionState
import sfedu.ictis.walkOfInterest.domain.usecase.GetMyProfileUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.GetPoiByIdUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.GetPoiReviewsUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.IsPoiSavedUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.SetReviewReactionUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.ToggleSavedPoiUseCase

class PoiViewModel(
    private val getPoiByIdUseCase: GetPoiByIdUseCase,
    private val getPoiReviewsUseCase: GetPoiReviewsUseCase,
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val setReviewReactionUseCase: SetReviewReactionUseCase,
    private val isPoiSavedUseCase: IsPoiSavedUseCase,
    private val toggleSavedPoiUseCase: ToggleSavedPoiUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(PoiUiState())
    val uiState: StateFlow<PoiUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<PoiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<PoiEvent> = _events.asSharedFlow()

    private var loadedId: Long? = null

    private val pendingReactions = mutableSetOf<String>()

    init {
        loadCurrentUsername()
    }

    fun load(poiId: Long) {
        if (loadedId == poiId && _uiState.value.poi != null) return
        loadedId = poiId

        _uiState.update {
            it.copy(isPoiLoading = true, isReviewsLoading = true)
        }

        viewModelScope.launch {
            val poiDeferred = async { getPoiByIdUseCase(poiId) }
            val reviewsDeferred = async { getPoiReviewsUseCase(poiId) }
            val savedDeferred = async { isPoiSavedUseCase(poiId) }

            poiDeferred.await()
                .onSuccess { poi ->
                    _uiState.update { it.copy(poi = poi, isPoiLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isPoiLoading = false) }
                    _events.emit(PoiEvent.ShowError(e.message ?: "Не удалось загрузить точку"))
                }

            reviewsDeferred.await()
                .onSuccess { reviews ->
                    _uiState.update { it.copy(reviews = reviews, isReviewsLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isReviewsLoading = false) }
                    _events.emit(PoiEvent.ShowError(e.message ?: "Не удалось загрузить отзывы"))
                }

            _uiState.update { it.copy(isSaved = savedDeferred.await()) }
        }
    }

    fun preload(name: String?, rating: Double?, count: Int?) {
        val current = _uiState.value.poi
        if (current != null) return

        _uiState.update {
            it.copy(
                poi = DomainPoiInfo(
                    id = loadedId ?: -1L,
                    point = null,
                    name = name,
                    description = null,
                    tags = emptyList(),
                    status = PoiStatus.APPROVED,
                    rating = rating ?: 0.0,
                    countRate = count ?: 0
                )
            )
        }
    }

    fun toggleSaved() {
        val poi = _uiState.value.poi ?: return
        val poiId = loadedId ?: return

        val address = poi.point?.let { "%.6f, %.6f".format(it.lat, it.lon) } ?: ""
        val domainSavedPoi = DomainSavedPoi(
            poiId = poiId,
            name = poi.name.orEmpty(),
            address = address,
            savedAt = System.currentTimeMillis(),
            photo = poi.photoUrl
        )

        viewModelScope.launch {
            val nowSaved = toggleSavedPoiUseCase(domainSavedPoi)
            _uiState.update { it.copy(isSaved = nowSaved) }
        }
    }

    fun toggleSortOrder() {
        _uiState.update { it.copy(sortOrder = it.sortOrder.toggle()) }
    }

    fun onAddOrEditReviewClicked() {
        val poiId = loadedId ?: return
        val state = _uiState.value
        viewModelScope.launch {
            _events.emit(
                PoiEvent.OpenReviewMake(
                    poiId = poiId,
                    poiName = state.poi?.name,
                    poiAddress = state.poi?.point?.let { "%.6f, %.6f".format(it.lat, it.lon) },
                    existingReview = state.myReview
                )
            )
        }
    }

    fun onLikeClicked(review: DomainReview) {
        applyReaction(review, ReactionType.LIKE)
    }

    fun onDislikeClicked(review: DomainReview) {
        applyReaction(review, ReactionType.DISLIKE)
    }

    private fun applyReaction(review: DomainReview, requested: ReactionType) {
        val reviewId = review.id.toLongOrNull() ?: return
        if (reviewId <= 0) return

        if (pendingReactions.contains(review.id)) return
        pendingReactions.add(review.id)

        val original = _uiState.value.reviews.firstOrNull { it.id == review.id } ?: return
        val optimistic = original.applyReactionLocally(requested)
        _uiState.update { state ->
            state.copy(reviews = state.reviews.map { if (it.id == review.id) optimistic else it })
        }

        viewModelScope.launch {
            val result = setReviewReactionUseCase(reviewId = reviewId, type = requested)

            result
                .onSuccess { server ->
                    _uiState.update { state ->
                        state.copy(
                            reviews = state.reviews.map { r ->
                                if (r.id == review.id) r.applyServerState(server) else r
                            }
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { state ->
                        state.copy(
                            reviews = state.reviews.map { r ->
                                if (r.id == review.id) original else r
                            }
                        )
                    }
                    _events.emit(PoiEvent.ShowError(e.message ?: "Не удалось обновить реакцию"))
                }

            pendingReactions.remove(review.id)
        }
    }

    fun refreshAfterReviewSaved() {
        val poiId = loadedId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isReviewsLoading = true) }

            val poiDeferred = async { getPoiByIdUseCase(poiId) }
            val reviewsDeferred = async { getPoiReviewsUseCase(poiId) }

            poiDeferred.await().onSuccess { poi ->
                _uiState.update { it.copy(poi = poi) }
            }

            reviewsDeferred.await()
                .onSuccess { reviews ->
                    _uiState.update { it.copy(reviews = reviews, isReviewsLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isReviewsLoading = false) }
                    _events.emit(PoiEvent.ShowError(e.message ?: "Не удалось обновить отзывы"))
                }
        }
    }

    private fun loadCurrentUsername() {
        viewModelScope.launch {
            getMyProfileUseCase()
                .onSuccess { profile ->
                    _uiState.update { it.copy(currentUsername = profile.username) }
                }
        }
    }
}

private fun DomainReview.applyReactionLocally(requested: ReactionType): DomainReview {
    val current = this.myReaction
    return when {
        current == requested -> when (requested) {
            ReactionType.LIKE -> copy(
                likes = (likes - 1).coerceAtLeast(0),
                myReaction = null
            )
            ReactionType.DISLIKE -> copy(
                dislikes = (dislikes - 1).coerceAtLeast(0),
                myReaction = null
            )
        }
        current != null -> when (requested) {
            ReactionType.LIKE -> copy(
                likes = likes + 1,
                dislikes = (dislikes - 1).coerceAtLeast(0),
                myReaction = ReactionType.LIKE
            )
            ReactionType.DISLIKE -> copy(
                likes = (likes - 1).coerceAtLeast(0),
                dislikes = dislikes + 1,
                myReaction = ReactionType.DISLIKE
            )
        }
        else -> when (requested) {
            ReactionType.LIKE -> copy(likes = likes + 1, myReaction = ReactionType.LIKE)
            ReactionType.DISLIKE -> copy(dislikes = dislikes + 1, myReaction = ReactionType.DISLIKE)
        }
    }
}

private fun DomainReview.applyServerState(state: ReviewReactionState): DomainReview =
    copy(likes = state.likes, dislikes = state.dislikes, myReaction = state.myReaction)