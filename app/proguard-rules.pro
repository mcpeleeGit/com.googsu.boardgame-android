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

# 기본 규칙
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*

# Compose 관련 규칙
-keep class androidx.compose.** { *; }
-keepclassmembers class androidx.compose.** { *; }

# 앱의 주요 클래스 보존
-keep class com.googsu.boardgame.MainActivity { *; }
-keep class com.googsu.boardgame.ui.theme.** { *; }

# 벡터 드로어블 보존
-keep class * extends android.graphics.drawable.Drawable { *; }
-keep class * extends android.graphics.drawable.VectorDrawable { *; }
