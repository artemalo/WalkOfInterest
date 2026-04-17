package sfedu.ictis.walkOfInterest.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import sfedu.ictis.walkOfInterest.data.model.RouteRequest
import sfedu.ictis.walkOfInterest.data.model.RouteResponse
import sfedu.ictis.walkOfInterest.data.model.SearchRequest
import sfedu.ictis.walkOfInterest.data.model.SearchResponse

interface RouteApi {
    @POST("/poi/generate/route")
    suspend fun getRoute(
        @Body request: RouteRequest
    ): Response<RouteResponse>

    @POST("/poi/generate/search")
    suspend fun searchRoute(
        @Body request: SearchRequest
    ): Response<SearchResponse>
}