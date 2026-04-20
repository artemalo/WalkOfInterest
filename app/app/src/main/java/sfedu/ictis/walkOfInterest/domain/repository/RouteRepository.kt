package sfedu.ictis.walkOfInterest.domain.repository

import sfedu.ictis.walkOfInterest.domain.model.DomainCategory
import sfedu.ictis.walkOfInterest.domain.model.DomainPoint
import sfedu.ictis.walkOfInterest.domain.model.DomainRoute
import sfedu.ictis.walkOfInterest.domain.model.DomainTrip
import sfedu.ictis.walkOfInterest.domain.model.RouteFromToResult

interface RouteRepository {
    suspend fun getRoute(from: DomainPoint, to: DomainPoint): Result<RouteFromToResult>
    suspend fun searchWalk(from: DomainPoint, to: DomainPoint, time: Int, lang: String = "ru"): Result<List<DomainCategory>>

    fun saveCurrentTrip(trip: DomainTrip)
    fun getCurrentTrip(): DomainTrip?

    suspend fun getRoutes(points: List<DomainPoint>): Result<List<DomainRoute>>
}