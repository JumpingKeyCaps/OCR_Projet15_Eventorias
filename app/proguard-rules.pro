# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


# Garde les annotations et règles spécifiques pour Android
-keep public class * extends android.app.Application
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.Service
-keep public class * extends android.view.View

# Garde toutes les annotations utilisées dans le projet
-keepattributes *Annotation*

# Garde les classes de firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.firebase.firestore.** { *; }
-keepnames class com.google.firebase.**
-keep class com.google.firebase.auth.** { *; }

# Garde les méthodes et classes de Jetpack Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Garde les modèles de données utilisés pour la sérialisation JSON
-keep class com.openclassroom.eventorias.domain.User{ *; }
-keep class com.openclassroom.eventorias.domain.Event{ *; }


-keepclassmembers class * implements java.io.Serializable {
    static volatile long serialVersionUID;
}
