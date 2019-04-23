package org.ecosia.randomaudiowidget.domain.usecases.impl

import android.media.MediaPlayer
import android.os.Handler

import org.ecosia.randomaudiowidget.model.eventbus.Event
import org.ecosia.randomaudiowidget.model.eventbus.EventBusFactory
import org.ecosia.randomaudiowidget.model.eventbus.Subscriber
import org.ecosia.randomaudiowidget.model.usecase.AbstractUseCase
import org.ecosia.randomaudiowidget.model.RandomAudio
import org.ecosia.randomaudiowidget.utils.Constants

import java.util.concurrent.atomic.AtomicBoolean

// Created by martinsmelkis on 03/04/2019.
class PlayRandomAudioUseCase(private val player: MediaPlayer, private val audio: RandomAudio) : AbstractUseCase(), Subscriber {
    private var observer: MediaObserver? = null
    private var playBackUpdater: Handler? = null

    public override fun run() {

        player.start()
        playBackUpdater = Handler()
        observer = MediaObserver()

        player.setOnPreparedListener { playBackUpdater!!.postDelayed(observer, 1000) }
        player.setOnCompletionListener { EventBusFactory.bus.dispatch(Event(audio, Constants.EVENT_AUDIO_FILE_FINISHED)) }
    }

    override fun destroy() {
        if (observer != null) {
            observer!!.stop()
        }
    }

    override fun handle(event: Event<*>) {
        if (event.id == Constants.EVENT_AUDIO_FILE_STOPPED) {
            destroy()
        }
    }

    private inner class MediaObserver internal constructor() : Runnable {

        private val stop = AtomicBoolean(false)

        init {
            stop.set(false)
        }

        internal fun stop() {
            stop.set(true)
        }

        override fun run() {
            if (stop.get()) {
                return
            }
            if (player.isPlaying) {
                audio.setPosition(player.currentPosition.toLong())
                EventBusFactory.bus.dispatch(Event(audio, Constants.EVENT_PROGRESS_AUDIO_FILE))
            }
            playBackUpdater!!.postDelayed(observer, 1000)
        }
    }

}
