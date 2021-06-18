package gille.patricia.birthdayreminder.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import gille.patricia.birthdayreminder.persistence.BirthdayReminderDatabase
import gille.patricia.birthdayreminder.persistence.BirthdayRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import java.io.IOError

class DeleteSentNotificationsWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    private val applicationScope = CoroutineScope(Dispatchers.IO)
    val database = BirthdayReminderDatabase.getDatabase(applicationContext, applicationScope)
    private val repository = BirthdayRepository(
        database.birthdayDao(),
        database.notificationDao(),
        database.notificationRuleDao()
    )

    companion object {
        const val WORK_NAME =
            "gille.patricia.birthdayreminder.workers.DeleteSentNotificationsWorker"
    }

    override suspend fun doWork(): Result {
        if (inputData.getInt(KEY_NUMBER_OF_NOTIFICATIONS, 0) == 0) {
            return Result.success()
        }
        val notificationIds = inputData.getLongArray(KEY_NOTIFICATION_IDS)
        if (notificationIds == null || notificationIds.isEmpty()) {
            return Result.failure()
        }
        try {
            // Delete sent notifications
            repository.deleteNotificationsByIds(notificationIds)
            Timber.d("WorkManager: Work request for deletion is run")
        } catch (e: IOError) {
            return Result.retry()
        }
        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }
}