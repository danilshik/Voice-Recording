package ru.ddstudio.voicerecording.ui.recorder

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.fr_record.*
import org.joda.time.DateTime
import ru.ddstudio.voicerecording.AppDelegate
import ru.ddstudio.voicerecording.MainViewModelFactory
import ru.ddstudio.voicerecording.R
import ru.ddstudio.voicerecording.data.RecorderState
import ru.ddstudio.voicerecording.data.database.entities.RecordEntity
import ru.ddstudio.voicerecording.extensions.toStringTime

import ru.ddstudio.voicerecording.services.RecordingService
import javax.inject.Inject


class RecorderFragment : Fragment(){

    companion object{
        const val TIME_CHANGE = "ru.ddstudio.voicerecording.ui.recorder.TIME_CHANGE"
        const val ADD_RECORD = "ru.ddstudio.voicerecording.ui.recorder.ADD_RECORD"
        const val DELETE_RECORD = "ru.ddstudio.voicerecording.ui.recorder.DELETE_RECORD"
    }
    private val isSavedRecorder = true

    private lateinit var viewModel: RecorderViewModel
    @Inject
    lateinit var viewModelFactory : MainViewModelFactory

    private val PERMISSION_RECORD_AUDIO = 1
    private val PERMISSION_WRITE_FILE = 2
    private lateinit var sharedPreferences : SharedPreferences
    private lateinit var direction: String

    private lateinit var brReceiver : BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppDelegate.appComponent.inject(this)
        initViewModel()
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        direction = context?.getExternalFilesDir(null)!!.absolutePath



        brReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                when(intent.action){
                    TIME_CHANGE -> viewModel.recorderTimeChanged(intent.getLongExtra("timeRecording", 0))
                    ADD_RECORD -> {
                        val recordEntity: RecordEntity = intent.getSerializableExtra("file") as RecordEntity
                        viewModel.saveRecord(recordEntity)
                    }
                    DELETE_RECORD -> {
                        val recordEntity: RecordEntity = intent.getSerializableExtra("file") as RecordEntity
                        viewModel.deleteRecord(recordEntity.filePath, recordEntity.name)
                    }
                }
            }
        }
        val intentFilter = IntentFilter()
        intentFilter.addAction(TIME_CHANGE)
        context!!.registerReceiver(brReceiver, intentFilter)


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fr_record, container, false)
        btn_save_voice.setOnClickListener{viewModel.stopRecording()}
        btn_delete_voice.setOnClickListener{viewModel.stopRecording()}
        fab.setOnClickListener{viewModel.startRecording()}



        return root
    }

    private fun initViewModel(){
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RecorderViewModel::class.java)
        viewModel.getTime().observe(this, Observer { updateTime(it)})
        viewModel.getRecorderState().observe(this, Observer { updateRecorderState(it) })
        viewModel.getOutputFileName().observe(this, Observer { updateOutputFileName(it) })
    }

    private fun updateOutputFileName(outputFileName: String) {
        et_filename.setText(outputFileName)
    }

    private fun updateTime(time : Long){
        tv_time.text = time.toStringTime()
    }




    private fun updateRecorderState(recorderState : RecorderState){
        val intent = Intent(context, RecordingService::class.java)
        when(recorderState){
            RecorderState.PLAY -> {
                if(isPermissionGranted(Manifest.permission.RECORD_AUDIO)&& isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        //PAUSE
                        fab.setOnClickListener { viewModel.pauseRecording() }
                        fab.setImageDrawable(
                            ContextCompat.getDrawable(
                                context!!,
                                R.drawable.ic_pause_white_24dp
                            )
                        )
                        btn_delete_voice.visibility = View.VISIBLE
                    } else {
                        //STOP
                        fab.setOnClickListener {viewModel.stopRecording()}
                            fab.setImageDrawable(ContextCompat.getDrawable(
                                    context!!,
                                    R.drawable.ic_stop_white_24dp
                                )
                            )
                        }

                    btn_save_voice.visibility = View.VISIBLE

                    tv_recorder_state.visibility = View.VISIBLE
                    tv_recorder_state.text = "Запись"
                    et_filename.isEnabled = false
                    Toast.makeText(context, "Запись", Toast.LENGTH_SHORT).show()

                    val outputFileName = if(et_filename.text.toString().trim() == "") {
                        val date = DateTime.now()
                        Log.d("Время", date.toString())
                        date.toString("dd.MM.yyyy-HH:mm:ss") + ".mpeg"
                    } else{
                        et_filename.text.toString().trim() + ".mpeg"
                    }
//                    val outputFile = "$direction/${outputFileName}"
                    intent.putExtra("outputFile", outputFileName)
                    intent.action = RecorderState.PLAY.name

                    intent.putExtra("samplingRate", Integer.parseInt(sharedPreferences.getString("samplingRateKey", "50000").toString()))
                    context?.startService(intent)
                    viewModel.outputFileNameChanged(outputFileName)

                }
                else{
                    if(!isPermissionGranted(Manifest.permission.RECORD_AUDIO))
                        requestPermission(Manifest.permission.RECORD_AUDIO, "Для начала записи звука, нам нужно разрешение записи звука", PERMISSION_RECORD_AUDIO)
                    if(!isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                        requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, "Для начала записи звука, нам нужно разрешение на доступ к файлам", PERMISSION_WRITE_FILE)
                }
            }
            RecorderState.PAUSE -> {
                fab.setOnClickListener{viewModel.resumeRecording()}
                tv_recorder_state.text = "Пауза"
                fab.setImageDrawable(
                    ContextCompat.getDrawable(
                        context!!,
                        R.drawable.ic_record_white_24dp
                    )
                )
                Toast.makeText(context, "Пауза", Toast.LENGTH_SHORT).show()
                intent.action = RecorderState.PAUSE.name
                context!!.startService(intent)
            }
            RecorderState.RESUME -> {
                fab.setOnClickListener{viewModel.pauseRecording()}
                tv_recorder_state.text = "Запись"
                fab.setImageDrawable(
                    ContextCompat.getDrawable(
                        context!!,
                        R.drawable.ic_pause_white_24dp
                    )
                )
                Toast.makeText(context, "Продолжение записи", Toast.LENGTH_SHORT).show()
                intent.action = RecorderState.RESUME.name
                context!!.startService(intent)
            }
            RecorderState.STOP -> {
                tv_recorder_state.visibility = View.INVISIBLE
                fab.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_voice_white_24dp))
                btn_save_voice.visibility = View.INVISIBLE
                btn_delete_voice.visibility = View.INVISIBLE
                et_filename.isEnabled = true
                viewModel.outputFileNameChanged("")
                fab.setOnClickListener{viewModel.startRecording()}
//
//                intent.action = RecorderState.STOP.name
//                context!!.startService(intent)
                intent.action = RecorderState.STOP.name
                intent.putExtra("isSaved", isSavedRecorder)
                context!!.startService(intent)
                Toast.makeText(context, "Стоп", Toast.LENGTH_SHORT).show()
            }
        }
    }

//    private fun startRecorder(){
//        if(isPermissionGranted(Manifest.permission.RECORD_AUDIO)&& isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
////            viewModel.startRecording(et_filename.text.toString().trim())
//            val intent = Intent(context, RecordingService::class.java)
//            val outputFileName = if(et_filename.text.toString().trim() != "") {
//                val date = DateTime.now()
//                Log.d("Время", date.toString())
//                date.toString("dd.MM.yyyy-HH:mm:ss") + ".mpeg"
//            } else{
//                et_filename.text.toString().trim() + ".mpeg"
//            }
//            val outputFile = "$direction/${outputFileName}"
//
//            intent.putExtra("outputFile", outputFile)
//            intent.action = RecorderState.PLAY.name
//            intent.putExtra("samplingRate", Integer.parseInt(sharedPreferences.getString("samplingRateKey", "50000").toString()))
//            context?.startService(intent)
//            viewModel.startRecording()
//
//        }
//        else{
//
//            if(!isPermissionGranted(Manifest.permission.RECORD_AUDIO))
//                requestPermission(Manifest.permission.RECORD_AUDIO, "Для начала записи звука, нам нужно разрешение записи звука", PERMISSION_RECORD_AUDIO)
//            if(!isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE))
//                requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, "Для начала записи звука, нам нужно разрешение на доступ к файлам", PERMISSION_WRITE_FILE)
//        }
//
//    }



//    private fun startPlayer(){
//        Toast.makeText(context, "Воспроизведение начато", Toast.LENGTH_SHORT).show()
//        try{
//            releasePlayer()
//            mediaPlayer = MediaPlayer()
//            mediaPlayer!!.setDataSource(fileName)
//            mediaPlayer!!.prepare()
//            mediaPlayer!!.start()
//        }
//        catch (e : Exception){
//            e.printStackTrace()
//        }
//    }
//
//    private fun stopPlayer(){
//        if(mediaPlayer != null){
//            mediaPlayer!!.stop()
//            Toast.makeText(context, "Воспроизведение окончено", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun releaseRecorder(){
//        if(mediaRecorder != null){
//            mediaRecorder!!.release()
//            mediaRecorder = null
//            Toast.makeText(context, "Запись объект уничтожен", Toast.LENGTH_SHORT).show()
//        }
//    }

    private fun requestPermission(permission: String, message: String, permissionResultCode : Int){
        if(shouldShowRequestPermissionRationale(permission)){
            createAlertDialog(permission, message, permissionResultCode)
        }
        else {
            requestPermissions(arrayOf(permission), permissionResultCode)
        }

    }

    private fun isPermissionGranted(permission: String) : Boolean{
        return ContextCompat.checkSelfPermission(context!!, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun createAlertDialog(permission : String?, message: String, permissionResultCode: Int){
        AlertDialog.Builder(context!!)
            .setMessage(message)
            .setPositiveButton("Понятно"
            ) { _, _ ->
                if(permission != null) {
                    requestPermissions(
                        arrayOf(permission),
                        permissionResultCode
                    )
                }
            }
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults.size != 1) return
        when(requestCode){
            PERMISSION_RECORD_AUDIO -> {
                if(grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    AlertDialog.Builder(context!!)
                        .setMessage("Вы можете дать разрешение в настройках устройства")
                        .setPositiveButton("Понятно", null)
                        .show()
                }
                else{
                    viewModel.startRecording()
                }

            }
            PERMISSION_WRITE_FILE -> {
                if(grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    AlertDialog.Builder(context!!)
                        .setMessage("Вы можете дать разрешение в настройках устройства")
                        .setPositiveButton("Понятно", null)
                        .show()
                }
                else{
                    viewModel.startRecording()
                }
            }
            else -> return
        }



    }

    override fun onDestroy() {
        context?.unregisterReceiver(brReceiver)
        super.onDestroy()
    }

}
