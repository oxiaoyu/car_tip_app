package com.parking.notification.di

import android.content.Context
import androidx.room.Room
import com.parking.notification.data.dao.HistoryDao
import com.parking.notification.data.dao.ItemRuleDao
import com.parking.notification.data.dao.NotificationDao
import com.parking.notification.data.dao.RuleDao
import com.parking.notification.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        val passphrase = android.security.keystore.KeyGenParameterSpec
            .Builder("parking_notification_db_key", android.security.keystore.KeyProperties.PURPOSE_ENCRYPT)
            .setBlockModes(android.security.keystore.KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(android.security.keystore.KeyProperties.ENCRYPTION_PADDING_NONE)
            .build()
        val keyStore = java.security.KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        if (!keyStore.containsAlias("parking_notification_db_key")) {
            val keyGenerator = javax.crypto.KeyGenerator.getInstance("AES", "AndroidKeyStore")
            keyGenerator.init(passphrase)
            keyGenerator.generateKey()
        }
        val secretKey = (keyStore.getEntry("parking_notification_db_key", null)
            as java.security.KeyStore.SecretKeyEntry).secretKey
        val hexKey = secretKey.encoded.joinToString("") { "%02x".format(it) }

        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "parking_notification.db"
        )
            .openHelperFactory(net.sqlcipher.database.SupportFactory(hexKey.toByteArray()))
            .build()
    }

    @Provides fun provideNotificationDao(db: AppDatabase): NotificationDao = db.notificationDao()
    @Provides fun provideRuleDao(db: AppDatabase): RuleDao = db.ruleDao()
    @Provides fun provideItemRuleDao(db: AppDatabase): ItemRuleDao = db.itemRuleDao()
    @Provides fun provideHistoryDao(db: AppDatabase): HistoryDao = db.historyDao()
}
