package sfedu.ictis.walkOfInterest.presentation.poi.add.my

import sfedu.ictis.walkOfInterest.domain.model.DomainMyPoi
import sfedu.ictis.walkOfInterest.domain.model.PoiStatus

data class MyPoisUiState(
    val isLoading: Boolean = false,
    val all: List<DomainMyPoi> = emptyList(),
    val selectedTab: PoiStatus = PoiStatus.PENDING,
    val errorMessage: String? = null
) {
    val visible: List<DomainMyPoi>
        get() = all.filter { it.status == selectedTab }
}
