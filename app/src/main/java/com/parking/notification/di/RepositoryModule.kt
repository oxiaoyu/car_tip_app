package com.parking.notification.di

import com.parking.notification.data.repository.HistoryRepository
import com.parking.notification.data.repository.NotificationRepository
import com.parking.notification.data.repository.RuleRepository
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    // Repositories are concrete classes, bound via @Singleton directly
}
