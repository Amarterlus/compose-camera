package com.dulun.compose.camera.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object PDataStoreModule {
    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.dataStore
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = DataStoreCont.DATA_STORE_NAME
)

object DataStoreCont {
    const val DATA_STORE_NAME = "app"
}
