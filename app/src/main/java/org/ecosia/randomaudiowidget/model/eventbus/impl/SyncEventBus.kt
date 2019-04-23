package org.ecosia.randomaudiowidget.model.eventbus.impl

import org.ecosia.randomaudiowidget.model.eventbus.Event
import org.ecosia.randomaudiowidget.model.eventbus.EventBus
import org.ecosia.randomaudiowidget.model.eventbus.Subscriber

import java.util.ArrayList

// Created by martinsmelkis on 03/04/2019.
class SyncEventBus : EventBus {

    private val subscribers: MutableList<Subscriber>

    init {
        subscribers = ArrayList()
    }

    override fun register(subscriber: Subscriber) {
        synchronized(this) {
            unregister(subscriber)
            subscribers.add(subscriber)
        }
    }

    override fun unregister(subscriber: Subscriber) {
        synchronized(this) {
            subscribers.remove(subscriber)
        }
    }

    override fun dispatch(event: Event<*>) {
        for (s in ArrayList(subscribers)) {
            s.handle(event)
        }
    }

}
