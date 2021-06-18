package gille.patricia.birthdayreminder.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import gille.patricia.birthdayreminder.R
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class WorkerUtils {
    fun makeBirthdayNotificationsForToday(messages: List<String>, context: Context) {
        // Make a channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            val name = BIRTHDAY_NOTIFICATION_CHANNEL_NAME
            val description = BIRTHDAY_NOTIFICATION_CHANNEL_DESCRIPTION
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description

            // Add the channel
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

            notificationManager?.createNotificationChannel(channel)
        }
        var id = -1
        for (message in messages) {
            // Create the notification
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(NOTIFICATION_TITLE)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(LongArray(0))
            id += 1
            // Show the notification
            NotificationManagerCompat.from(context).notify(id, builder.build())
        }
    }

    fun getDaysUntilBirthday(day: Int, month: Int): Int {
        val currentDate: LocalDate = LocalDate.now()
        val birthday: LocalDate = LocalDate.of(currentDate.year, month, day)
        val days = ChronoUnit.DAYS.between(currentDate, birthday)
        if (days < 0) {
            return ChronoUnit.DAYS.between(currentDate, birthday.plusYears(1L)).toInt()
        }
        return days.toInt()
    }

}