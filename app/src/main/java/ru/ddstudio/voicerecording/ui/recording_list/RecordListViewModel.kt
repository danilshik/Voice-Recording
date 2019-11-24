package ru.ddstudio.voicerecording.ui.recording_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.ddstudio.voicerecording.data.database.entities.RecordEntity
import ru.ddstudio.voicerecording.repositories.RecordRepository
import javax.inject.Inject

class RecordListViewModel @Inject constructor(private val repository: RecordRepository): ViewModel() {
    private val records: LiveData<List<RecordEntity>> = repository.getAllRecording()

    fun getAllRecords() = records
}