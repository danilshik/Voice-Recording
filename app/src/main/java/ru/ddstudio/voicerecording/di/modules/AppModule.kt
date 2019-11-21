package ru.ddstudio.voicerecording.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.ddstudio.voicerecording.AppDelegate
import ru.ddstudio.voicerecording.MainViewModelFactory
import ru.ddstudio.voicerecording.repositories.RecordRepository
import javax.inject.Singleton

@Module
class AppModule(private val mApp: AppDelegate){

    @Provides
    @Singleton
    fun provideContext(): Context = mApp

    @Provides
    @Singleton
    fun provideViewModelFactory(repository: RecordRepository) : MainViewModelFactory {
        return MainViewModelFactory(repository)
    }


}