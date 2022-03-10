---
title: PrefsDatabase
---


# PrefsDatabase



[androidJvm]\
class [PrefsDatabase](index.html)@Injectconstructor(context: [Context](https://developer.android.com/reference/kotlin/android/content/Context.html)) : [LocalDatabase](../-local-database/index.html)

Implements the [LocalDatabase](../-local-database/index.html) with Shared Preferences.



#### Author



saifkhichi96



#### Since



1.0.0



## See also


androidJvm

| | |
|---|---|
| [io.github.saifkhichi96.android.db.LocalDatabase](../-local-database/index.html) |  |



## Parameters


androidJvm

| | |
|---|---|
| context | The application context. |



## Constructors


| | |
|---|---|
| [PrefsDatabase](-prefs-database.html) | [androidJvm]<br>@Inject<br>fun [PrefsDatabase](-prefs-database.html)(context: [Context](https://developer.android.com/reference/kotlin/android/content/Context.html))<br>Creates a new instance of the [PrefsDatabase](index.html) class. |


## Functions


| Name | Summary |
|---|---|
| [clear](clear.html) | [androidJvm]<br>open override fun [clear](clear.html)()<br>Deletes all data from database. |
| [create](create.html) | [androidJvm]<br>open override fun [create](create.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), value: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html))<br>Saves an object at a path in the database. |
| [getOrDefault](../-local-database/get-or-default.html) | [androidJvm]<br>inline fun &lt;[T](../-local-database/get-or-default.html) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; [getOrDefault](../-local-database/get-or-default.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), defValue: [T](../-local-database/get-or-default.html)): [T](../-local-database/get-or-default.html)<br>Gets data stored at a path. |
| [getOrElse](../-local-database/get-or-else.html) | [androidJvm]<br>inline fun &lt;[T](../-local-database/get-or-else.html) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; [getOrElse](../-local-database/get-or-else.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), onFailure: (exception: [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)) -&gt; [T](../-local-database/get-or-else.html)): [T](../-local-database/get-or-else.html)<br>Gets data stored at a path. |
| [getOrNull](../-local-database/get-or-null.html) | [androidJvm]<br>inline fun &lt;[T](../-local-database/get-or-null.html) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; [getOrNull](../-local-database/get-or-null.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [T](../-local-database/get-or-null.html)?<br>Gets data stored at a path. |
| [getOrThrow](get-or-throw.html) | [androidJvm]<br>open override fun &lt;[T](get-or-throw.html) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; [getOrThrow](get-or-throw.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), type: [Class](https://developer.android.com/reference/kotlin/java/lang/Class.html)&lt;[T](get-or-throw.html)&gt;): [T](get-or-throw.html)<br>Gets data stored at a path. |
| [remove](remove.html) | [androidJvm]<br>open override fun [remove](remove.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html))<br>Deletes data stored at a path. |
| [update](update.html) | [androidJvm]<br>open override fun [update](update.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), value: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html))<br>Updates existing data at a path. |

