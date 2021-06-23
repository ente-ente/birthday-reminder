package gille.patricia.birthdayreminder.workers

/*

class RetrieveCurrentNotificationsWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    companion object {
        const val WORK_NAME =
            "gille.patricia.birthdayreminder.workers.RetrieveCurrentNotificationsWorker"
    }

    private val applicationScope = CoroutineScope(Dispatchers.IO)
    val database = getDatabase(applicationContext, applicationScope)
    private val repository = BirthdayRepository(
        database.birthdayDao(),
        database.notificationDao(),
        database.notificationRuleDao()
    )

    override suspend fun doWork(): Result {
        var notifications = mutableListOf<Notification>()

        try {
            // Retrieve all notifications due until (including) the current date from the notifications table
            notifications.addAll(repository.getDueNotifications(OffsetDateTime.now()))
            Timber.d("WorkManager: Work request retrieving all notifications")
        } catch (e: IOError) {
            return Result.retry()
        }

        val birthdayIds: List<Long> = notifications.map {
            it.birthdayId
        }

        val notificationIds: List<Long> = notifications.map {
            it.id
        }

        val output: Data = workDataOf(
            KEY_BIRTHDAY_IDS to birthdayIds.toTypedArray(),
            KEY_NOTIFICATION_IDS to notificationIds.toTypedArray(),
            KEY_NUMBER_OF_NOTIFICATIONS to notificationIds.size,
        )
        return Result.success(output)
    }
}
*/
