package sfedu.ictis.walkOfInterest.presentation.details

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import sfedu.ictis.walkOfInterest.domain.usecase.GetTripByIdUseCase

class TripDetailsViewModel(
    private val getTripByIdUseCase: GetTripByIdUseCase
) : ViewModel() {
    fun loadTripDetails(tripId: String) {
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
}