---
title: createChild
---


# createChild



[androidJvm]\
suspend fun [createChild](create-child.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), value: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html))



Saves an object at a new child node of a path in the database.



A timestamp-based unique key is assigned to the child node.



## Parameters


androidJvm

| | |
|---|---|
| path | A path in the database. |
| value | The object to save as a new child. |





[androidJvm]\
suspend fun [createChild](create-child.html)(path: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), childKey: [String](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-string/index.html), value: [Any](https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-any/index.html))



Saves an object at a child node of a path in the database.



If there is an existing child key at this path, its data is overwritten.



## Parameters


androidJvm

| | |
|---|---|
| path | A path in the database. |
| childKey | The key of the child node. |
| value | The object to save at the child node. |




