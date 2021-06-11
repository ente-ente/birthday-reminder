@file:JvmName("Constants")

package gille.patricia.birthdayreminder.workers

// Notification Channel constants

// Name of Notification Channel for birthday notifications
@JvmField
val BIRTHDAY_NOTIFICATION_CHANNEL_NAME: CharSequence =
    "Birthday Notifications"
const val BIRTHDAY_NOTIFICATION_CHANNEL_DESCRIPTION =
    "Shows a notification for each notification which is found in database for the current date"
@JvmField
val NOTIFICATION_TITLE: CharSequence = "Birthday Alarm"
const val CHANNEL_ID = "BIRTHDAY_NOTIFICATION"
const val NOTIFICATION_ID = 1
