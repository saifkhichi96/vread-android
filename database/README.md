---
layout: project
title: Android Database
permalink: /
code: saifkhichi96/android-database
---

# Android Database

This package provides an extensible interface for creating and managing databases in Android apps. This includes both
local and remote databases.

By default, the package provides implementation for a Firebase Realtime Database, and a local SharedPreferences-based
database. More database implementations can be added by extending the `LocalDatabase` and `RemoteDatabase` classes.

## Installation

The compiled package is only available internally via GitHubPackages at the moment, and can be added to your project dependencies (_only if you have internal access_) as follows:

```groovy
dependencies {
    implementation 'io.github.saifkhichi96:android-database:1.0.0'
}
```

In the future, the package would be made publicly available via [Maven Central](https://mvnrepository.com/artifact/io.github.saifkhichi96/android-database).

For now, please clone the repository as a submodule of your project as follows:

```
git submodule add https://github.com/saifkhichi96/android-database database
```

This will import the package as a local module in your project.

Then add the following to your `build.gradle` file:

```groovy
dependencies {
    implementation project(path: ':database')
}
```


## Usage

The package can be used with Hilt to automatically inject the database dependencies where required. To achieve this,
create the following singleton class in your app:

```kotlin
@Module
@InstallIn(ViewModelComponent::class) // ApplicationComponent::class, FragmentComponent::class, etc.
object DatabaseModule {
    @Provides
    fun provideLocalDatabase(context: Context): LocalDatabase {
        return PrefsDatabase(context)  // return the implementation you want to use
    }

    @Provides
    fun provideRemoteDatabase(context: Context): RemoteDatabase {
        return FirebaseDatabase(context)  // return the implementation you want to use
    }
}
```

Then in your ViewModel, for example, use the `@Inject` annotation to inject the database dependencies:

```kotlin
@Inject
lateinit var localDatabase: LocalDatabase

@Inject
lateinit var remoteDatabase: RemoteDatabase
```

It is also possible to use the package without Hilt, in which case you can simply initialize the databases yourself where required.

To get started with the package, see the [LocalDatabase](https://github.com/saifkhichi96/android-database/blob/main/src/main/java/io/github/saifkhichi96/android/db/LocalDatabase.kt) and [RemoteDatabase](https://github.com/saifkhichi96/android-database/blob/main/src/main/java/io/github/saifkhichi96/android/db/RemoteDatabase.kt) classes, or read the [API documentation](./docs/).