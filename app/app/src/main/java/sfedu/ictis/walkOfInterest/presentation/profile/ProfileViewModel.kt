package sfedu.ictis.walkOfInterest.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sfedu.ictis.walkOfInterest.domain.model.DomainUserProfile
import sfedu.ictis.walkOfInterest.domain.repository.UserRepository
import sfedu.ictis.walkOfInterest.domain.usecase.GetMyProfileUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.GetProfileByUsernameUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.GetUserReviewsUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.LogoutAllUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.LogoutUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.UpdateNicknameUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.UpdateProfileInfoUseCase

class ProfileViewModel(
    private val getMyProfileUseCase: GetMyProfileUseCase,
    private val getProfileByUsernameUseCase: GetProfileByUsernameUseCase,
    private val getUserReviewsUseCase: GetUserReviewsUseCase,
    private val updateNicknameUseCase: UpdateNicknameUseCase,
    private val updateProfileInfoUseCase: UpdateProfileInfoUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val logoutAllUseCase: LogoutAllUseCase,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ProfileEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<ProfileEvent> = _events.asSharedFlow()

    private var initialized = false

    fun initWith(username: String?) {
        if (initialized) return
        initialized = true

        if (username == null) {
            _uiState.update { it.copy(isOwnProfile = true) }
            observeProfileChanges()
            loadOwnProfile()
        } else {
            _uiState.update { it.copy(isOwnProfile = false) }
            loadProfileByUsername(username)
        }
    }

    private fun observeProfileChanges() {
        viewModelScope.launch {
            userRepository.observeProfile().collect { profile ->
                if (profile != null) {
                    _uiState.update { it.copy(profile = profile) }
                }
            }
        }
    }

    private fun loadOwnProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val profileResult = getMyProfileUseCase()
            profileResult.onSuccess { profile ->
                _uiState.update { it.copy(profile = profile, isLoading = false) }
                loadReviewsFor(profile)
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false) }
                _events.emit(ProfileEvent.EmptyProfile)
                _events.emit(ProfileEvent.ShowError(e.message ?: "Не удалось загрузить профиль"))
            }
        }
    }

    private fun loadProfileByUsername(username: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val profileResult = getProfileByUsernameUseCase(username)
            profileResult.onSuccess { profile ->
                _uiState.update { it.copy(profile = profile, isLoading = false) }
                loadReviewsFor(profile)
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false) }
                _events.emit(ProfileEvent.EmptyProfile)
                _events.emit(ProfileEvent.ShowError(e.message ?: "Не удалось загрузить профиль"))
            }
        }
    }

    private fun loadReviewsFor(profile: DomainUserProfile) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingReviews = true) }

            val reviewsResult = getUserReviewsUseCase(profile.username)
            reviewsResult.onSuccess { reviews ->
                _uiState.update { it.copy(reviews = reviews, isLoadingReviews = false) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoadingReviews = false) }
                _events.emit(ProfileEvent.ShowError(e.message ?: "Не удалось загрузить отзывы"))
            }
        }
    }

    fun toggleSortOrder() {
        _uiState.update { it.copy(sortOrder = it.sortOrder.toggle()) }
    }

    fun openEditScreen() {
        _uiState.update { it.copy(isEditMode = true, nicknameError = null) }
    }

    fun closeEditScreen() {
        _uiState.update { it.copy(isEditMode = false, nicknameError = null) }
        viewModelScope.launch { _events.emit(ProfileEvent.CloseEditScreen) }
    }

    fun applyEditScreen(firstName: String, lastName: String, bio: String) {
        if (_uiState.value.isUpdatingProfile) return

        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingProfile = true) }

            val result = updateProfileInfoUseCase(
                firstName = firstName,
                lastName = lastName,
                bio = bio
            )

            result.onSuccess { updatedProfile ->
                _uiState.update {
                    it.copy(
                        profile = updatedProfile,
                        isUpdatingProfile = false
                    )
                }

                closeEditScreen()
            }.onFailure { e ->
                _uiState.update { it.copy(isUpdatingProfile = false) }
                _events.emit(ProfileEvent.ShowError(e.message ?: "Не удалось обновить профиль"))
            }
        }
    }

    fun updateNickname(newUsername: String) {
        val current = _uiState.value.profile?.username
        if (current != null && current == newUsername.trim()) {
            viewModelScope.launch { _events.emit(ProfileEvent.NicknameUpdated) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingNickname = true, nicknameError = null) }

            val result = updateNicknameUseCase(newUsername)
            result.onSuccess { updated ->
                _uiState.update { it.copy(isUpdatingNickname = false, nicknameError = null) }
                _events.emit(ProfileEvent.NicknameUpdated)

                loadReviewsFor(updated)
            }.onFailure { e ->
                _uiState.update {
                    it.copy(
                        isUpdatingNickname = false,
                        nicknameError = e.message ?: "Не удалось изменить никнейм"
                    )
                }
            }
        }
    }

    fun clearNicknameError() {
        _uiState.update { it.copy(nicknameError = null) }
    }

    fun logout() {
        if (_uiState.value.isLoggingOut) return

        viewModelScope.launch {
            if (_uiState.value.profile == null) {
                _events.emit(ProfileEvent.ShowError("Ошибка"))
                return@launch
            }

            _uiState.update { it.copy(isLoggingOut = true) }

            logoutUseCase().onSuccess {
                _events.emit(ProfileEvent.LoggedOut)
            }.onFailure { e ->
                _uiState.update { it.copy(isLoggingOut = false) }
                _events.emit(ProfileEvent.ShowError(e.message ?: "Не удалось выйти"))
            }
        }
    }

    fun logoutAll() {
        if (_uiState.value.isLoggingOut) return

        viewModelScope.launch {
            if (_uiState.value.profile == null) {
                _events.emit(ProfileEvent.ShowError("Ошибка"))
                return@launch
            }

            _uiState.update { it.copy(isLoggingOut = true) }

            logoutAllUseCase().onSuccess {
                _events.emit(ProfileEvent.LoggedOut)
            }.onFailure { e ->
                _uiState.update { it.copy(isLoggingOut = false) }
                _events.emit(ProfileEvent.ShowError(e.message ?: "Не удалось выйти со всех устройств"))
            }
        }
    }
}
