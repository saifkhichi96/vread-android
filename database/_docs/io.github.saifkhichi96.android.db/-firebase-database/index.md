---
title: FirebaseDatabase
---


# FirebaseDatabase



[androidJvm]\
class [FirebaseDatabase](index.html)@Injectconstructor(context: [Context](https://developer.android.com/reference/kotlin/android/content/Context.html), persistent: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)?) : [RemoteDatabase](../-remote-database/index.html)

Implements the [RemoteDatabase](../-remote-database/index.html) with Firebase Realtime Database.



#### Author



saifkhichi96



#### Since



1.0.0



## See also


androidJvm

| | |
|---|---|
| [io.github.saifkhichi96.android.db.RemoteDatabase](../-remote-database/index.html) |  |



## Parameters


androidJvm

| | |
|---|---|
| context | The application context. |
| persistent | Whether the database should be persistent. If true, a local cache will be used to     speed up the database operations. If false, persistence will be disabled. If null,     no changes to the default persistence behavior of Firebase will be made. Defaults to     null. |



## Constructors


| | |
|---|---|
| [FirebaseDatabase](-firebase-database.html) | [androidJvm]<br>@Inject<br>fun [FirebaseDatabase](-firebase-database.html)(context: [Context](https://developer.android.com/reference/kotlin/android/content/Context.html), persistent: [Boolean](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-boolean/index.html)? = null)<br>Creates a new instance of [FirebaseDatabase](index.html). |


## Functions


| Name | Summary |
|---|---|
| [clear](clear.html) | [androidJvm]<br>open suspend override fun [clear](clear.html)()<br>Deletes all data from database. |
| [create](create.html) | [androidJvm]<br>open suspend override fun [create](create.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), value: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html))<br>Saves an object at a path in the database. |
| [createChild](../-remote-database/create-child.html) | [androidJvm]<br>suspend fun [createChild](../-remote-database/create-child.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), value: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html))<br>Saves an object at a new child node of a path in the database.<br>[androidJvm]<br>suspend fun [createChild](../-remote-database/create-child.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), childKey: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), value: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html))<br>Saves an object at a child node of a path in the database. |
| [createEmptyChild](create-empty-child.html) | [androidJvm]<br>open override fun [createEmptyChild](create-empty-child.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)<br>Adds a placeholder for a child node at a path. |
| [getOrDefault](../-remote-database/get-or-default.html) | [androidJvm]<br>inline suspend fun &lt;[T](../-remote-database/get-or-default.html) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; [getOrDefault](../-remote-database/get-or-default.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), defValue: [T](../-remote-database/get-or-default.html)): [T](../-remote-database/get-or-default.html)<br>Gets data stored at a path. |
| [getOrElse](../-remote-database/get-or-else.html) | [androidJvm]<br>inline suspend fun &lt;[T](../-remote-database/get-or-else.html) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; [getOrElse](../-remote-database/get-or-else.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), onFailure: (exception: [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)) -&gt; [T](../-remote-database/get-or-else.html)): [T](../-remote-database/get-or-else.html)<br>Gets data stored at a path. |
| [getOrNull](../-remote-database/get-or-null.html) | [androidJvm]<br>inline suspend fun &lt;[T](../-remote-database/get-or-null.html) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; [getOrNull](../-remote-database/get-or-null.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)): [T](../-remote-database/get-or-null.html)?<br>Gets data stored at a path. |
| [getOrThrow](get-or-throw.html) | [androidJvm]<br>open suspend override fun &lt;[T](get-or-throw.html) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; [getOrThrow](get-or-throw.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), type: [Class](https://developer.android.com/reference/kotlin/java/lang/Class.html)&lt;[T](get-or-throw.html)&gt;): [T](get-or-throw.html)<br>Gets data stored at a path. |
| [list](list.html) | [androidJvm]<br>open suspend override fun &lt;[T](list.html) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; [list](list.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), type: [Class](https://developer.android.com/reference/kotlin/java/lang/Class.html)&lt;[T](list.html)&gt;, filter: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?, equalTo: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html)?): [List](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.collections/-list/index.html)&lt;[T](list.html)&gt;<br>Gets list of objects stored at a path. |
| [observe](observe.html) | [androidJvm]<br>@ExperimentalCoroutinesApi<br>open suspend override fun &lt;[T](observe.html) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; [observe](observe.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), type: [Class](https://developer.android.com/reference/kotlin/java/lang/Class.html)&lt;[T](observe.html)&gt;): Flow&lt;[T](observe.html)&gt;<br>Observes data stored at a path for any changes. |
| [observeChildren](observe-children.html) | [androidJvm]<br>@ExperimentalCoroutinesApi<br>open suspend override fun &lt;[T](observe-children.html) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; [observeChildren](observe-children.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), type: [Class](https://developer.android.com/reference/kotlin/java/lang/Class.html)&lt;[T](observe-children.html)&gt;): Flow&lt;[DatabaseEvent](../../io.github.saifkhichi96.android.db.model/-database-event/index.html)&lt;[T](observe-children.html)&gt;&gt;<br>Observes data stored at child nodes of a path for any changes. |
| [remove](remove.html) | [androidJvm]<br>open suspend override fun [remove](remove.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html))<br>Deletes data stored at a path. |
| [update](update.html) | [androidJvm]<br>open suspend override fun [update](update.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), value: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html))<br>Updates existing data at a path. |


## Properties


| Name | Summary |
|---|---|
| [context](context.html) | [androidJvm]<br>val [context](context.html): [Context](https://developer.android.com/reference/kotlin/android/content/Context.html) |

