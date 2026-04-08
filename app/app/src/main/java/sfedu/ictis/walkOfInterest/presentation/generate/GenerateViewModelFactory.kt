package sfedu.ictis.walkOfInterest.presentation.generate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import sfedu.ictis.walkOfInterest.domain.usecase.CalculateWalkUseCase
import sfedu.ictis.walkOfInterest.domain.usecase.GetBaseRouteUseCase

class GenerateViewModelFactory(private val getBaseRouteUseCase: GetBaseRouteUseCase,
                               private val calculateWalkUseCase: CalculateWalkUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GenerateViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GenerateViewModel(getBaseRouteUseCase, calculateWalkUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}