package sfedu.ictis.walkOfInterest.presentation.generate

sealed class GenerateEvent {
    object OpenTimePicker : GenerateEvent()
    data class ShowError(val message: String) : GenerateEvent()
}