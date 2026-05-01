package sfedu.ictis.walkOfInterest.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DomainUserProfile(
    val id: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val bio: String?,
    val photoUrl: String?,
    val countTrips: Int,
    val countSpots: Int,
    val countComments: Int
) : Parcelable {
    val fullName: String get() = listOf(firstName, lastName).filter { it.isNotBlank() }.joinToString(" ")
}