package gille.patricia.birthdayreminder.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class SendCurrentNotificationsWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        // Retrieve all notifications due until (including) the current date from the notifications table

        // Make string messages for notifications

        // Use makeBirthdayNotificationsForToday to send notifications

        // Calculate new notifications

        // Delete sent notifications

        // Save new notifications

        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }
}
