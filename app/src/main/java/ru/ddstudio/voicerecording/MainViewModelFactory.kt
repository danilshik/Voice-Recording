package ru.ddstudio.voicerecording


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.ddstudio.voicerecording.repositories.RecordRepository
import ru.ddstudio.voicerecording.ui.recorder.RecorderViewModel
import javax.inject.Inject

class MainViewModelFactory @Inject constructor(private val recordRepository : RecordRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(RecorderViewModel::class.java)){
            return RecorderViewModel(recordRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class. See MainViewModelFactory.kt")
    }
}
