package ru.ddstudio.voicerecording.ui.recording_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import ru.ddstudio.voicerecording.R

class RecordListFragment : Fragment() {

    private lateinit var listVoiceViewModel: RecordListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        listVoiceViewModel =
            ViewModelProviders.of(this).get(RecordListViewModel::class.java)
        val root = inflater.inflate(R.layout.fr_list_record, container, false)
//        val textView: TextView = root.findViewById(R.id.text_gallery)
//        listVoiceViewModel.text.observe(this, Observer {
//            textView.text = it
//        })
        return root
    }
}