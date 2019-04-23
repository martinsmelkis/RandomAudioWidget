package org.ecosia.randomaudiowidget.domain.usecases.impl

import android.media.MediaPlayer

import org.ecosia.randomaudiowidget.model.eventbus.Event
import org.ecosia.randomaudiowidget.model.eventbus.EventBusFactory
import org.ecosia.randomaudiowidget.model.eventbus.Subscriber
import org.ecosia.randomaudiowidget.model.usecase.AbstractUseCase
import org.ecosia.randomaudiowidget.utils.Constants

// Created by martinsmelkis on 03/04/2019.
class StopRandomAudioUseCase(private val player: MediaPlayer) : AbstractUseCase(), Subscriber {

    init {
        EventBusFactory.bus.dispatch(Event("", Constants.EVENT_AUDIO_FILE_STOPPED))
    }

    public override fun run() {
        player.stop()
    }

    override fun destroy() {
        // nothing
    }

    override fun handle(event: Event<*>) {
        // nothing
    }

}