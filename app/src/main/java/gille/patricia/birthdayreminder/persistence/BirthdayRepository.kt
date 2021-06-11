package gille.patricia.birthdayreminder.persistence

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.room.Transaction
import gille.patricia.birthdayreminder.Birthday
import gille.patricia.birthdayreminder.model.Notification
import gille.patricia.birthdayreminder.model.NotificationFactory
import gille.patricia.birthdayreminder.model.NotificationRule
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import java.time.OffsetDateTime

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

    @WorkerThread
    suspend fun updateBirthday(birthday: Birthday) {
        birthdayDao.update(birthday)
    }

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
    suspend fun getDueNotifications(dateTime: OffsetDateTime): List<Notification> {
        return notificationDao.getDueNotifications(dateTime)
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
    suspend fun findNotificationRuleById(id: Long): NotificationRule {
        return notificationRuleDao.findById(id)
    }

    @Transaction
    suspend fun updateNotificationRuleAndNotification(
        birthday: Birthday,
        notificationRule: NotificationRule
    ) {
        //clean up old notification
        notificationDao.deleteByNotificationRuleId(notificationRule.id)
        //generate
        val notificationFactory = NotificationFactory()
        val updatedNotification: Notification = notificationFactory.next(birthday, notificationRule)
        notificationDao.insert(updatedNotification)
        return notificationRuleDao.update(notificationRule)
    }

    @WorkerThread
    suspend fun toggleNotification(birthdayId: Long, notificationActive: Boolean) {
        return birthdayDao.toggleNotification(birthdayId, notificationActive)
    }

    @WorkerThread
    suspend fun notificationRuleForBirthdayCount(birthdayId: Long): Int {
        return notificationRuleDao.count(birthdayId)
    }

    @Transaction
    suspend fun insertNewNotificationRuleAndGenerateFirstNotification(
        birthday: Birthday,
        notificationRule: NotificationRule
    ) {
        Timber.d("birthdayId: ${birthday.id}")

        Timber.d("notificationRule.id: ${notificationRule.id}")
        val newNotificationRuleId = notificationRuleDao.insert(notificationRule)
        Timber.d("newNotificationRuleId: $newNotificationRuleId")
        notificationRule.id = newNotificationRuleId
        val notificationFactory = NotificationFactory()
        val firstNotification: Notification = notificationFactory.next(birthday, notificationRule)
        notificationDao.insert(firstNotification)
    }
}

class BirthdaySaveError(message: String, cause: Throwable?) : Throwable(message, cause)

class NotificationSaveError(message: String, cause: Throwable?) : Throwable(message, cause)

class NotificationRuleSaveError(message: String, cause: Throwable?) : Throwable(message, cause)