package sfedu.ictis.walkOfInterest.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [TripEntity::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() { // TODO connect local <-> remote DB
    abstract fun tripDao(): TripDao
}