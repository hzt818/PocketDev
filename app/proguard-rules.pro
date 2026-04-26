# Add project specific ProGuard rules here.
-keepattributes *Annotation*
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}
-keep class com.pocketdev.data.remote.api.** { *; }
-keep class com.pocketdev.domain.model.** { *; }
