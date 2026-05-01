package sfedu.ictis.walkOfInterest.data.model.dto

data class UserProfileDto(
    val id: Long,
    val username: String,
    val firstName: String?,
    val lastName: String?,
    val bio: String?,
    val photoUrl: String?,
    val countTrips: Int?,
    val countSpots: Int?,
    val countComments: Int?
)