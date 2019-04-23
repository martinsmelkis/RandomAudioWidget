package org.ecosia.randomaudiowidget

import android.app.Application

// Created by martinsmelkis on 03/04/2019.
/*
A simple Android widget that plays a randomly selected .mp3 file from the device's library.

* Includes a stop and start function

* Displays the elapsed time

* Displays basic metadata of the audio file

* Has the ability to run in the background, allowing for other sounds to be output while continuing to play the .mp3

 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()

    }

}
