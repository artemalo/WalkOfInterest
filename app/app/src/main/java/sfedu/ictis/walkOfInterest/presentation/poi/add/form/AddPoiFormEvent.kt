package sfedu.ictis.walkOfInterest.presentation.poi.add.form

sealed class AddPoiFormEvent {
    data class ShowError(val message: String) : AddPoiFormEvent()
    data class ShowRateLimit(val message: String) : AddPoiFormEvent()
    object SubmittedSuccessfully : AddPoiFormEvent()

    data class PrefillApplied(
        val name: String,
        val description: String
    ) : AddPoiFormEvent()
}
