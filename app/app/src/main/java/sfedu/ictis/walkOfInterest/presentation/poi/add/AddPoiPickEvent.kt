package sfedu.ictis.walkOfInterest.presentation.poi.add

import sfedu.ictis.walkOfInterest.domain.model.DomainPoint

sealed class AddPoiPickEvent {
    data class ShowError(val message: String) : AddPoiPickEvent()

    /** Похожих точек нет - форма создания */
    data class OpenForm(
        val point: DomainPoint,
        val targetPoiId: Long? = null
    ) : AddPoiPickEvent()

    /** Открыть существующий POI */
    data class OpenExistingPoi(val poiId: Long) : AddPoiPickEvent()
}
