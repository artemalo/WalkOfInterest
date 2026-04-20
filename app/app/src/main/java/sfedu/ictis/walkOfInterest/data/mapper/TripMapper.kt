package sfedu.ictis.walkOfInterest.data.mapper

import sfedu.ictis.walkOfInterest.data.local.TripEntity
import sfedu.ictis.walkOfInterest.domain.model.DomainTrip

fun DomainTrip.toEntity(): TripEntity {
    return TripEntity(
        id = this.id,
        addressFrom = this.addressFrom,
        addressTo = this.addressTo,
        from = this.from,
        to = this.to,
        totalTime = this.totalTime,
        totalPois = this.totalPois,
        selectedPois = this.selectedPois
    )
}

fun TripEntity.toDomain(): DomainTrip {
    return DomainTrip(
        id = this.id,
        addressFrom = this.addressFrom,
        addressTo = this.addressTo,
        from = this.from,
        to = this.to,
        totalTime = this.totalTime,
        totalPois = this.totalPois,
        selectedPois = this.selectedPois
    )
}