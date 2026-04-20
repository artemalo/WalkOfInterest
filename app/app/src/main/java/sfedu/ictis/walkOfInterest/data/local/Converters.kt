package sfedu.ictis.walkOfInterest.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import sfedu.ictis.walkOfInterest.domain.model.DomainPoint
import sfedu.ictis.walkOfInterest.domain.model.RoutePoint

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromDomainPoint(point: DomainPoint): String = gson.toJson(point)

    @TypeConverter
    fun toDomainPoint(json: String): DomainPoint = gson.fromJson(json, DomainPoint::class.java)

    @TypeConverter
    fun fromRoutePointList(list: List<RoutePoint>): String = gson.toJson(list)

    @TypeConverter
    fun toRoutePointList(json: String): List<RoutePoint> {
        val type = object : TypeToken<List<RoutePoint>>() {}.type
        return gson.fromJson(json, type)
    }
}