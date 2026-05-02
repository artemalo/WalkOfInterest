package sfedu.ictis.walkOfInterest.domain.repository

import sfedu.ictis.walkOfInterest.domain.model.DomainPoiInfo
import sfedu.ictis.walkOfInterest.domain.model.DomainReview

interface PoiRepository {
    suspend fun getPoiById(id: Long, lang: String = "ru"): Result<DomainPoiInfo>

    suspend fun getReviewsByPoiId(id: Long, lang: String = "ru"): Result<List<DomainReview>>
}