# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\Android_SDK/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-allowaccessmodification

-dontpreverify      # 混淆时是否做预校验
-dontskipnonpubliclibraryclasses    #不去忽略非公共的库类
-dontskipnonpubliclibraryclassmembers
-dontusemixedcaseclassnames     # 是否使用大小写混合

-dontshrink
-dontwarn android.support.v4.**
-dontwarn android.support.v13.**

-flattenpackagehierarchy
-ignorewarnings
-optimizationpasses 5       # 指定代码的压缩级别     #-dontoptimize
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*  # 混淆时所采用的算法
-printmapping map.txt
-verbose        # 混淆时是否记录日志

-renamesourcefileattribute SourceFile

-keepattributes Exceptions, EnclosingMethod, LineNumberTable
#-keep public class * extends android.app.Activity      # 保持哪些类不被混淆
#-keep public class * extends android.app.Application   # 保持哪些类不被混淆
#-keep public class * extends android.app.Service       # 保持哪些类不被混淆
-keep public class * extends android.content.BroadcastReceiver  # 保持哪些类不被混淆
-keep public class * extends android.content.ContentProvider    # 保持哪些类不被混淆
-keep public class * extends android.app.backup.BackupAgentHelper # 保持哪些类不被混淆
-keep public class * extends android.preference.Preference        # 保持哪些类不被混淆
-keep public class * extends android.support.v4.**    #如果有引用v4包可以添加这行

#-keep interface android.support.v4.app.** { *; }
#-keep class android.support.v4.** { *; }

########################## 喜马拉雅接口 ##########################
-dontwarn okio.**
-keep class okio.** { *;}
-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.** { *;}
-dontwarn com.ximalaya.ting.android.player.**
-keep class com.ximalaya.ting.android.player.** { *;}
-dontwarn com.google.gson.**
-keep class com.google.gson.** { *;}
-dontwarn android.support.**
-keep class android.support.** { *;}
-keep interface com.ximalaya.ting.android.opensdk.** {*;}
-keep class com.ximalaya.ting.android.opensdk.** { *; }

# 保持 native 方法不被混淆
-keepclasseswithmembernames class * {  native <methods>;}
# 保持自定义控件类不被混淆
-keepclasseswithmembers class * { public <init>(android.content.Context, android.util.AttributeSet);}
# 保持自定义控件类不被混淆
-keepclasseswithmembers class * { public <init>(android.content.Context, android.util.AttributeSet, int);}
# 保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity { public void *(android.view.View);}
# 保持枚举 enum 类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
# 保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {  public static final android.os.Parcelable$Creator *;}