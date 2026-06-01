package sfedu.ictis.walkOfInterest.data.mapper

import sfedu.ictis.walkOfInterest.data.local.TripEntity
import sfedu.ictis.walkOfInterest.domain.model.DomainTrip

fun DomainTrip.toEntity(): TripEntity = TripEntity(
    id = id,
    addressFrom = addressFrom,
    addressTo = addressTo,
    from = from,
    to = to,
    bestRouteTime = bestRouteTime,
    userSelectedTime = userSelectedTime,
    totalPois = totalPois,
    selectedPois = selectedPois
)

fun TripEntity.toDomain(): DomainTrip = DomainTrip(
    id = id,
    addressFrom = addressFrom,
    addressTo = addressTo,
    from = from,
    to = to,
    bestRouteTime = bestRouteTime,
    userSelectedTime = userSelectedTime,
    totalPois = totalPois,
    selectedPois = selectedPois
)