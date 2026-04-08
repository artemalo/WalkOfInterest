package sfedu.ictis.walkOfInterest.domain.repository

import sfedu.ictis.walkOfInterest.domain.model.DomainPoint
import sfedu.ictis.walkOfInterest.domain.model.RouteResult

interface RouteRepository {
    suspend fun getRoute(from: DomainPoint, to: DomainPoint): Result<RouteResult>
    suspend fun searchWalk(from: DomainPoint, to: DomainPoint, time: Int, lang: String = "ru"): Result<Boolean>
}