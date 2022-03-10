---
title: RemoteDatabase
---


# RemoteDatabase



[androidJvm]\
abstract class [RemoteDatabase](index.html)

Defines an interface for managing a remote database.



Purpose of this class is to provide a two-way communication interface with a remote, cloud-based database so that the app can read and write app data from the cloud.



#### Author



saifkhichi96



#### Since



1.0.0



## Constructors


| | |
|---|---|
| [RemoteDatabase](-remote-database.html) | [androidJvm]<br>fun [RemoteDatabase](-remote-database.html)() |


## Functions


| Name | Summary |
|---|---|
| [clear](clear.html) | [androidJvm]<br>abstract suspend fun [clear](clear.html)()<br>Deletes all data from database. |
| [create](create.html) | [androidJvm]<br>abstract suspend fun [create](create.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), value: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html))<br>Saves an object at a path in the database. |
| [createChild](create-child.html) | [androidJvm]<br>suspend fun [createChild](create-child.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), value: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html))<br>Saves an object at a new child node of a path in the database.<br>[androidJvm]<br>suspend fun [createChild](create-child.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), childKey: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), value: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html))<br>Saves an object at a child node of a path in the database. |
| [createEmptyChild](create-empty-child.html) | [androidJvm]<br>abstract fun [createEmptyChild](create-empty-child.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>Adds a placeholder for a child node at a path. |
| [getOrDefault](get-or-default.html) | [androidJvm]<br>inline suspend fun &lt;[T](get-or-default.html) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; [getOrDefault](get-or-default.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), defValue: [T](get-or-default.html)): [T](get-or-default.html)<br>Gets data stored at a path. |
| [getOrElse](get-or-else.html) | [androidJvm]<br>inline suspend fun &lt;[T](get-or-else.html) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; [getOrElse](get-or-else.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), onFailure: (exception: [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)) -&gt; [T](get-or-else.html)): [T](get-or-else.html)<br>Gets data stored at a path. |
| [getOrNull](get-or-null.html) | [androidJvm]<br>inline suspend fun &lt;[T](get-or-null.html) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; [getOrNull](get-or-null.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [T](get-or-null.html)?<br>Gets data stored at a path. |
| [getOrThrow](get-or-throw.html) | [androidJvm]<br>abstract suspend fun &lt;[T](get-or-throw.html) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; [getOrThrow](get-or-throw.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), type: [Class](https://developer.android.com/reference/kotlin/java/lang/Class.html)&lt;[T](get-or-throw.html)&gt;): [T](get-or-throw.html)<br>Gets data stored at a path. |
| [list](list.html) | [androidJvm]<br>abstract suspend fun &lt;[T](list.html) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; [list](list.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), type: [Class](https://developer.android.com/reference/kotlin/java/lang/Class.html)&lt;[T](list.html)&gt;, filter: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?, equalTo: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[T](list.html)&gt;<br>Gets list of objects stored at a path. |
| [observe](observe.html) | [androidJvm]<br>@ExperimentalCoroutinesApi<br>abstract suspend fun &lt;[T](observe.html) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; [observe](observe.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), type: [Class](https://developer.android.com/reference/kotlin/java/lang/Class.html)&lt;[T](observe.html)&gt;): Flow&lt;[T](observe.html)&gt;<br>Observes data stored at a path for any changes. |
| [observeChildren](observe-children.html) | [androidJvm]<br>@ExperimentalCoroutinesApi<br>abstract suspend fun &lt;[T](observe-children.html) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; [observeChildren](observe-children.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), type: [Class](https://developer.android.com/reference/kotlin/java/lang/Class.html)&lt;[T](observe-children.html)&gt;): Flow&lt;[DatabaseEvent](../../io.github.saifkhichi96.android.db.model/-database-event/index.html)&lt;[T](observe-children.html)&gt;&gt;<br>Observes data stored at child nodes of a path for any changes. |
| [remove](remove.html) | [androidJvm]<br>abstract suspend fun [remove](remove.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html))<br>Deletes data stored at a path. |
| [update](update.html) | [androidJvm]<br>abstract suspend fun [update](update.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), value: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html))<br>Updates existing data at a path. |


## Inheritors


| Name |
|---|
| [FirebaseDatabase](../-firebase-database/index.html) |

