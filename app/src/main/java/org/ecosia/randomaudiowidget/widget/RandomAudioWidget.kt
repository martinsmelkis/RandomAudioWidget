package org.ecosia.randomaudiowidget.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews

import org.ecosia.randomaudiowidget.PermissionActivity
import org.ecosia.randomaudiowidget.R
import org.ecosia.randomaudiowidget.domain.service.RandomAudioPlayService
import org.ecosia.randomaudiowidget.model.RandomAudio
import org.ecosia.randomaudiowidget.model.eventbus.Event
import org.ecosia.randomaudiowidget.model.eventbus.EventBusFactory
import org.ecosia.randomaudiowidget.model.eventbus.Subscriber
import org.ecosia.randomaudiowidget.utils.Constants

class RandomAudioWidget : AppWidgetProvider(), Subscriber {

    private var progressText = ""

    override fun onUpdate(ctx: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {

        optAskStoragePermission(ctx)
        startPlayerService(ctx)

        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(ctx, appWidgetManager, appWidgetId)
            val views = RemoteViews(ctx.packageName, R.layout.random_audio_widget)
            val intent = Intent(ctx, RandomAudioPlayService::class.java)
            intent.putExtra(Constants.R_A_SERVICE_ACTION, Constants.R_A_SERVICE_ACTION_PLAY)
            val pendingIntentStart = PendingIntent.getService(ctx, 12, intent, 0)
            intent.putExtra(Constants.R_A_SERVICE_ACTION, Constants.R_A_SERVICE_ACTION_STOP)
            val pendingIntentStop = PendingIntent.getService(ctx, 18, intent, 0)
            views.setOnClickPendingIntent(R.id.btnPlayClick, pendingIntentStart)
            views.setOnClickPendingIntent(R.id.btnStopClick, pendingIntentStop)

            views.setTextViewText(R.id.appwidget_text, progressText)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        EventBusFactory.bus.register(this)
    }

    private fun optAskStoragePermission(ctx: Context) {
        val i = Intent(ctx, PermissionActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        ctx.startActivity(i)
    }

    private fun startPlayerService(ctx: Context) {
        try {
            ctx.startService(Intent(ctx, RandomAudioPlayService::class.java))
        } catch (ex: IllegalStateException) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ctx.startForegroundService(Intent(ctx, RandomAudioPlayService::class.java))
            } else {
                ctx.startService(Intent(ctx, RandomAudioPlayService::class.java))
            }
        }
    }

    // Called e.g. on reboot, but can take a minute
    override fun onEnabled(context: Context) {
        optAskStoragePermission(context)
        val ids = AppWidgetManager.getInstance(
                context).getAppWidgetIds(ComponentName(context, RandomAudioWidget::class.java))
        onUpdate(context, AppWidgetManager.getInstance(context), ids)
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun handle(event: Event<*>) {
        if (event.id == Constants.EVENT_PROGRESS_AUDIO_FILE) {
            progressText = event.data.toString()
            val ctx = (event.data as RandomAudio).getContext()
            if (ctx == null) {
                Log.w(javaClass.simpleName, "handle Widget event - Context is null")
                return
            }
            val ids = AppWidgetManager.getInstance(
                    ctx).getAppWidgetIds(ComponentName(ctx, RandomAudioWidget::class.java))
            onUpdate(ctx, AppWidgetManager.getInstance(ctx), ids)
        }
    }

    companion object {

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {

            val views = RemoteViews(context.packageName, R.layout.random_audio_widget)

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

}
