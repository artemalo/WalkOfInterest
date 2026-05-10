package sfedu.ictis.walkOfInterest.domain.repository

import sfedu.ictis.walkOfInterest.domain.model.DomainCategory
import sfedu.ictis.walkOfInterest.domain.model.DomainPoint
import sfedu.ictis.walkOfInterest.domain.model.DomainRoute
import sfedu.ictis.walkOfInterest.domain.model.PoiOrder
import sfedu.ictis.walkOfInterest.domain.model.RouteFromToResult

interface RouteRepository {
    suspend fun getRoute(from: DomainPoint, to: DomainPoint): Result<RouteFromToResult>
    suspend fun searchWalk(from: DomainPoint, to: DomainPoint, time: Int, maxPoi: Int, lang: String = "ru"): Result<List<DomainCategory>>

    suspend fun getRoutes(points: List<DomainPoint>): Result<List<DomainRoute>>
    suspend fun getTime(points: List<DomainPoint>): Result<Int>

    suspend fun reorder(pois: List<PoiOrder>, from: DomainPoint, to: DomainPoint): Result<List<PoiOrder>>
}