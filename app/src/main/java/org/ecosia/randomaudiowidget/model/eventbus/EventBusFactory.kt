package org.ecosia.randomaudiowidget.model.eventbus

import org.ecosia.randomaudiowidget.model.eventbus.impl.SyncEventBus

// Created by martinsmelkis on 03/04/2019.
object EventBusFactory {

    /**
     * Creates and returns an SyncEventBus, the instance is held inside an enumeration, to make it a
     * singleton inside the vm
     */
    val bus: EventBus
        @Synchronized get() = EventBusHolder.SYNC_INSTANCE.bus

    private enum class EventBusHolder private constructor(val bus: EventBus) {

        SYNC_INSTANCE(SyncEventBus())
    }

}
