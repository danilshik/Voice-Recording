package ru.ddstudio.voicerecording.ui.recorder


import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.ddstudio.voicerecording.data.RecorderState
import ru.ddstudio.voicerecording.data.database.entities.RecordEntity
import ru.ddstudio.voicerecording.repositories.RecordRepository
import javax.inject.Inject

class RecorderViewModel @Inject constructor(private val repository: RecordRepository): ViewModel() {
    private val recorderState = MutableLiveData<RecorderState>()
    private val recordingTime = MutableLiveData<Long>()
    private val outputFileName = MutableLiveData<String>()

    fun startRecording() {
        recorderState.value = RecorderState.PLAY
        Log.d("ViewModel", "Play")
    }

    fun stopRecording() {
        recorderState.value = RecorderState.STOP
        Log.d("ViewModel", "Stop")

    }

    fun pauseRecording() {
        recorderState.value = RecorderState.PAUSE
        Log.d("ViewModel", "Pause")
    }

    fun resumeRecording() {
        recorderState.value = RecorderState.RESUME
        Log.d("ViewModel", "Resume")
    }

    fun saveRecord(recordEntity: RecordEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addRecording(recordEntity)
        }
    }

    fun getTime(): LiveData<Long> = recordingTime

    fun getRecorderState(): LiveData<RecorderState> = recorderState

    fun getOutputFileName(): LiveData<String> = outputFileName

    fun recorderTimeChanged(time: Long) {
        recordingTime.value = time
    }

    fun outputFileNameChanged(filename: String?) {
        outputFileName.value = filename
    }


    fun deleteRecord(filePath: String, name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFile(filePath, name)
        }

    }
}
