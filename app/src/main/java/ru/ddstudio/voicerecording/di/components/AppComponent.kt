package ru.ddstudio.voicerecording.di.components



import dagger.Component
import ru.ddstudio.voicerecording.di.modules.AppModule
import ru.ddstudio.voicerecording.di.modules.DatabaseModule
import ru.ddstudio.voicerecording.ui.recorder.RecorderFragment
import javax.inject.Singleton


@Singleton
@Component(modules = [AppModule::class, DatabaseModule::class])
public interface AppComponent{
    fun inject(recorderFragment: RecorderFragment)
}