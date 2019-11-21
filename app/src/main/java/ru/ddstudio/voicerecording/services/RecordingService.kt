package ru.ddstudio.voicerecording.services


import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import org.joda.time.DateTime
import org.joda.time.Duration
import ru.ddstudio.voicerecording.R
import ru.ddstudio.voicerecording.data.RecorderState
import ru.ddstudio.voicerecording.data.database.entities.RecordEntity
import ru.ddstudio.voicerecording.extensions.toStringTime
import ru.ddstudio.voicerecording.ui.recorder.RecorderFragment
import java.util.*


class RecordingService : Service() {

    private val TAG = "RecordingService"

    private var samplingRate : Int? = null

    private lateinit var pathFolder: String
    private var outputFile : String? = null
    private var outputFileName : String? = null


    private var mediaRecorder: MediaRecorder? = null
    private var startTimeRecording : Long = 0
    private var recordingTimeMillies : Long = 0

    private var timer: Timer? = null
    private val intentTimeRecording = Intent(RecorderFragment.TIME_CHANGE)


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        pathFolder =  applicationContext.getExternalFilesDir(null)!!.absolutePath
        Toast.makeText(this, "Служба создана", Toast.LENGTH_SHORT).show()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            RecorderState.PLAY.name -> {
                samplingRate = intent.getIntExtra("samplingRate", 41000)
                outputFileName = intent.getStringExtra("outputFile")
                outputFile = "${pathFolder}/${outputFileName}"
                startRecording()
            }

            RecorderState.PAUSE.name -> {
                pauseRecording()
            }
            RecorderState.RESUME.name -> {
                resumeRecording()
            }
            RecorderState.STOP.name -> {
                stopRecording(intent.getBooleanExtra("isSaved", true))
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        if(mediaRecorder != null){
            stopRecording()
        }
        super.onDestroy()

    }


    private fun startRecording(){

        Log.d(TAG, "samplingRate ${samplingRate.toString()}")
        mediaRecorder = MediaRecorder()
        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder?.setAudioChannels(1)
        mediaRecorder?.setAudioSamplingRate(samplingRate!!)
        mediaRecorder?.setOutputFile(outputFile)
        Log.d(TAG, "samplingRate ${samplingRate.toString()}")
        Log.d(TAG, "outputFile ${outputFile.toString()}")

        //try
        mediaRecorder!!.prepare()
        mediaRecorder!!.start()
        Log.d(TAG, "Start")
        startTimer()
        startTimeRecording = System.currentTimeMillis()

        startForeground(1, createNotification())





    }

    private fun stopRecording(isSaved : Boolean = true){
        mediaRecorder?.stop()
        val durationTimeMilles = Duration.millis(System.currentTimeMillis() - startTimeRecording)
        mediaRecorder?.reset()
        mediaRecorder?.release()
        stopTimer()
        resetTimer()
        mediaRecorder = null

        val createdDate = DateTime.now()

        Log.d(TAG, "STOP")
        Log.d("OutputFileName", outputFileName!!)
        Log.d("OutputFile", outputFile!!)
        Log.d("CreatedDate", createdDate.toString())
        Log.d("Duration", durationTimeMilles.toString())

        val recordEntity = RecordEntity(0, outputFileName!!, outputFile!!, durationTimeMilles, createdDate)

        val intent = if(isSaved)
            Intent(RecorderFragment.ADD_RECORD)
        else
            Intent(RecorderFragment.DELETE_RECORD)

        intent.putExtra("file", recordEntity)
        sendBroadcast(intent)

        
        stopForeground(true)
    }



    @RequiresApi(Build.VERSION_CODES.N)
    private fun pauseRecording() {
        pauseTimer()
        mediaRecorder?.pause()
        Log.d(TAG, "PAUSED")
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun resumeRecording(){
        mediaRecorder?.resume()
        startTimer()
        Log.d(TAG, "RESUME - PLAY")
    }

    private fun startTimer(){
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask(){
            override fun run() {
                recordingTimeMillies += 1000
                Log.d(TAG, recordingTimeMillies.toString())
                intentTimeRecording.putExtra("timeRecording", recordingTimeMillies)
                sendBroadcast(intentTimeRecording)


            }

        }, 1000, 1000)

    }

    private fun stopTimer(){
        timer?.cancel()

    }

    private fun pauseTimer(){
        stopTimer()
        timer = null
    }

    private fun resetTimer(){
        recordingTimeMillies = 0
        intentTimeRecording.putExtra("timeRecording", recordingTimeMillies)
        sendBroadcast(intentTimeRecording)
    }

    private fun createNotification(): Notification {
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = createNotificationChannel("my_service", "My Background Service")
            NotificationCompat.Builder(this, channelId)
                .setContentTitle("Запись звука....")
                .setContentText(recordingTimeMillies.toStringTime())
                .setOngoing(true)
        } else{
            NotificationCompat.Builder(this)
                .setContentTitle("Запись звука....")
                .setContentText(recordingTimeMillies.toStringTime())
                .setOngoing(true)
        }

        builder.setContentIntent(
            PendingIntent.getActivities(
                applicationContext, 0,
                arrayOf(Intent(applicationContext, RecorderFragment::class.java)), 0
            )
        )

        val stopIntent = Intent(applicationContext, RecordingService::class.java)
        stopIntent.action = "STOP_RECORDING"
        builder.addAction(
            R.drawable.ic_stop_black_24dp, "Stop", PendingIntent.getService(
                applicationContext, 0, stopIntent, 0
            )
        )
        return builder.build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String{
        val chan = NotificationChannel(channelId,
            channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

}
