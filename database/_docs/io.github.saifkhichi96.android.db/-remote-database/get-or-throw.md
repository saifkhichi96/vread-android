---
title: getOrThrow
---


# getOrThrow



[androidJvm]\
abstract suspend fun &lt;[T](get-or-throw.html) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; [getOrThrow](get-or-throw.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), type: [Class](https://developer.android.com/reference/kotlin/java/lang/Class.html)&lt;[T](get-or-throw.html)&gt;): [T](get-or-throw.html)



Gets data stored at a path.



#### Return



The data stored at the given path.



## Parameters


androidJvm

| | |
|---|---|
| path | The path of the data. |
| type | The class type of the data. |



## Throws


| | |
|---|---|
| [kotlin.Exception](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-exception/index.html) | When no data is stored at given path. |



