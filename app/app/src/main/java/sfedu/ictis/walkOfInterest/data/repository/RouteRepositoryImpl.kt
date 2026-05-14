package sfedu.ictis.walkOfInterest.data.repository

import org.json.JSONObject
import sfedu.ictis.walkOfInterest.data.api.RouteApi
import sfedu.ictis.walkOfInterest.data.model.ReorderPoiItem
import sfedu.ictis.walkOfInterest.data.model.ReorderRequest
import sfedu.ictis.walkOfInterest.data.model.dto.PointDto
import sfedu.ictis.walkOfInterest.data.model.RouteRequest
import sfedu.ictis.walkOfInterest.data.model.SearchRequest
import sfedu.ictis.walkOfInterest.data.model.dto.CategoryDto
import sfedu.ictis.walkOfInterest.data.model.dto.PoiDto
import sfedu.ictis.walkOfInterest.data.model.dto.RouteDto
import sfedu.ictis.walkOfInterest.data.model.dto.SubCategoryDto
import sfedu.ictis.walkOfInterest.domain.exception.ServerException
import sfedu.ictis.walkOfInterest.domain.model.DomainCategory
import sfedu.ictis.walkOfInterest.domain.model.DomainPoi
import sfedu.ictis.walkOfInterest.domain.model.DomainPoint
import sfedu.ictis.walkOfInterest.domain.model.DomainRoute
import sfedu.ictis.walkOfInterest.domain.model.DomainSubCategory
import sfedu.ictis.walkOfInterest.domain.model.PoiOrder
import sfedu.ictis.walkOfInterest.domain.model.RouteFromToResult
import sfedu.ictis.walkOfInterest.domain.repository.RouteRepository
import java.util.UUID

class RouteRepositoryImpl(private val api: RouteApi) : RouteRepository {
    override suspend fun getRoute(from: DomainPoint, to: DomainPoint): Result<RouteFromToResult> {
        return try {
            val request = RouteRequest(from.toDto(), to.toDto())
            val response = api.getRoute(request)
            val body = response.body()

            if (response.isSuccessful && body != null) {
                Result.success(RouteFromToResult(
                    minTime = body.minTime,
                    distance = body.distance,
                    points = body.route.map { it.toDomain() }
                ))
            } else Result.failure(Exception("Ошибка сервера: ${response.code()}"))
        } catch (e: Exception) { Result.failure(e) }
    }

    override suspend fun searchWalk(from: DomainPoint,to: DomainPoint, time: Int, maxPoi: Int, lang: String): Result<List<DomainCategory>> {
        return runCatching {
            val dto = SearchRequest(
                p1 = PointDto(from.lat, from.lon),
                p2 = PointDto(to.lat, to.lon),
                time = time,
                maxPoi = maxPoi,
                lang = lang,
                requestId = UUID.randomUUID().toString()
            )

            val response = api.searchRoute(dto)

            if (response.isSuccessful && response.body() != null) {
                response.body()?.categories?.map { dto ->
                    dto.toDomain().copy(isSelect = dto.selected > 0 && dto.time > 0)
                } ?: emptyList()
            } else {
                if (response.code() == 400) {
                    val errorMsg = response.errorBody()?.string()?.let {
                        JSONObject(it).getString(JSONObject(it).keys().next())
                    } ?: "Ошибка данных"
                    throw ServerException(errorMsg)
                } else {
                    throw Exception("Ошибка ${response.code()}")
                }
            }
        }
    }

    override suspend fun getRoutes(points: List<DomainPoint>): Result<List<DomainRoute>> {
        return runCatching {
            val request = points.map { PointDto(it.lat, it.lon) }

            val response = api.getRoutes(request)
            val body = response.body()

            if (response.isSuccessful && body != null) {
                body.map { it.toDomain() }
            } else {
                if (response.code() == 400) {
                    val errorMsg = response.errorBody()?.string()?.let {
                        JSONObject(it).getString(JSONObject(it).keys().next())
                    } ?: "Ошибка данных"
                    throw ServerException(errorMsg)
                } else {
                    throw Exception("Ошибка ${response.code()}")
                }
            }
        }
    }

    override suspend fun getTime(points: List<DomainPoint>): Result<Int> {
        return runCatching {
            val request = points.map { PointDto(it.lat, it.lon) }

            val response = api.getTime(request)
            val body = response.body()

            if (response.isSuccessful && body != null) {
                body.toInt()
            } else {
                if (response.code() == 400) {
                    val errorMsg = response.errorBody()?.string()?.let {
                        JSONObject(it).getString(JSONObject(it).keys().next())
                    } ?: "Ошибка данных"
                    throw ServerException(errorMsg)
                } else {
                    throw Exception("Ошибка ${response.code()}")
                }
            }
        }
    }

    override suspend fun reorder(pois: List<PoiOrder>, from: DomainPoint, to: DomainPoint): Result<List<PoiOrder>> {
        return runCatching {
            val request = ReorderRequest(
                p1 = PointDto(from.lat, from.lon),
                p2 = PointDto(to.lat, to.lon),
                pois = pois.map { ReorderPoiItem(it.id, it.lat, it.lon) }
            )
            val response = api.reorder(request)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                body.map { PoiOrder(id = it.id, lat = 0.0, lon = 0.0, order = it.order) }
            } else {
                throw Exception("Ошибка reorder: ${response.code()}")
            }
        }
    }



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
        lat = lat, lon = lon, selected = selected, rate = rate, count = count,
        order = order,
        photo = photo
    )

    private fun RouteDto.toDomain() = DomainRoute(
        minTime = minTime.toInt(),
        distance = distance,
        steps = steps.toInt(),
        points = route.map { it.toDomain() }
    )
}