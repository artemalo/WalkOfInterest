package sfedu.ictis.walkOfInterest.presentation.routes

sealed class RoutesEvent {
    data class ShowError(val message: String) : RoutesEvent()
    object CollapseBottomSheet : RoutesEvent()
}