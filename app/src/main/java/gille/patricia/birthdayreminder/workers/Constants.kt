@file:JvmName("Constants")

package gille.patricia.birthdayreminder.workers

import java.time.LocalTime

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
const val KEY_NOTIFICATION_IDS = "notificationIds"
const val KEY_NUMBER_OF_NOTIFICATIONS = "numberOfNotifications"
const val KEY_BIRTHDAY_IDS = "birthdayIds"
const val BIRTHDAY_NOTIFICATION_WORK_NAME = "Notify current birthdays"
val birthdayTime = LocalTime.of(0, 0, 0, 0)