package gille.patricia.birthdayreminder.model

import androidx.room.DatabaseView
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

@DatabaseView(
    "SELECT notification.id AS notificationId, notification.step, " +
            "notification.date AS notificationDate, " +
            "notification.notificationRuleVersion AS actualRuleVersion, " +
            "notificationRule.id AS notificationRuleId, notificationRule.version AS currentRuleVersion, " +
            "notificationRule.firstNotification, notificationRule.repeat, notificationRule.lastNotification, " +
            "birthday.id AS birthdayId, birthday.name, birthday.surname, birthday.year, birthday.day, " +
            "birthday.month, birthday.notificationActive FROM notification " +
            "INNER JOIN notificationRule ON notificationRule.id = notification.notificationRuleId " +
            "INNER JOIN birthday ON birthday.id = notificationRule.birthdayId "
)
data class NotificationWithNotificationRuleAndBirthday(
    val notificationId: Long,
    val notificationDate: OffsetDateTime,
    val step: Int,
    val actualRuleVersion: Int,
    val notificationRuleId: Long,
    val currentRuleVersion: Int,
    val firstNotification: Int,
    val repeat: Int,
    val lastNotification: Int,
    val birthdayId: Long,
    val name: String,
    val surname: String?,
    val year: Int,
    val month: Int,
    val day: Int,
    val notificationActive: Boolean
) {

    private fun getNextBirthday(currentDate: LocalDate): LocalDate {
        val bd: LocalDate = LocalDate.of(currentDate.year, month, day)
        if (currentDate > bd) {
            return bd.plusYears(1L)
        }
        return bd
    }

    fun getDaysUntilNextBirthday(currentDate: LocalDate): Int {
        return ChronoUnit.DAYS.between(currentDate, getNextBirthday(currentDate)).toInt()
    }

    fun getNotificationMessage(): String {
        TODO("Not implemented")
    }

    fun getNextNotification(notificationFactory: NotificationFactory): Notification {
        val notificationRule =
            NotificationRule(birthdayId, firstNotification, repeat, lastNotification)
        notificationRule.version = currentRuleVersion
        notificationRule.id = notificationRuleId
        return notificationFactory.next(
            birthdayId, day, month,
            notificationRule,
            step, notificationRuleId, actualRuleVersion
        )
    }
}