package sfedu.ictis.walkOfInterest.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class PoiStatus : Parcelable {
    PENDING, APPROVED, REJECTED
}