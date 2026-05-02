package sfedu.ictis.walkOfInterest.data.repository

import org.json.JSONObject
import retrofit2.Response
import sfedu.ictis.walkOfInterest.data.api.PoiApi
import sfedu.ictis.walkOfInterest.data.mapper.toDomain
import sfedu.ictis.walkOfInterest.data.model.dto.ReviewRequestDto
import sfedu.ictis.walkOfInterest.domain.exception.ServerException
import sfedu.ictis.walkOfInterest.domain.model.DomainPoiInfo
import sfedu.ictis.walkOfInterest.domain.model.DomainReview
import sfedu.ictis.walkOfInterest.domain.repository.PoiRepository

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

    private fun Response<*>.toException(fallback: String): Exception {
        val rawError = errorBody()?.string()
        val parsedMessage = rawError?.let { tryParseFirstJsonValue(it) }

        return when (code()) {
            in 400..499 -> ServerException(parsedMessage ?: fallback)
            else -> Exception("$fallback (код ${code()})")
        }
    }

    private fun tryParseFirstJsonValue(raw: String): String? = runCatching {
        val json = JSONObject(raw)
        val keys = json.keys()
        if (keys.hasNext()) json.getString(keys.next()) else null
    }.getOrNull()
}