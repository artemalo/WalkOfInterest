package sfedu.ictis.walkOfInterest.data.model

data class UpdateProfileRequest (
    val firstName: String,
    val lastName: String,
    val bio: String?
)