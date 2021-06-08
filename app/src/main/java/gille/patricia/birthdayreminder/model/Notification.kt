package gille.patricia.birthdayreminder.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import gille.patricia.birthdayreminder.Birthday
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime

private val birthdayTime = LocalTime.of(0, 0, 0, 0)
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
        currentOffsetDateTime: OffsetDateTime = OffsetDateTime.now(),
        notification: Notification? = null
    ): Notification {

        val lastReminderDate = getLastReminderForNextBirthday(
            birthday.day, birthday.month, notificationRule.lastNotification, currentOffsetDateTime
        )

        val lastNotificationStep =
        // falls es die erste Benachrichtigung zu einem Geburtstag ist
        // oder der User seine Benachrichtigungseinstellung
            // geaendert hat
            if (notification == null || notificationRule.id != notification.notificationRuleId
                || notificationRule.version != notification.notificationRuleVersion
            ) {
                -1
                // andernfalls kann die Variable step aus der letzten Benachrichtigung verwendet werden,
                // um die naechste Benachrichtigung zu generieren
            } else {
                notification.step
            }

        var (notificationDate, newNotificationStep) =
            getNextReminderForNextBirthday(
                birthday.day,
                birthday.month,
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
            birthday.id,
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
        val birthday: LocalDate = LocalDate.of(currentOffsetDateTime.year, bdMonth, bdDay)

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
        val start = birthday.minusDays(notificationRule.firstNotification.toLong())
        val repetitions: Int = periodForRepeat / repeatEvery
        var thisStep: Int = -1
        for (newStep in (step + 1)..repetitions) {
            val date = start.plusDays(newStep * repeatEvery.toLong())
            if (date.isAfter(currentOffsetDateTime.toLocalDate()) && date.isBefore(birthday)) {
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
}
