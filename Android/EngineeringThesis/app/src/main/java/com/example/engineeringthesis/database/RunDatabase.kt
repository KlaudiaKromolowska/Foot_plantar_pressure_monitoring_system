package com.example.engineeringthesis.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.engineeringthesis.others.BitmapCreator

@Database(
    entities = [Run::class],
    version = 3
)
@TypeConverters(BitmapCreator::class)
abstract class RunDatabase : RoomDatabase() {
    abstract fun getRunDao(): RunDAO
}