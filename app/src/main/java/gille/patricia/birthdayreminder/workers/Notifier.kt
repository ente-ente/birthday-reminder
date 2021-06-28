package gille.patricia.birthdayreminder.workers

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import gille.patricia.birthdayreminder.R

/**
 * Utility class for posting notifications.
 * This class creates the notification channel (as necessary) and posts to it when requested.
 */
object Notifier {
    private const val channelId = "Default"

    fun init(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                activity.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
            val existingChannel = notificationManager.getNotificationChannel(channelId)
            if (existingChannel == null) {
                // Create the NotificationChannel
                val name = activity.getString(R.string.defaultChannel)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val mChannel = NotificationChannel(channelId, name, importance)
                mChannel.description = activity.getString(R.string.notificationDescription)
                notificationManager.createNotificationChannel(mChannel)
            }
        }
    }

    fun postNotification(
        id: Long,
        context: Context,
        birthdayDetailsIntent: PendingIntent,
        editNotificationIntent: PendingIntent,
        message: String
    ) {
        val builder = NotificationCompat.Builder(context, channelId)
        builder.setContentTitle(context.getString(R.string.deepLinkNotificationTitle))
            .setSmallIcon(R.drawable.ic_baseline_alarm_on_24)
        val notification = builder.setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(birthdayDetailsIntent)
            .setAutoCancel(true)
            .addAction(
                R.drawable.ic_baseline_alarm_on_24, context.getString(R.string.edit_notification),
                editNotificationIntent
            )
            .build()
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(id.toInt(), notification)
    }
}
