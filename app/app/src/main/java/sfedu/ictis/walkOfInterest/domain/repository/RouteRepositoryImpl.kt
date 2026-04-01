package sfedu.ictis.walkOfInterest.domain.repository

import sfedu.ictis.walkOfInterest.data.api.RouteApi
import sfedu.ictis.walkOfInterest.data.model.Coordinates
import sfedu.ictis.walkOfInterest.data.model.CoordinatesDto
import sfedu.ictis.walkOfInterest.data.model.MinTimeResponse
import sfedu.ictis.walkOfInterest.data.model.SearchRequestDto
import sfedu.ictis.walkOfInterest.data.repository.RouteRepository

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
            val dto = SearchRequestDto(
                from = CoordinatesDto(from.lat, from.lon),
                to = CoordinatesDto(to.lat, to.lon),
                timeLimit = timeMinutes
            )
            val response = api.searchRoute(dto)
            Result.success(response.isSuccessful)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}