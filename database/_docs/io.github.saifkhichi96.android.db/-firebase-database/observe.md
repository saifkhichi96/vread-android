---
title: observe
---


# observe



[androidJvm]\




@ExperimentalCoroutinesApi



open suspend override fun &lt;[T](observe.html) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; [observe](observe.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), type: [Class](https://developer.android.com/reference/kotlin/java/lang/Class.html)&lt;[T](observe.html)&gt;): Flow&lt;[T](observe.html)&gt;



Observes data stored at a path for any changes.



A new object is emitted everytime there is a change.



#### Return



A Flow of the objects, which emits a new object on every update.



## Parameters


androidJvm

| | |
|---|---|
| path | The path of the data to observe. |
| type | The class type of the data. |




