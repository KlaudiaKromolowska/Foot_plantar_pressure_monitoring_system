package com.example.engineeringthesis.newRun

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.engineeringthesis.R
import com.example.engineeringthesis.main.MainActivity
import com.example.engineeringthesis.others.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @ServiceScoped
    @Provides
    fun provideFusedLocationProviderClient(
        @ApplicationContext context: Context,
    ) = FusedLocationProviderClient(context)

    @ServiceScoped
    @Provides
    fun provideMainActivityPendingIntent(
        @ApplicationContext context: Context,
    ): PendingIntent = PendingIntent.getActivity(
        context,
        0,
        Intent(context, MainActivity::class.java).also {
            it.action = Constants.EVENT_SHOW_TRACKING_FRAGMENT
        },
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    @ServiceScoped
    @Provides
    fun provideBaseNotificationBuilder(
        @ApplicationContext context: Context,
        pendingIntent: PendingIntent,
    ): NotificationCompat.Builder = NotificationCompat.Builder(context, Constants.NOTIFICATION_ID_CHANNEL)
        .setContentTitle(context.getString(R.string.app_name_notification))
        .setContentText("00:00:00")
        .setContentIntent(pendingIntent)
        .setAutoCancel(false)
        .setOngoing(true)
        .setSmallIcon(R.drawable.icon_running)

}