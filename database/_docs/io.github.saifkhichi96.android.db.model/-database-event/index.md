---
title: DatabaseEvent
---


# DatabaseEvent



[androidJvm]\
sealed class [DatabaseEvent](index.html)&lt;[T](index.html)&gt;

A class that contains the data of the event that is triggered by the database.



#### Author



saifkhichi96



## Parameters


androidJvm

| | |
|---|---|
|  | <T> The type of the data that is stored in the event. |



## Types


| Name | Summary |
|---|---|
| [Cancelled](-cancelled/index.html) | [androidJvm]<br>data class [Cancelled](-cancelled/index.html)&lt;[T](-cancelled/index.html)&gt;(error: DatabaseError) : [DatabaseEvent](index.html)&lt;[T](-cancelled/index.html)&gt; <br>Event that is triggered when an error occurs. |
| [Changed](-changed/index.html) | [androidJvm]<br>data class [Changed](-changed/index.html)&lt;[T](-changed/index.html)&gt;(item: [T](-changed/index.html)) : [DatabaseEvent](index.html)&lt;[T](-changed/index.html)&gt; <br>Event that is triggered when data is updated in the database. |
| [Created](-created/index.html) | [androidJvm]<br>data class [Created](-created/index.html)&lt;[T](-created/index.html)&gt;(item: [T](-created/index.html)) : [DatabaseEvent](index.html)&lt;[T](-created/index.html)&gt; <br>Event that is triggered when new data is added to the database. |
| [Deleted](-deleted/index.html) | [androidJvm]<br>data class [Deleted](-deleted/index.html)&lt;[T](-deleted/index.html)&gt;(item: [T](-deleted/index.html)) : [DatabaseEvent](index.html)&lt;[T](-deleted/index.html)&gt; <br>Event that is triggered when data is removed from the database. |


## Inheritors


| Name |
|---|
| [Created](-created/index.html) |
| [Changed](-changed/index.html) |
| [Deleted](-deleted/index.html) |
| [Cancelled](-cancelled/index.html) |

