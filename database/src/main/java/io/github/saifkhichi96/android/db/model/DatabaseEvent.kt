package io.github.saifkhichi96.android.db.model

import com.google.firebase.database.DatabaseError

/**
 * A class that contains the data of the event that is triggered by the database.
 *
 * @param <T> The type of the data that is stored in the event.
 * @author saifkhichi96
 */
sealed class DatabaseEvent<T> {

    /**
     * Event that is triggered when new data is added to the database.
     */
    data class Created<T>(val item: T) : DatabaseEvent<T>()

    /**
     * Event that is triggered when data is updated in the database.
     */
    data class Changed<T>(val item: T) : DatabaseEvent<T>()

    /**
     * Event that is triggered when data is removed from the database.
     */
    data class Deleted<T>(val item: T) : DatabaseEvent<T>()

    /**
     * Event that is triggered when an error occurs.
     */
    data class Cancelled<T>(val error: DatabaseError) : DatabaseEvent<T>()

}