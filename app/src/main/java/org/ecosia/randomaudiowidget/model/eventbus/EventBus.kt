package org.ecosia.randomaudiowidget.model.eventbus

/**
 * Description of the contract of a generic EventBus implementation
 */
// Created by martinsmelkis on 03/04/2019.
interface EventBus {

    /**
     * Registers a new Subscribable to this EventBus instance
     */
    fun register(subscriber: Subscriber)

    /**
     * Unregisters a Subscribable from this EventBus instance
     */
    fun unregister(subscriber: Subscriber)

    /**
     * Send the given event in this EventBus implementation to be consumed by interested subscribers
     */
    fun dispatch(event: Event<*>)
}