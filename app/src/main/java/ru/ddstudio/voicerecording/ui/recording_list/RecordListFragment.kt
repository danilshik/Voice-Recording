package ru.ddstudio.voicerecording.ui.recording_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fr_list_record.*
import ru.ddstudio.voicerecording.R
import android.graphics.drawable.InsetDrawable
import android.util.Log
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fr_list_record.view.*
import ru.ddstudio.voicerecording.AppDelegate
import ru.ddstudio.voicerecording.MainViewModelFactory
import ru.ddstudio.voicerecording.extensions.dp
import javax.inject.Inject

class RecordListFragment : Fragment() {

    private lateinit var viewModel: RecordListViewModel
    @Inject
    lateinit var viewModelFactory : MainViewModelFactory
    private lateinit var recordListAdapter: RecordListAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppDelegate.appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fr_list_record, container, false)
//        val textView: TextView = root.findViewById(R.id.text_gallery)
//        listVoiceViewModel.text.observe(this, Observer {
//            textView.text = it
//        })
        initViews(root)
        initViewModel()
        return root
    }

    private fun initViews(root : View){
        recordListAdapter = RecordListAdapter {
            Snackbar.make(rv_record_list, "Click on ${it.name}", Snackbar.LENGTH_SHORT)
                .show()
        }

//        val customDivider = resources.getDrawable(R.drawable.divider_item_list, context?.theme)
//        val customDividerWithMargin = InsetDrawable(customDivider, 72.dp, 0, 0 , 0)
        val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
//        divider.setDrawable(customDividerWithMargin)

        with(root.rv_record_list){
            adapter = recordListAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(divider)
        }

        val navController = NavHostFragment.findNavController(this)

        root.fab.setOnClickListener{navController.navigate(R.id.nav_record)}
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RecordListViewModel::class.java)
        viewModel.getAllRecords().observe(this, Observer { recordListAdapter.updateData(it) })
    }

}