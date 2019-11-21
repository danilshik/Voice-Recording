package ru.ddstudio.voicerecording

import android.app.Application
import ru.ddstudio.voicerecording.di.components.AppComponent
import ru.ddstudio.voicerecording.di.components.DaggerAppComponent
import ru.ddstudio.voicerecording.di.modules.AppModule
import ru.ddstudio.voicerecording.di.modules.DatabaseModule


class AppDelegate : Application() {

    companion object{
        lateinit var appComponent: AppComponent
    }
    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent
            .builder()
            .appModule(AppModule(this))
            .databaseModule(DatabaseModule())
            .build()

    }

}