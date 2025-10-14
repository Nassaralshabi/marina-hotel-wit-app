# Marina Hotel Kotlin - ProGuard Rules
# This file contains the ProGuard configuration for Marina Hotel application

#============================================================================
# GENERAL OPTIMIZATION RULES
#============================================================================

# Enable optimization and obfuscation
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose

# Keep application class
-keep public class * extends android.app.Application

# Keep all native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep all enum classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Parcelable implementations
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

#============================================================================
# ANDROID COMPONENTS
#============================================================================

# Keep all Activities, Services, BroadcastReceivers
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# Keep fragment classes
-keep class androidx.fragment.** { *; }
-keep class * extends androidx.fragment.app.Fragment

#============================================================================
# ANDROIDX AND MATERIAL DESIGN
#============================================================================

# AndroidX Navigation
-keep class androidx.navigation.** { *; }
-keepnames class androidx.navigation.fragment.NavHostFragment

# Material Design Components
-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**

# AndroidX RecyclerView
-keep class androidx.recyclerview.widget.** { *; }

# AndroidX ViewPager2
-keep class androidx.viewpager2.** { *; }

# AndroidX ConstraintLayout
-keep class androidx.constraintlayout.** { *; }

# AndroidX Lifecycle
-keep class androidx.lifecycle.** { *; }
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

#============================================================================
# ROOM DATABASE
#============================================================================

# Room - Keep entity classes
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *

# Room - Keep query methods
-keepclassmembers class * extends androidx.room.RoomDatabase {
    public abstract *;
}

#============================================================================
# KOTLIN COROUTINES
#============================================================================

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

#============================================================================
# MARINA HOTEL SPECIFIC CLASSES
#============================================================================

# Keep Marina Hotel main activities
-keep class com.marinahotel.kotlin.MainActivity { *; }
-keep class com.marinahotel.kotlin.dashboard.** { *; }
-keep class com.marinahotel.kotlin.bookings.** { *; }
-keep class com.marinahotel.kotlin.payments.** { *; }
-keep class com.marinahotel.kotlin.employees.** { *; }
-keep class com.marinahotel.kotlin.expenses.** { *; }
-keep class com.marinahotel.kotlin.finance.** { *; }
-keep class com.marinahotel.kotlin.notes.** { *; }
-keep class com.marinahotel.kotlin.reports.** { *; }
-keep class com.marinahotel.kotlin.rooms.** { *; }
-keep class com.marinahotel.kotlin.settings.** { *; }

# Keep data models
-keep class com.marinahotel.kotlin.data.** { *; }
-keep class com.marinahotel.kotlin.models.** { *; }

# Keep API interfaces and responses
-keep class com.marinahotel.kotlin.api.** { *; }
-keep class com.marinahotel.kotlin.network.** { *; }

#============================================================================
# REFLECTION AND SERIALIZATION
#============================================================================

# Keep classes with @Keep annotation
-keep @androidx.annotation.Keep class *
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}

# Gson serialization (if used)
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }

#============================================================================
# DEBUGGING AND LOGGING
#============================================================================

# Remove debug logging in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

#============================================================================
# SECURITY MEASURES
#============================================================================

# Remove sensitive method names for security
-repackageclasses 'com.marinahotel.obfuscated'

# Additional obfuscation
-allowaccessmodification
-mergeinterfacesaggressively

#============================================================================
# WARNINGS SUPPRESSION
#============================================================================

# Ignore warnings for optional dependencies
-dontwarn javax.annotation.**
-dontwarn javax.inject.**
-dontwarn sun.misc.Unsafe

# Ignore warnings for kotlinx
-dontwarn kotlinx.**
