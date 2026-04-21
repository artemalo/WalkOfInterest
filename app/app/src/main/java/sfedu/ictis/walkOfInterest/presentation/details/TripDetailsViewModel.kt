package sfedu.ictis.walkOfInterest.presentation.details

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import sfedu.ictis.walkOfInterest.domain.usecase.DeleteTripByIdUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.GetTripByIdUseCase

class TripDetailsViewModel(
    private val getTripByIdUseCase: GetTripByIdUseCase,
    private val deleteTripByIdUseCase: DeleteTripByIdUseCase
) : ViewModel() {
    private val _deleteState = MutableStateFlow<DeleteState>(DeleteState.Idle)
    val deleteState: StateFlow<DeleteState> = _deleteState

    private var currentTripId: String? = null

    fun loadTripDetails(tripId: String) {
        currentTripId = tripId
        viewModelScope.launch {
            val trip = getTripByIdUseCase(tripId)
            if (trip != null) {
                Log.i("TripDetails", "Загружен маршрут: ${trip.addressFrom} -> ${trip.addressTo}")
                Log.i("TripDetails", "Точки (selectedPois):")
                trip.selectedPois.forEach { poi ->
                    Log.i("TripDetails", "- ${poi.name} (Cat: ${poi.nameCat})")
                }
            } else {
                Log.e("TripDetails", "Маршрут с ID $tripId не найден в БД!")
            }
        }
    }

    fun deleteTrip() {
        val id = currentTripId ?: return
        viewModelScope.launch {
            val success = deleteTripByIdUseCase(id)
            _deleteState.value = if (success) DeleteState.Success else DeleteState.Error
        }
    }
}