package sfedu.ictis.walkOfInterest.data.mapper

import sfedu.ictis.walkOfInterest.data.model.dto.ReviewDto
import sfedu.ictis.walkOfInterest.data.model.dto.UserProfileDto
import sfedu.ictis.walkOfInterest.domain.model.DomainReview
import sfedu.ictis.walkOfInterest.domain.model.DomainUserProfile
import java.text.SimpleDateFormat
import java.util.Locale

fun UserProfileDto.toDomain(): DomainUserProfile = DomainUserProfile(
    id = id.toString(),
    username = username,
    firstName = firstName.orEmpty(),
    lastName = lastName.orEmpty(),
    bio = bio,
    photoUrl = photoUrl,
    countTrips = countTrips ?: 0,
    countSpots = countSpots ?: 0,
    countComments = countComments ?: 0
)

fun ReviewDto.toDomain(): DomainReview = DomainReview(
    id = id.toString(),
    authorUsername = authorUsername.orEmpty(),
    authorAvatarUrl = authorAvatarUrl,
    poiId = poiId?.toString().orEmpty(),
    poiName = poiName.orEmpty(),
    content = content.orEmpty(),
    rating = rating ?: 0,
    likes = likes ?: 0,
    dislikes = dislikes ?: 0,
    createdAtMillis = parseIsoToMillis(createdAt)
)



private fun parseIsoToMillis(iso: String?): Long {
    if (iso.isNullOrBlank()) return System.currentTimeMillis()

    return runCatching {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

        val date = format.parse(iso)
        date?.time ?: System.currentTimeMillis()
    }.getOrDefault(System.currentTimeMillis())
}