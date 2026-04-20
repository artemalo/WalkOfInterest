package sfedu.ictis.walkOfInterest.data.repository

import sfedu.ictis.walkOfInterest.domain.model.DomainPoint
import sfedu.ictis.walkOfInterest.domain.repository.MapSettingRepository

class MapSettingRepositoryImpl : MapSettingRepository {
    private var userMapCenter: DomainPoint? = null
    private val hardcodedDefault = DomainPoint(47.207564, 38.938756)


    override fun getMapCenter(): DomainPoint {
        return userMapCenter ?: hardcodedDefault
    }

    override fun setMapCenter(point: DomainPoint) {
        userMapCenter = point
    }

    override fun getDefaultZoom(): Double = 15.0
}