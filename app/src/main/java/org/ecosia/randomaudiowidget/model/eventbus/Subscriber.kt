package org.ecosia.randomaudiowidget.model.eventbus

// Created by martinsmelkis on 03/04/2019.
interface Subscriber {

    /**
     * Consume the event dispatched by the bus
     */
    fun handle(event: Event<*>)

}
