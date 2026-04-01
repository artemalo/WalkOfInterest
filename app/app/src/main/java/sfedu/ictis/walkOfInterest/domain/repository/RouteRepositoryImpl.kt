package sfedu.ictis.walkOfInterest.domain.repository

import sfedu.ictis.walkOfInterest.data.api.RouteApi
import sfedu.ictis.walkOfInterest.data.model.Coordinates
import sfedu.ictis.walkOfInterest.data.model.MinTimeResponse
import sfedu.ictis.walkOfInterest.data.model.PointDto
import sfedu.ictis.walkOfInterest.data.model.SearchRequestDto
import sfedu.ictis.walkOfInterest.data.repository.RouteRepository
import java.util.UUID

class RouteRepositoryImpl(private val api: RouteApi) : RouteRepository {
    override suspend fun getMinTime(from: Coordinates, to: Coordinates): Result<MinTimeResponse> {
        return try {
            val response = api.getMinTime(from.lat, from.lon, to.lat, to.lon)
            if (response.isSuccessful && response.body() != null) {
                // Маппим DTO в Domain модель
                Result.success(MinTimeResponse(response.body()!!.minMinutes))
            } else {
                Result.failure(Exception("Ошибка бэкенда"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchRoute(from: Coordinates, to: Coordinates, timeMinutes: Int): Result<Boolean> {
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