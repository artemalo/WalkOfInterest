package sfedu.ictis.walkOfInterest.domain.repository

import sfedu.ictis.walkOfInterest.data.api.RouteApi
import sfedu.ictis.walkOfInterest.data.model.PointDto
import sfedu.ictis.walkOfInterest.data.model.RouteRequestDto
import sfedu.ictis.walkOfInterest.data.model.RouteResponseDto
import sfedu.ictis.walkOfInterest.data.model.SearchRequestDto
import sfedu.ictis.walkOfInterest.data.repository.RouteRepository
import java.util.UUID

class RouteRepositoryImpl(private val api: RouteApi) : RouteRepository {
    override suspend fun getRoute(from: PointDto, to: PointDto): Result<RouteResponseDto> {
        return try {
            val dto = RouteRequestDto(
                p1 = PointDto(from.lat, from.lon),
                p2 = PointDto(to.lat, to.lon)
            )
            val response = api.getRoute(dto)
            if (response.isSuccessful && response.body() != null) {
                Result.success(RouteResponseDto(
                    response.body()!!.minTime,
                    response.body()!!.distance,
                    response.body()!!.route
                ))
            } else {
                Result.failure(Exception("Ошибка бэкенда"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchRoute(from: PointDto, to: PointDto, timeMinutes: Int): Result<Boolean> {
        return try {
            val currentRequestId = UUID.randomUUID().toString()

            val currentLang = "ru"

            val dto = SearchRequestDto(
                p1 = PointDto(from.lat, from.lon),
                p2 = PointDto(to.lat, to.lon),
                timeLimitMinutes = timeMinutes,
                lang = currentLang,
                requestId = currentRequestId
            )

            val response = api.searchRoute(dto)

            if (response.isSuccessful) {
                Result.success(true)
            } else {
                Result.failure(Exception("Search failed with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}