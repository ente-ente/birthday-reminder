package gille.patricia.birthdayreminder.workers
/*

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import gille.patricia.birthdayreminder.Birthday
import gille.patricia.birthdayreminder.persistence.BirthdayReminderDatabase
import gille.patricia.birthdayreminder.persistence.BirthdayRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import java.io.IOError

class SendBirthdayNotificationsWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    companion object {
        const val WORK_NAME =
            "gille.patricia.birthdayreminder.workers.SendBirthdayNotificationsWorker"
    }

    private val applicationScope = CoroutineScope(Dispatchers.IO)
    val database = BirthdayReminderDatabase.getDatabase(applicationContext, applicationScope)
    private val repository = BirthdayRepository(
        database.birthdayDao(),
        database.notificationDao(),
        database.notificationRuleDao()
    )

    // Retrieve all birthdays
    override suspend fun doWork(): Result {
        if (inputData.getInt(KEY_NUMBER_OF_NOTIFICATIONS, 0) == 0) {
            Timber.d("No messages for today.")
            return Result.success(workDataOf(KEY_NUMBER_OF_NOTIFICATIONS to 0))
        }
        val birthdayIds = inputData.getLongArray(KEY_BIRTHDAY_IDS)
        if (birthdayIds == null || birthdayIds.isEmpty()) {
            return Result.failure(workDataOf())
        }

        val birthdays = mutableListOf<Birthday>()
        try {
            // Retrieve all notifications due until (including) the current date from the notifications table
            birthdays.addAll(repository.getBirthdays(birthdayIds.asList()))
            Timber.d("WorkManager: Work request retrieving all birthdays for the notifications")
        } catch (e: IOError) {
            return Result.retry()
        }
        val workerUtils = WorkerUtils()
        // Make string messages for notifications
        val birthdayMessages = birthdays.filter { it.notificationActive }
            .map {
                "${it.person.name} hat in  ${
                    workerUtils.getDaysUntilBirthday(
                        it.day,
                        it.month
                    )
                } Tagen Geburtstag."
            }



        // Use makeBirthdayNotificationsForToday to send notifications
        workerUtils.makeBirthdayNotificationsForToday(birthdayMessages, applicationContext)

        val output: Data =
            workDataOf(KEY_NOTIFICATION_IDS to inputData.getLongArray(KEY_NOTIFICATION_IDS))
        return Result.success(output)
    }


}
*/