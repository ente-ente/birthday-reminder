package gille.patricia.birthdayreminder

import android.app.Application
import androidx.databinding.library.BuildConfig
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import gille.patricia.birthdayreminder.model.NotificationFactory
import gille.patricia.birthdayreminder.persistence.BirthdayReminderDatabase
import gille.patricia.birthdayreminder.persistence.BirthdayRepository
import gille.patricia.birthdayreminder.workers.TriggerWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber
import timber.log.Timber.DebugTree

class BirthdayApplication : Application() {
    // No need to cancel this scope as it'll be torn down with the process
    private val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    private val database by lazy { BirthdayReminderDatabase.getDatabase(this, applicationScope) }
    val birthdayRepository by lazy {
        BirthdayRepository(
            database.birthdayDao(),
            database.notificationDao(),
            database.notificationRuleDao(),
            database.notificationWithNotificationRuleAndBirthdayDao()
        )
    }
    val notificationFactory by lazy { NotificationFactory() }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
            setupRecurringWork()
        }
    }

    private fun setupRecurringWork() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .apply {
                setRequiresDeviceIdle(true)
            }
            .build()

        val repeatingRequest = PeriodicWorkRequestBuilder<TriggerWorker>(
            1,
            java.util.concurrent.TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .build()

        Timber.d("WorkManager: Periodic Work request for birthday notifications is scheduled")
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            TriggerWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )
    }
}
