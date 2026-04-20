package sfedu.ictis.walkOfInterest.domain.repository

import sfedu.ictis.walkOfInterest.domain.model.DomainPoint

interface MapSettingRepository {
    fun getMapCenter(): DomainPoint
    fun setMapCenter(point: DomainPoint)
    fun getDefaultZoom(): Double
}