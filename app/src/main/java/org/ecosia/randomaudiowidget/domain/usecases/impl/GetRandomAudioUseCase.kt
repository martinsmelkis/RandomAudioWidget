package org.ecosia.randomaudiowidget.domain.usecases.impl

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log

import org.ecosia.randomaudiowidget.model.usecase.AbstractUseCase
import org.ecosia.randomaudiowidget.model.RandomAudio
import org.ecosia.randomaudiowidget.model.eventbus.Event
import org.ecosia.randomaudiowidget.model.eventbus.EventBusFactory
import org.ecosia.randomaudiowidget.utils.Constants

import java.io.File
import java.util.ArrayList
import java.util.concurrent.ThreadLocalRandom

// Created by martinsmelkis on 03/04/2019.
class GetRandomAudioUseCase(private val ctx: Context) : AbstractUseCase() {

    private fun queryMediaCursor(ctx: Context): Cursor? {
        val mediaColumns = arrayOf(MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DURATION)
        val contentResolver = ctx.contentResolver

        @SuppressLint("Recycle") // Closed in run()
        val mediaCursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                mediaColumns, null, null, null)

        return if (mediaCursor != null && mediaCursor.count > 0) mediaCursor else null
    }

    public override fun run() {
        val c = queryMediaCursor(ctx)
        val e = Event(getRandomAudioFromList(makeRandomAudiosFromCursor(c)),
                Constants.EVENT_GOT_AUDIO_FILE)
        EventBusFactory.bus.dispatch(e)
        c?.close()
    }

    override fun destroy() {
        // nothing
    }

    private fun makeRandomAudiosFromCursor(cursor: Cursor?): List<RandomAudio> {
        val randomAudioList = ArrayList<RandomAudio>()
        if (cursor != null) {
            cursor.moveToFirst()

            while (!cursor.isAfterLast) {

                val path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))

                cursor.moveToNext()
                if (path != null && path.endsWith(".mp3")) {

                    val mediaFile = File(path)

                    try {
                        val title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                        val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))

                        val a = RandomAudio(title, artist, path, mediaFile.length())
                        randomAudioList.add(a)
                    } catch (e: Exception) {
                        Log.e("", e.message)
                        continue
                    }

                }
            }
        }
        return randomAudioList
    }

    private fun getRandomAudioFromList(list: List<RandomAudio>): RandomAudio {
        val random = ThreadLocalRandom.current().nextInt(0, list.size - 1)
        return list[random]
    }

}
