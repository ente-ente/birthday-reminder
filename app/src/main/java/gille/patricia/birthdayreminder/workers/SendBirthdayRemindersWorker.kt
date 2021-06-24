package gille.patricia.birthdayreminder.workers

import android.app.Application
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import gille.patricia.birthdayreminder.BirthdayApplication
import gille.patricia.birthdayreminder.model.NotificationRule
import gille.patricia.birthdayreminder.model.NotificationWithNotificationRuleAndBirthday
import timber.log.Timber
import java.io.IOError
import java.time.OffsetDateTime

class SendBirthdayRemindersWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    companion object {
        const val WORK_NAME =
            "gille.patricia.birthdayreminder.workers.SendBirthdayNotificationsWorker"
    }

    private val repository = (Application() as BirthdayApplication).birthdayRepository
    override suspend fun doWork(): Result {
        var reminderData = mutableListOf<NotificationWithNotificationRuleAndBirthday>()
        val currentDate = OffsetDateTime.now()
        try {
            // Retrieve all notifications due until (including) the current date from the notifications table
            reminderData.addAll(repository.getNotificationData(currentDate))
            Timber.d("WorkManager: Work request retrieving all notifications")
        } catch (e: IOError) {
            return Result.retry()
        }

        if (reminderData.size == 0) {
            Timber.d("No messages for today.")
            return Result.success()
        }

        val messages = reminderData.filter { it.notificationActive }
            .map {
                "${it.name} hat in  ${
                    it.getDaysUntilNextBirthday(
                        currentDate.toLocalDate()
                    )
                } Tagen Geburtstag."
            }

        WorkerUtils().makeBirthdayNotificationsForToday(messages, applicationContext)

        val notificationFactory = (Application() as BirthdayApplication).notificationFactory

        val newNotifications = reminderData.map {
            notificationFactory.next(
                it.birthdayId, it.day, it.month,
                addVersionAndIdToNotificationRule(
                    NotificationRule(
                        it.birthdayId, it.firstNotification, it.repeat, it.lastNotification
                    ),
                    it.currentRuleVersion, it.notificationRuleId
                ),
                it.step, it.notificationRuleId, it.actualRuleVersion, currentDate
            )
        }

        try {
            repository.insertNewNotifications(newNotifications)
            Timber.d("WorkManager: Work request saving all new notifications")
        } catch (e: IOError) {
            return Result.retry()
        }

        val notificationIds = reminderData.map { it.notificationId }
        val output: Data = workDataOf(
            KEY_NOTIFICATION_IDS to notificationIds.toTypedArray(),
            KEY_NUMBER_OF_NOTIFICATIONS to notificationIds.size,
        )
        return Result.success(output)
    }

    private fun addVersionAndIdToNotificationRule(
        notificationRule: NotificationRule,
        ruleVersion: Int,
        oldRuleId: Long
    ): NotificationRule {
        notificationRule.id = oldRuleId
        notificationRule.version = ruleVersion
        return notificationRule
    }
}