package com.boswelja.quickmessage.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import com.boswelja.quickmessage.R
import com.boswelja.quickmessage.ActionService

class ColoredBG : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray?
    ) {
        appWidgetIds?.forEach { appWidgetId ->
            val sendMessagePendingIntent = Intent(context, ActionService::class.java).apply {
                action =
                    ActionService.ACTION_SEND_MESSAGE
            }.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    PendingIntent.getForegroundService(context, 0, it, 0)
                } else {
                    PendingIntent.getService(context, 0, it, 0)
                }
            }

            val remoteViews = RemoteViews(context.packageName,
                R.layout.widget_quick_message_coloredbg
            ).apply {
                setOnClickPendingIntent(R.id.button, sendMessagePendingIntent)
            }

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
        }
    }
}