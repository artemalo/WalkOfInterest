package sfedu.ictis.walkOfInterest.presentation.generate

import sfedu.ictis.walkOfInterest.domain.model.DomainCategory

sealed class GenerateEvent {
    object ExpandBottomSheet : GenerateEvent()
    object OpenTimePicker : GenerateEvent()
    data class ShowError(val message: String) : GenerateEvent()
    class NavigateToCategories(val categories: List<DomainCategory>) : GenerateEvent()
}