package com.parking.notification.di

import android.content.Context
import com.parking.notification.service.alert.AlertManager
import com.parking.notification.service.alert.NotificationChannels
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Provides
    @Singleton
    fun provideNotificationChannels(@ApplicationContext context: Context): NotificationChannels {
        return NotificationChannels(context)
    }

    @Provides
    @Singleton
    fun provideAlertManager(
        @ApplicationContext context: Context,
        channels: NotificationChannels
    ): AlertManager {
        return AlertManager(context, channels)
    }
}
