---
title: getOrElse
---


# getOrElse



[androidJvm]\
inline suspend fun &lt;[T](get-or-else.html) : [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html)&gt; [getOrElse](get-or-else.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), onFailure: (exception: [Throwable](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-throwable/index.html)) -&gt; [T](get-or-else.html)): [T](get-or-else.html)



Gets data stored at a path.



#### Return



The data stored at given path, or result of [onFailure](get-or-else.html) callback.



## Parameters


androidJvm

| | |
|---|---|
| path | The path of the data. |
| onFailure | The callback to execute if nothing saved at path. |




