// Location: app/src/main/java/com/sscprephub/app/di/AppModule.kt
package com.sscprephub.app.di

import android.content.Context
import androidx.room.Room
import com.sscprephub.app.data.local.dao.PrepDao
import com.sscprephub.app.data.local.database.AppDatabase
import com.sscprephub.app.data.local.database.AppDatabaseCallback
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope {
        // Creates a scope tied directly to the lifetime of the App application process
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

    @Provides
    @Singleton
    fun provideDatabaseCallback(
        databaseProvider: Provider<AppDatabase>,
        applicationScope: CoroutineScope
    ): AppDatabaseCallback {
        return AppDatabaseCallback(databaseProvider, applicationScope)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        callback: AppDatabaseCallback
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "ssc_prep_hub.db"
        )
        .addCallback(callback)
        .fallbackToDestructiveMigration() // Safely clears db locally if entity structures change during development
        .build()
    }

    @Provides
    @Singleton
    fun providePrepDao(database: AppDatabase): PrepDao {
        return database.prepDao()
    }
}
