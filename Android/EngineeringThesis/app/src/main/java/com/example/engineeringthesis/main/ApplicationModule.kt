package com.example.engineeringthesis.main

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.example.engineeringthesis.database.RunDatabase
import com.example.engineeringthesis.others.Constants.KEY_FIRST_TIME_TOGGLE
import com.example.engineeringthesis.others.Constants.KEY_NAME
import com.example.engineeringthesis.others.Constants.KEY_WEIGHT
import com.example.engineeringthesis.others.Constants.NAME_SHARED_PREFERENCES
import com.example.engineeringthesis.others.Constants.RUNNING_MAIN_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object ApplicationModule {

    @Singleton
    @Provides
    fun provideRunningDatabase(
        @ApplicationContext context: Context,
    ) = Room.databaseBuilder(
        context,
        RunDatabase::class.java,
        RUNNING_MAIN_DATABASE_NAME
    ).fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideRunDao(database: RunDatabase) = database.getRunDao()

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences(NAME_SHARED_PREFERENCES, MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideWeight(sharedPreferences: SharedPreferences) =
        sharedPreferences.getFloat(KEY_WEIGHT, 80f)

    @Singleton
    @Provides
    fun provideName(sharedPreferences: SharedPreferences) =
        sharedPreferences.getString(KEY_NAME, "")
            ?: ""

    @Singleton
    @Provides
    fun provideFirstTimeToggle(sharedPreferences: SharedPreferences) =
        sharedPreferences.getBoolean(KEY_FIRST_TIME_TOGGLE, true)
}