package sfedu.ictis.walkOfInterest.presentation.categories

sealed class CategoriesEvent {
    object NavigateToRoutes : CategoriesEvent()
    data class ShowError(val message: String) : CategoriesEvent()
}