package gille.patricia.birthdayreminder.workers

import android.content.Context
import androidx.work.*

class TriggerWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    private val workManager = WorkManager.getInstance(applicationContext)

    companion object {
        const val WORK_NAME = "gille.patricia.birthdayreminder.workers.TriggerWorker"
    }

    override fun doWork(): Result {
        val continuation = workManager
            .beginUniqueWork(
                BIRTHDAY_NOTIFICATION_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequest.from(RetrieveCurrentNotificationsWorker::class.java)
            ).then(OneTimeWorkRequest.from(SendBirthdayNotificationsWorker::class.java)).then(
                OneTimeWorkRequest.from(DeleteSentNotificationsWorker::class.java)
            )
        continuation.enqueue()
        return Result.success()
    }
}