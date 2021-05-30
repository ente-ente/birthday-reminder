package gille.patricia.birthdayreminder.persistence

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import gille.patricia.birthdayreminder.Birthday
import gille.patricia.birthdayreminder.model.Notification
import gille.patricia.birthdayreminder.model.NotificationRule
import kotlinx.coroutines.flow.Flow

/**
 * Abstracted Repository as promoted by the Architecture Guide.
 * https://developer.android.com/topic/libraries/architecture/guide.html
 */
class BirthdayRepository(
    private val birthdayDao: BirthdayDao,
    private val notificationDao: NotificationDao,
    private val notificationRuleDao: NotificationRuleDao
) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allBirthdays: LiveData<List<Birthday>> = birthdayDao.loadAllBirthdays()

    @WorkerThread
    suspend fun getBirthday(id: Long): Birthday {
        return birthdayDao.findById(id)
    }

    @WorkerThread
    fun getBirthdaysForDay(day: Int, month: Int): Flow<List<Birthday>> {
        return birthdayDao.birthdaysWithDayAndMonth(day, month)
    }

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @WorkerThread
    suspend fun insert(birthday: Birthday) {
        return birthdayDao.insertBirthday(birthday)
    }

    @WorkerThread
    suspend fun insertIfNotExists(birthday: Birthday): Int {
        return birthdayDao.insertBirthdayIfNotExists(birthday)
    }

    @WorkerThread
    suspend fun getNotification(id: Long): Notification {
        return notificationDao.findById(id)
    }

    @WorkerThread
    suspend fun insertNotification(notification: Notification): Long {
        return notificationDao.insert(notification)
    }

    @WorkerThread
    suspend fun getNotificationRule(birthdayId: Long): NotificationRule {
        return notificationRuleDao.getForBirthdayId(birthdayId)
    }

    @WorkerThread
    suspend fun insertNotificationRule(notificationRule: NotificationRule): Long {
        return notificationRuleDao.insert(notificationRule)
    }

    @WorkerThread
    suspend fun updateNotificationRule(notificationRule: NotificationRule) {
        return notificationRuleDao.update(notificationRule)
    }

    @WorkerThread
    suspend fun toggleNotification(birthdayId: Long, notificationActive: Boolean) {
        return birthdayDao.toggleNotification(birthdayId, notificationActive)
    }
}

class BirthdaySaveError(message: String, cause: Throwable?) : Throwable(message, cause)

class NotificationSaveError(message: String, cause: Throwable?) : Throwable(message, cause)