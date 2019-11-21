package ru.ddstudio.voicerecording.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.ddstudio.voicerecording.data.database.AppDatabase
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }
}