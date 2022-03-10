---
title: LocalDatabase
---


# LocalDatabase



[androidJvm]\
abstract class [LocalDatabase](index.html)

Defines an interface for managing on-device app data.



Purpose of this class is to provide on-device storage for caching data received from [RemoteDatabase](../-remote-database/index.html), and to read and write any other local app data.



#### Author



saifkhichi96



#### Since



1.0.0



## Constructors


| | |
|---|---|
| [LocalDatabase](-local-database.html) | [androidJvm]<br>fun [LocalDatabase](-local-database.html)() |


## Functions


| Name | Summary |
|---|---|
| [clear](clear.html) | [androidJvm]<br>abstract fun [clear](clear.html)()<br>Deletes all data from database. |
| [create](create.html) | [androidJvm]<br>abstract fun [create](create.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), value: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html))<br>Saves an object at a path in the database. |
| [getOrDefault](get-or-default.html) | [androidJvm]<br>inline fun &lt;[T](get-or-default.html) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; [getOrDefault](get-or-default.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), defValue: [T](get-or-default.html)): [T](get-or-default.html)<br>Gets data stored at a path. |
| [getOrElse](get-or-else.html) | [androidJvm]<br>inline fun &lt;[T](get-or-else.html) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; [getOrElse](get-or-else.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), onFailure: (exception: [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)) -&gt; [T](get-or-else.html)): [T](get-or-else.html)<br>Gets data stored at a path. |
| [getOrNull](get-or-null.html) | [androidJvm]<br>inline fun &lt;[T](get-or-null.html) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; [getOrNull](get-or-null.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [T](get-or-null.html)?<br>Gets data stored at a path. |
| [getOrThrow](get-or-throw.html) | [androidJvm]<br>abstract fun &lt;[T](get-or-throw.html) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; [getOrThrow](get-or-throw.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), type: [Class](https://developer.android.com/reference/kotlin/java/lang/Class.html)&lt;[T](get-or-throw.html)&gt;): [T](get-or-throw.html)<br>Gets data stored at a path. |
| [remove](remove.html) | [androidJvm]<br>abstract fun [remove](remove.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html))<br>Deletes data stored at a path. |
| [update](update.html) | [androidJvm]<br>abstract fun [update](update.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), value: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html))<br>Updates existing data at a path. |


## Inheritors


| Name |
|---|
| [PrefsDatabase](../-prefs-database/index.html) |

