package gille.patricia.birthdayreminder.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import gille.patricia.birthdayreminder.Birthday
import gille.patricia.birthdayreminder.workers.birthdayTime
import java.time.LocalDate
import java.time.OffsetDateTime


@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Birthday::class,
            parentColumns = ["id"],
            childColumns = ["birthdayId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = NotificationRule::class,
            parentColumns = ["id"],
            childColumns = ["notificationRuleId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Notification(
    @ColumnInfo(index = true)
    val birthdayId: Long,
    val date: OffsetDateTime,
    val step: Int,
    @ColumnInfo(index = true)
    val notificationRuleId: Long,
    val notificationRuleVersion: Int
) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L
}

class NotificationFactory {
    // next heisst, dass die naechste Beachrichtigung hergestellt wird, die gefeuert wird.
    // Uebergeben wird die eben bearbeitete aus der Datenbank oder keine, falls es die erste Benachrichtigung ist.

    fun next(
        birthday: Birthday,
        notificationRule: NotificationRule,
        notification: Notification? = null,
        currentOffsetDateTime: OffsetDateTime = OffsetDateTime.now()
    ): Notification {
        return next(
            birthday.id,
            birthday.day,
            birthday.month,
            notificationRule,
            notification?.step ?: -1,
            notification?.notificationRuleId ?: -1,
            notification?.notificationRuleVersion ?: -1,
            currentOffsetDateTime
        )
    }

    fun next(
        birthdayId: Long,
        birthdayDay: Int,
        birthdayMonth: Int,
        notificationRule: NotificationRule,
        lastStep: Int = -1,
        lastNotificationRuleId: Long = -1,
        lastNotificationRuleVersion: Int = -1,
        currentOffsetDateTime: OffsetDateTime = OffsetDateTime.now()
    ): Notification {

        val lastReminderDate = getLastReminderForNextBirthday(
            birthdayDay, birthdayMonth, notificationRule.lastNotification, currentOffsetDateTime
        )

        val lastNotificationStep =
        // falls es die erste Benachrichtigung zu einem Geburtstag ist
        // oder der User seine Benachrichtigungseinstellung
            // geaendert hat
            if (notificationRule.id != lastNotificationRuleId
                || notificationRule.version != lastNotificationRuleVersion
            ) {
                -1
                // andernfalls kann die Variable step aus der letzten Benachrichtigung verwendet werden,
                // um die naechste Benachrichtigung zu generieren
            } else {
                lastStep
            }

        var (notificationDate, newNotificationStep) =
            getNextReminderForNextBirthday(
                birthdayDay,
                birthdayMonth,
                lastNotificationStep,
                notificationRule,
                currentOffsetDateTime
            )
        // if no step was successful
        if (newNotificationStep == -1) {
            notificationDate = lastReminderDate
        }
        // last reminder before birthday, calculations start again
        if (notificationDate == lastReminderDate) {
            newNotificationStep = -1
        }
        return Notification(
            birthdayId,
            notificationDate,
            newNotificationStep,
            notificationRule.id,
            notificationRule.version
        )
    }


    private fun getLastReminderForNextBirthday(
        bdDay: Int,
        bdMonth: Int,
        daysBeforeBd: Int,
        currentDateTime: OffsetDateTime
    ): OffsetDateTime {
        val birthday: LocalDate = LocalDate.of(currentDateTime.year, bdMonth, bdDay)
        val lastReminder = OffsetDateTime.of(birthday, birthdayTime, currentDateTime.offset)
            .minusDays(daysBeforeBd.toLong())
        if (!lastReminder.isAfter(currentDateTime)) {
            return lastReminder.plusYears(1L)
        }
        return lastReminder
    }

    private fun getNextReminderForNextBirthday(
        bdDay: Int,
        bdMonth: Int,
        step: Int,
        notificationRule: NotificationRule,
        currentOffsetDateTime: OffsetDateTime
    ): Pair<OffsetDateTime, Int> {
        var nextBirthday: LocalDate = LocalDate.of(currentOffsetDateTime.year, bdMonth, bdDay)
        if (nextBirthday <= currentOffsetDateTime.toLocalDate()) {
            nextBirthday = nextBirthday.plusYears(1L)
        }
        val periodForRepeat: Int =
            notificationRule.firstNotification - notificationRule.lastNotification
        if (periodForRepeat == 0) {
            return Pair(
                getLastReminderForNextBirthday(
                    bdDay,
                    bdMonth,
                    notificationRule.lastNotification,
                    currentOffsetDateTime
                ), step
            )
        }
        val repeatEvery = if (notificationRule.repeat == 0) {
            // interpret zero as not set, that is only first and last notification
            periodForRepeat
        } else {
            notificationRule.repeat
        }
        var nextReminderDate = OffsetDateTime.MIN
        val start = nextBirthday.minusDays(notificationRule.firstNotification.toLong())

        val repetitions: Int = periodForRepeat / repeatEvery
        var thisStep: Int = -1
        for (newStep in (step + 1)..repetitions) {
            val date = start.plusDays(newStep * repeatEvery.toLong())
            if (date.isAfter(currentOffsetDateTime.toLocalDate()) && date.isBefore(nextBirthday)) {
                nextReminderDate = OffsetDateTime.of(
                    date,
                    birthdayTime,
                    currentOffsetDateTime.offset
                )
                thisStep = newStep
                break
            }
        }
        return Pair(nextReminderDate, thisStep)
    }

    fun getAll(birthday: Birthday, notificationRule: NotificationRule): List<Notification> {
        val currentOffsetDateTime = OffsetDateTime.now()
        val firstNotification = next(birthday, notificationRule, null, currentOffsetDateTime)
        val notifications = mutableListOf<Notification>()
        notifications.add(firstNotification)
        var index = 0
        while (notifications[index].step > -1) {
            index += 1
            notifications.add(
                index,
                next(birthday, notificationRule, notifications[index - 1], currentOffsetDateTime)
            )
        }
        return notifications
    }
}
