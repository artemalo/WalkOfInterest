package sfedu.ictis.walkOfInterest.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import sfedu.ictis.walkOfInterest.data.model.RouteRequest
import sfedu.ictis.walkOfInterest.data.model.RouteResponse
import sfedu.ictis.walkOfInterest.data.model.SearchRequest
import sfedu.ictis.walkOfInterest.data.model.SearchResponse
import sfedu.ictis.walkOfInterest.data.model.dto.PointDto
import sfedu.ictis.walkOfInterest.data.model.dto.RouteDto

interface RouteApi {
    @POST("api/poi/generate/route")
    suspend fun getRoute(
        @Body request: RouteRequest
    ): Response<RouteResponse>

    @POST("api/poi/generate/search")
    suspend fun searchRoute(
        @Body request: SearchRequest
    ): Response<SearchResponse>

    @POST("api/poi/route/routes")
    suspend fun getRoutes(@Body points: List<PointDto>): Response<List<RouteDto>>
}