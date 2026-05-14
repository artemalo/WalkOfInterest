package sfedu.ictis.walkOfInterest.domain.usecase

import sfedu.ictis.walkOfInterest.domain.model.DomainPoiInfo
import sfedu.ictis.walkOfInterest.domain.repository.PoiRepository

class UploadPoiPhotoUseCase(private val repository: PoiRepository) {
    suspend operator fun invoke(id: Long, bytes: ByteArray, extension: String): Result<DomainPoiInfo> =
        repository.uploadPoiPhoto(id, bytes, extension)
}
