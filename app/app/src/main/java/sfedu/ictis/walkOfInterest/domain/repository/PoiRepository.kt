package sfedu.ictis.walkOfInterest.domain.repository

import sfedu.ictis.walkOfInterest.domain.model.DomainMyPoi
import sfedu.ictis.walkOfInterest.domain.model.DomainPoiInfo
import sfedu.ictis.walkOfInterest.domain.model.DomainPoiNearbyCheck
import sfedu.ictis.walkOfInterest.domain.model.DomainPoint
import sfedu.ictis.walkOfInterest.domain.model.DomainReview
import sfedu.ictis.walkOfInterest.domain.model.ReactionType

interface PoiRepository {
    suspend fun getPoiById(id: Long, lang: String = "ru"): Result<DomainPoiInfo>

    suspend fun getReviewsByPoiId(id: Long, lang: String = "ru"): Result<List<DomainReview>>

    suspend fun upsertMyReview(
        poiId: Long,
        rating: Int,
        content: String?,
        lang: String = "ru"
    ): Result<DomainReview>

    suspend fun setReviewReaction(
        reviewId: Long,
        type: ReactionType
    ): Result<ReviewReactionState>

    suspend fun checkNearby(
        point: DomainPoint,
        lang: String = "ru"
    ): Result<DomainPoiNearbyCheck>

    suspend fun createPoi(
        point: DomainPoint,
        name: String,
        description: String?,
        lang: String,
        subcategoryIds: List<Int>,
        force: Boolean
    ): Result<DomainPoiInfo>

    suspend fun updatePoi(
        id: Long,
        point: DomainPoint,
        name: String,
        description: String?,
        lang: String,
        subcategoryIds: List<Int>
    ): Result<DomainPoiInfo>

    suspend fun supplementPoi(
        id: Long,
        name: String,
        description: String?,
        lang: String,
        subcategoryIds: List<Int>
    ): Result<DomainPoiInfo>

    suspend fun getMyPois(lang: String = "ru"): Result<List<DomainMyPoi>>

    suspend fun uploadPoiPhoto(id: Long, bytes: ByteArray, extension: String): Result<DomainPoiInfo>
}

data class ReviewReactionState(
    val reviewId: Long,
    val likes: Int,
    val dislikes: Int,
    val myReaction: ReactionType?
)
