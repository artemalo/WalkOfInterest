package sfedu.ictis.walkOfInterest.domain.repository

import sfedu.ictis.walkOfInterest.domain.model.DomainPoiInfo
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
}

data class ReviewReactionState(
    val reviewId: Long,
    val likes: Int,
    val dislikes: Int,
    val myReaction: ReactionType?
)