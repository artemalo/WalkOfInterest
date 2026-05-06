package sfedu.ictis.walkOfInterest.presentation.poi.add

import sfedu.ictis.walkOfInterest.domain.model.DomainPoiNearby
import sfedu.ictis.walkOfInterest.domain.model.DomainPoint

data class AddPoiPickUiState(
    val isChecking: Boolean = false,
    val similarPois: List<DomainPoiNearby> = emptyList(),
    val showSimilarSheet: Boolean = false,
    val lastCheckedPoint: DomainPoint? = null
)
