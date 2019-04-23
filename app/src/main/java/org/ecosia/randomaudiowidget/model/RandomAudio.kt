package org.ecosia.randomaudiowidget.model

import android.content.Context
import java.util.Locale

// Created by martinsmelkis on 03/04/2019.
class RandomAudio(private val title: String, private val artist: String, val path: String, private val size: Long) {
    private var position: Long = 0
    private var ctx: Context? = null

    private val readableProgress: String
        get() {
            val minutes = (position / (60 * 1000)).toInt()
            val seconds = (position / 1000 % 60).toInt()
            return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
        }

    private fun getSize(): String {
        val unit = 1024
        if (size < unit) return "$size B"
        val exp = (Math.log(size.toDouble()) / Math.log(unit.toDouble())).toInt()
        val pre = "KMGTPE"[exp - 1] + "i"
        return String.format(Locale.getDefault(), "%.1f %sB", size / Math.pow(unit.toDouble(), exp.toDouble()), pre)
    }

    fun setPosition(p: Long) {
        this.position = p
    }

    fun setContext(ctx: Context) {
        this.ctx = ctx
    }

    fun getContext(): Context? {
        return ctx
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append(readableProgress).append("\n")
        sb.append(artist)
        sb.append(if (artist.isEmpty()) "" else " - ").append(title).append("\n")
        sb.append(getSize())
        return sb.toString()
    }

}
