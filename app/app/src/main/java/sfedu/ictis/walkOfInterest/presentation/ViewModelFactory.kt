package sfedu.ictis.walkOfInterest.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import sfedu.ictis.walkOfInterest.domain.repository.RouteRepository
import sfedu.ictis.walkOfInterest.presentation.categories.CategoriesViewModel
import sfedu.ictis.walkOfInterest.presentation.routes.RoutesViewModel

class ViewModelFactory(private val repository: RouteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(CategoriesViewModel::class.java) ->
                CategoriesViewModel(repository) as T
            modelClass.isAssignableFrom(RoutesViewModel::class.java) ->
                RoutesViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}