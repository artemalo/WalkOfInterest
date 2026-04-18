package sfedu.ictis.walkOfInterest.data.repository

import sfedu.ictis.walkOfInterest.data.api.RouteApi
import sfedu.ictis.walkOfInterest.data.model.dto.PointDto
import sfedu.ictis.walkOfInterest.data.model.RouteRequest
import sfedu.ictis.walkOfInterest.data.model.SearchRequest
import sfedu.ictis.walkOfInterest.data.model.dto.CategoryDto
import sfedu.ictis.walkOfInterest.data.model.dto.PoiDto
import sfedu.ictis.walkOfInterest.data.model.dto.SubCategoryDto
import sfedu.ictis.walkOfInterest.domain.model.DomainCategory
import sfedu.ictis.walkOfInterest.domain.model.DomainPoi
import sfedu.ictis.walkOfInterest.domain.model.DomainPoint
import sfedu.ictis.walkOfInterest.domain.model.DomainSubCategory
import sfedu.ictis.walkOfInterest.domain.model.DomainTrip
import sfedu.ictis.walkOfInterest.domain.model.RouteResult
import sfedu.ictis.walkOfInterest.domain.repository.RouteRepository
import java.util.UUID

class RouteRepositoryImpl(private val api: RouteApi) : RouteRepository {
    private var currentTrip: DomainTrip? = null

    override suspend fun getRoute(from: DomainPoint, to: DomainPoint): Result<RouteResult> {
        return try {
            val request = RouteRequest(from.toDto(), to.toDto())
            val response = api.getRoute(request)
            val body = response.body()

            if (response.isSuccessful && body != null) {
                Result.success(RouteResult(
                    minTime = body.minTime,
                    distance = body.distance,
                    points = body.route.map { it.toDomain() }
                ))
            } else Result.failure(Exception("Ошибка сервера: ${response.code()}"))
        } catch (e: Exception) { Result.failure(e) }
    }

    override suspend fun searchWalk(from: DomainPoint, to: DomainPoint, time: Int, lang: String): Result<List<DomainCategory>> {
        return try {
            val dto = SearchRequest(
                p1 = PointDto(from.lat, from.lon),
                p2 = PointDto(to.lat, to.lon),
                time = time,
                lang = lang,
                requestId = UUID.randomUUID().toString()
            )

            val response = api.searchRoute(dto)

            if (response.isSuccessful && response.body() != null) {
                val domainCategories = response.body()?.categories?.map { dto ->
                    dto.toDomain().copy(isSelect = dto.selected > 0 && dto.time > 0)
                } ?: emptyList()

                Result.success(domainCategories)
            } else {
                Result.failure(Exception("Ошибка сервера: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun saveCurrentTrip(trip: DomainTrip) {
        currentTrip = trip
    }

    override fun getCurrentTrip(): DomainTrip? = currentTrip

    private fun DomainPoint.toDto() = PointDto(lat, lon)
    private fun PointDto.toDomain() = DomainPoint(lat, lon)

    private fun CategoryDto.toDomain() = DomainCategory(
        id = id, name = name, description = description, icon = icon,
        selected = selected, totalPois = totalPois, time = time,
        subcategories = subcategories?.map { it.toDomain() } ?: emptyList()
    )
    private fun SubCategoryDto.toDomain() = DomainSubCategory(
        id = id, name = name, description = description, icon = icon,
        pois = pois?.map { it.toDomain() } ?: emptyList()
    )
    private fun PoiDto.toDomain() = DomainPoi(
        id = id, name = name, description = description, lang = lang,
        lat = lat, lon = lon, selected = selected, rate = rate, count = count
    )
}