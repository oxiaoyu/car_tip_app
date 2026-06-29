# ProGuard rules for Parking Notification App
# Keep Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Keep Room entities
-keep class com.parking.notification.data.entity.** { *; }

# Keep SQLCipher
-keep class net.sqlcipher.** { *; }
-keep class net.zetetic.** { *; }

# Keep Hilt worker factory
-keep class * extends androidx.work.Worker { *; }

# Keep Timber
-keep class timber.log.Timber { *; }

# Keep serialization
-keepattributes *Annotation*, Signature, Exception
-keep class kotlin.Metadata { *; }

# General Android rules
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.app.Activity
