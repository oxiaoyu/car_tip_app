package com.parking.notification.di

import android.content.Context
import android.os.Build
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
import timber.log.Timber
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
        val t0 = System.currentTimeMillis()
        Timber.i("[TRACE] DB_INIT: initDatabase() called on thread=%s", Thread.currentThread().name)
        if (cachedDatabase.get() != null) {
            Timber.i("[TRACE] DB_INIT: already cached, skip at +%dms", System.currentTimeMillis() - t0)
            return
        }
        val hexKey = loadOrGenerateDatabaseKey()
        Timber.i("[TRACE] DB_INIT: key loaded at +%dms, building Room DB...", System.currentTimeMillis() - t0)
        val db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "parking_notification.db"
        )
            .openHelperFactory(SupportFactory(hexKey.toByteArray()))
            .build()
        cachedDatabase.set(db)
        initLatch.countDown()
        Timber.i("[TRACE] DB_INIT: complete at +%dms, thread=%s", System.currentTimeMillis() - t0, Thread.currentThread().name)
    }

    private fun loadOrGenerateDatabaseKey(): String {
        val t0 = System.currentTimeMillis()
        Timber.i("[TRACE] DB_KEY: loadOrGenerateDatabaseKey() start on thread=%s", Thread.currentThread().name)
        val passphrase = KeyGenParameterSpec.Builder(
            "parking_notification_db_key", KeyProperties.PURPOSE_ENCRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .build()
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        if (!keyStore.containsAlias("parking_notification_db_key")) {
            Timber.i("[TRACE] DB_KEY: key not found, generating new AES key via KeyStore IPC...")
            val keyGenerator = KeyGenerator.getInstance("AES", "AndroidKeyStore")
            keyGenerator.init(passphrase)
            keyGenerator.generateKey()
            Timber.i("[TRACE] DB_KEY: new AES key generated at +%dms", System.currentTimeMillis() - t0)
        } else {
            Timber.i("[TRACE] DB_KEY: existing key found in KeyStore at +%dms", System.currentTimeMillis() - t0)
        }
        val secretKey = (keyStore.getEntry("parking_notification_db_key", null)
            as KeyStore.SecretKeyEntry).secretKey
        val result = secretKey.encoded.joinToString("") { "%02x".format(it) }
        Timber.i("[TRACE] DB_KEY: complete at +%dms, sdk=%d, mfr=%s", System.currentTimeMillis() - t0, Build.VERSION.SDK_INT, Build.MANUFACTURER)
        return result
    }

    @Provides
    @Singleton
    fun provideAppDatabase(): AppDatabase {
        val t0 = System.currentTimeMillis()
        val thread = Thread.currentThread()
        Timber.i("[TRACE] DB_PROVIDE: provideAppDatabase() called on thread=%s", thread.name)
        val db = cachedDatabase.get()
        if (db != null) {
            Timber.i("[TRACE] DB_PROVIDE: instant cache hit at +%dms", System.currentTimeMillis() - t0)
            return db
        }
        Timber.w("[TRACE] DB_PROVIDE: cache MISS — waiting on CountDownLatch (thread=%s)", thread.name)
        var waited = false
        try {
            waited = initLatch.await(15, TimeUnit.SECONDS)
        } catch (_: InterruptedException) {
            Thread.currentThread().interrupt()
        }
        val result = cachedDatabase.get()
        if (result != null) {
            Timber.i("[TRACE] DB_PROVIDE: latch released after %dms, waited=%s, thread=%s",
                System.currentTimeMillis() - t0, waited, thread.name)
        } else {
            Timber.e("[TRACE] DB_PROVIDE: TIMEOUT! 15s elapsed, DB init may be blocked by KeyStore IPC, thread=%s", thread.name)
        }
        return result ?: throw IllegalStateException("AppDatabase init timed out after 15s")
    }

    @Provides fun provideNotificationDao(db: AppDatabase): NotificationDao = db.notificationDao()
    @Provides fun provideRuleDao(db: AppDatabase): RuleDao = db.ruleDao()
    @Provides fun provideItemRuleDao(db: AppDatabase): ItemRuleDao = db.itemRuleDao()
    @Provides fun provideHistoryDao(db: AppDatabase): HistoryDao = db.historyDao()
}
