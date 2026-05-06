package sfedu.ictis.walkOfInterest.data.repository

import sfedu.ictis.walkOfInterest.data.api.PoiApi
import sfedu.ictis.walkOfInterest.data.mapper.toDomain
import sfedu.ictis.walkOfInterest.data.model.dto.PoiAddDto
import sfedu.ictis.walkOfInterest.data.model.dto.PoiNearbyCheckRequestDto
import sfedu.ictis.walkOfInterest.data.model.dto.PointDto
import sfedu.ictis.walkOfInterest.data.model.dto.ReviewReactionRequestDto
import sfedu.ictis.walkOfInterest.data.model.dto.ReviewRequestDto
import sfedu.ictis.walkOfInterest.data.util.toException
import sfedu.ictis.walkOfInterest.data.util.tryParseFirstJsonValue
import sfedu.ictis.walkOfInterest.domain.exception.PoiAlreadyExistsException
import sfedu.ictis.walkOfInterest.domain.model.DomainMyPoi
import sfedu.ictis.walkOfInterest.domain.model.DomainPoiInfo
import sfedu.ictis.walkOfInterest.domain.model.DomainPoiNearbyCheck
import sfedu.ictis.walkOfInterest.domain.model.DomainPoint
import sfedu.ictis.walkOfInterest.domain.model.DomainReview
import sfedu.ictis.walkOfInterest.domain.model.ReactionType
import sfedu.ictis.walkOfInterest.domain.repository.PoiRepository
import sfedu.ictis.walkOfInterest.domain.repository.ReviewReactionState

class PoiRepositoryImpl(
    private val api: PoiApi
) : PoiRepository {

    override suspend fun getPoiById(id: Long, lang: String): Result<DomainPoiInfo> = runCatching {
        val response = api.getPoiById(id, lang)
        val body = response.body()

        if (response.isSuccessful && body != null) {
            body.toDomain()
        } else {
            throw response.toException("Не удалось загрузить точку")
        }
    }

    override suspend fun getReviewsByPoiId(id: Long, lang: String): Result<List<DomainReview>> = runCatching {
        val response = api.getReviewsByPoiId(id, lang)
        val body = response.body()

        if (response.isSuccessful && body != null) {
            body.map { it.toDomain() }
        } else {
            throw response.toException("Не удалось загрузить отзывы")
        }
    }

    override suspend fun upsertMyReview(
        poiId: Long,
        rating: Int,
        content: String?,
        lang: String
    ): Result<DomainReview> = runCatching {
        val response = api.upsertMyReview(
            id = poiId,
            request = ReviewRequestDto(rating = rating, content = content),
            lang = lang
        )
        val body = response.body()

        if (response.isSuccessful && body != null) {
            body.toDomain()
        } else {
            throw response.toException("Не удалось сохранить отзыв")
        }
    }

    override suspend fun setReviewReaction(
        reviewId: Long,
        type: ReactionType
    ): Result<ReviewReactionState> = runCatching {
        val response = api.setReviewReaction(
            reviewId = reviewId,
            request = ReviewReactionRequestDto(type = type.name)
        )
        val body = response.body()

        if (response.isSuccessful && body != null) {
            ReviewReactionState(
                reviewId = body.reviewId,
                likes = body.likes,
                dislikes = body.dislikes,
                myReaction = when (body.myReaction?.uppercase()) {
                    "LIKE" -> ReactionType.LIKE
                    "DISLIKE" -> ReactionType.DISLIKE
                    else -> null
                }
            )
        } else {
            throw response.toException("Не удалось поставить реакцию")
        }
    }

    override suspend fun checkNearby(
        point: DomainPoint,
        lang: String
    ): Result<DomainPoiNearbyCheck> = runCatching {
        val response = api.checkNearby(
            request = PoiNearbyCheckRequestDto(point = PointDto(point.lat, point.lon)),
            lang = lang
        )
        val body = response.body()

        if (response.isSuccessful && body != null) {
            body.toDomain()
        } else {
            throw response.toException("Не удалось проверить точку")
        }
    }

    override suspend fun createPoi(
        point: DomainPoint,
        name: String,
        description: String?,
        lang: String,
        subcategoryIds: List<Int>,
        force: Boolean
    ): Result<DomainPoiInfo> = runCatching {
        val response = api.createPoi(
            poi = PoiAddDto(
                point = PointDto(point.lat, point.lon),
                name = name,
                description = description,
                lang = lang,
                subcategoriesId = subcategoryIds,
                force = if (force) true else null
            )
        )
        val body = response.body()

        if (response.isSuccessful && body != null) {
            body.toDomain()
        } else if (response.code() == 409) {
            val parsed = response.errorBody()?.string()?.let { tryParseFirstJsonValue(it) }
            throw PoiAlreadyExistsException(parsed ?: "Похожее место уже существует рядом")
        } else {
            throw response.toException("Не удалось создать место")
        }
    }

    override suspend fun updatePoi(
        id: Long,
        point: DomainPoint,
        name: String,
        description: String?,
        lang: String,
        subcategoryIds: List<Int>
    ): Result<DomainPoiInfo> = runCatching {
        val response = api.updatePoi(
            id = id,
            poi = PoiAddDto(
                id = id,
                point = PointDto(point.lat, point.lon),
                name = name,
                description = description,
                lang = lang,
                subcategoriesId = subcategoryIds,
                force = null
            )
        )
        val body = response.body()

        if (response.isSuccessful && body != null) {
            body.toDomain()
        } else {
            throw response.toException("Не удалось обновить место")
        }
    }

    override suspend fun supplementPoi(
        id: Long,
        name: String,
        description: String?,
        lang: String,
        subcategoryIds: List<Int>
    ): Result<DomainPoiInfo> = runCatching {
        val response = api.supplementPoi(
            id = id,
            poi = PoiAddDto(
                id = id,
                point = PointDto(0.0, 0.0), // ignore
                name = name,
                description = description,
                lang = lang,
                subcategoriesId = subcategoryIds,
                force = null
            )
        )
        val body = response.body()

        if (response.isSuccessful && body != null) {
            body.toDomain()
        } else {
            throw response.toException("Не удалось дополнить место")
        }
    }

    override suspend fun getMyPois(lang: String): Result<List<DomainMyPoi>> = runCatching {
        val response = api.getMyPois(lang)
        val body = response.body()

        if (response.isSuccessful && body != null) {
            body.map { it.toDomain() }
        } else {
            throw response.toException("Не удалось загрузить ваши места")
        }
    }
}
