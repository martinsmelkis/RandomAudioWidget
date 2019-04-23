package org.ecosia.randomaudiowidget.domain.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log

import org.ecosia.randomaudiowidget.R
import org.ecosia.randomaudiowidget.model.eventbus.Event
import org.ecosia.randomaudiowidget.model.eventbus.EventBusFactory
import org.ecosia.randomaudiowidget.model.eventbus.Subscriber
import org.ecosia.randomaudiowidget.domain.usecases.impl.GetRandomAudioUseCase
import org.ecosia.randomaudiowidget.domain.usecases.impl.PlayRandomAudioUseCase
import org.ecosia.randomaudiowidget.domain.usecases.impl.StopRandomAudioUseCase
import org.ecosia.randomaudiowidget.model.RandomAudio
import org.ecosia.randomaudiowidget.utils.Constants

import java.io.File

// Created by martinsmelkis on 03/04/2019.
class RandomAudioPlayService : Service(), Subscriber {

    private var player: MediaPlayer? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // check intent extra - coming from e.g. widget
        var action: String? = ""
        if (intent != null) {
            action = intent.getStringExtra(Constants.R_A_SERVICE_ACTION)
        }
        Log.w(javaClass.simpleName, "onstartcommand " + action)
        if (action != null && action == Constants.R_A_SERVICE_ACTION_PLAY) {
            EventBusFactory.bus.register(this)
            GetRandomAudioUseCase(this).run()
            startAsForeground()
        } else if (action != null && action == Constants.R_A_SERVICE_ACTION_STOP) {
            StopRandomAudioUseCase(player!!).run()
            clearNotification()
        }
        return Service.START_NOT_STICKY
    }

    private fun clearNotification() {
        stopForeground(false)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.cancel(Constants.R_A_SERVICE_NOTIFICATION_ID)
    }

    @SuppressLint("WrongConstant")
    private fun startAsForeground() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val notificationChannelId = packageName
        val channelName = packageName
        val chan = NotificationChannel(notificationChannelId, channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE

        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(chan)

        val notificationBuilder = Notification.Builder(this, notificationChannelId)

        val notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(getString(R.string.app_running_in_bg))
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()

        startForeground(Constants.R_A_SERVICE_NOTIFICATION_ID, notification)
    }

    override fun handle(event: Event<*>) {
        if (event.id == Constants.EVENT_GOT_AUDIO_FILE) {
            val a = event.data as RandomAudio
            a.setContext(this)
            if (player != null && player!!.isPlaying) {
                player!!.stop()
                EventBusFactory.bus.dispatch(Event<Any>(null, Constants.EVENT_AUDIO_FILE_STOPPED))
            }
            player = MediaPlayer.create(this, Uri.fromFile(File(a.path)))
            PlayRandomAudioUseCase(player!!, a).run()
        } else if (event.id == Constants.EVENT_AUDIO_FILE_FINISHED || event.id == Constants.EVENT_AUDIO_FILE_STOPPED) {
            clearNotification()
        }
    }

}
