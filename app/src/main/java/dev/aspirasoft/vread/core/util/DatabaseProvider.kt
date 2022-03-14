package dev.aspirasoft.vread.core.util

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.saifkhichi96.android.db.FirebaseDatabase
import io.github.saifkhichi96.android.db.LocalDatabase
import io.github.saifkhichi96.android.db.PrefsDatabase
import io.github.saifkhichi96.android.db.RemoteDatabase

/**
 * A module which informs the Hilt which databases to use when injecting dependencies.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
@Module
@InstallIn(ActivityComponent::class, FragmentComponent::class, ViewModelComponent::class)
object DatabaseProvider {

    @Provides
    fun provideRemoteDb(@ApplicationContext context: Context): RemoteDatabase {
        return FirebaseDatabase(context)
    }

    @Provides
    fun provideLocalDb(@ApplicationContext context: Context): LocalDatabase {
        return PrefsDatabase(context)
    }

}