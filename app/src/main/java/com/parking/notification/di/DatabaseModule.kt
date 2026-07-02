package com.parking.notification.di

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
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
import net.sqlcipher.database.SupportFactory
import java.security.KeyStore
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import javax.crypto.KeyGenerator
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private val cachedDatabase = AtomicReference<AppDatabase>()
    private val initLatch = CountDownLatch(1)

    @JvmStatic
    fun initDatabase(context: Context) {
        if (cachedDatabase.get() != null) return
        val hexKey = loadOrGenerateDatabaseKey()
        val db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "parking_notification.db"
        )
            .openHelperFactory(SupportFactory(hexKey.toByteArray()))
            .build()
        cachedDatabase.set(db)
        initLatch.countDown()
    }

    private fun loadOrGenerateDatabaseKey(): String {
        val passphrase = KeyGenParameterSpec.Builder(
            "parking_notification_db_key", KeyProperties.PURPOSE_ENCRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .build()
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        if (!keyStore.containsAlias("parking_notification_db_key")) {
            val keyGenerator = KeyGenerator.getInstance("AES", "AndroidKeyStore")
            keyGenerator.init(passphrase)
            keyGenerator.generateKey()
        }
        val secretKey = (keyStore.getEntry("parking_notification_db_key", null)
            as KeyStore.SecretKeyEntry).secretKey
        return secretKey.encoded.joinToString("") { "%02x".format(it) }
    }

    @Provides
    @Singleton
    fun provideAppDatabase(): AppDatabase {
        val db = cachedDatabase.get()
        if (db != null) return db
        try {
            initLatch.await(15, TimeUnit.SECONDS)
        } catch (_: InterruptedException) {
            Thread.currentThread().interrupt()
        }
        return cachedDatabase.get()
            ?: throw IllegalStateException("AppDatabase init timed out after 15s")
    }

    @Provides fun provideNotificationDao(db: AppDatabase): NotificationDao = db.notificationDao()
    @Provides fun provideRuleDao(db: AppDatabase): RuleDao = db.ruleDao()
    @Provides fun provideItemRuleDao(db: AppDatabase): ItemRuleDao = db.itemRuleDao()
    @Provides fun provideHistoryDao(db: AppDatabase): HistoryDao = db.historyDao()
}
