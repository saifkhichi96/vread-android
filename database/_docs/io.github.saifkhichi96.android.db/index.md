---
title: io.github.saifkhichi96.android.db
---


# Package io.github.saifkhichi96.android.db



## Types


| Name | Summary |
|---|---|
| [FirebaseDatabase](-firebase-database/index.html) | [androidJvm]<br>class [FirebaseDatabase](-firebase-database/index.html)@Injectconstructor(context: [Context](https://developer.android.com/reference/kotlin/android/content/Context.html), persistent: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)?) : [RemoteDatabase](-remote-database/index.html)<br>Implements the [RemoteDatabase](-remote-database/index.html) with Firebase Realtime Database. |
| [LocalDatabase](-local-database/index.html) | [androidJvm]<br>abstract class [LocalDatabase](-local-database/index.html)<br>Defines an interface for managing on-device app data. |
| [PrefsDatabase](-prefs-database/index.html) | [androidJvm]<br>class [PrefsDatabase](-prefs-database/index.html)@Injectconstructor(context: [Context](https://developer.android.com/reference/kotlin/android/content/Context.html)) : [LocalDatabase](-local-database/index.html)<br>Implements the [LocalDatabase](-local-database/index.html) with Shared Preferences. |
| [RemoteDatabase](-remote-database/index.html) | [androidJvm]<br>abstract class [RemoteDatabase](-remote-database/index.html)<br>Defines an interface for managing a remote database. |

