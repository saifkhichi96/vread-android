---
title: observeChildren
---


# observeChildren



[androidJvm]\




@ExperimentalCoroutinesApi



abstract suspend fun &lt;[T](observe-children.html) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; [observeChildren](observe-children.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), type: [Class](https://developer.android.com/reference/kotlin/java/lang/Class.html)&lt;[T](observe-children.html)&gt;): Flow&lt;[DatabaseEvent](../../io.github.saifkhichi96.android.db.model/-database-event/index.html)&lt;[T](observe-children.html)&gt;&gt;



Observes data stored at child nodes of a path for any changes.



A new [DatabaseEvent](../../io.github.saifkhichi96.android.db.model/-database-event/index.html) is emitted everytime there is a change, including creation of a new child, changing existing child, and removal of a child.



#### Return



A Flow of [DatabaseEvent](../../io.github.saifkhichi96.android.db.model/-database-event/index.html)s, which emits a child object on every update.



## Parameters


androidJvm

| | |
|---|---|
| path | The path of the data to observe. |
| type | The class type of the children. |




