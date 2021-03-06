package gille.patricia.birthdayreminder.persistence

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.room.Transaction
import gille.patricia.birthdayreminder.Birthday
import gille.patricia.birthdayreminder.model.Notification
import gille.patricia.birthdayreminder.model.NotificationFactory
import gille.patricia.birthdayreminder.model.NotificationRule
import gille.patricia.birthdayreminder.model.NotificationWithNotificationRuleAndBirthday
import timber.log.Timber
import java.time.OffsetDateTime


/**
 * Abstracted Repository as promoted by the Architecture Guide.
 * https://developer.android.com/topic/libraries/architecture/guide.html
 */
class BirthdayRepository(
    private val birthdayDao: BirthdayDao,
    private val notificationDao: NotificationDao,
    private val notificationRuleDao: NotificationRuleDao,
    private val notificationWithNotificationRuleAndBirthdayDao: NotificationWithNotificationRuleAndBirthdayDao
) {

    val allBirthdays: LiveData<List<Birthday>> = birthdayDao.loadAllBirthdays()
    private val notificationFactory = NotificationFactory()

    @WorkerThread
    suspend fun getBirthday(id: Long): Birthday {
        return birthdayDao.findById(id)
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
    suspend fun getDueNotifications(dateTime: OffsetDateTime): List<Notification> {
        return notificationDao.getDueNotifications(dateTime)
    }

    @WorkerThread
    suspend fun getNextNotificationByBirthdayId(birthdayId: Long): Notification {
        return notificationDao.nextNotificationByBirthdayId(birthdayId)
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
        //check if something has changed
        val oldRule = findNotificationRuleById(notificationRule.id)
        if (!oldRule.isEqual(notificationRule)) {
            notificationRule.version = oldRule.version.inc()
            notificationRuleDao.update(notificationRule)
            //clean up old notification(s)
            notificationDao.deleteByNotificationRuleId(notificationRule.id)
            //generate new notification
            val updatedNotification = notificationFactory.next(birthday, notificationRule)
            insertNewNotification(updatedNotification)
        }
    }

    @WorkerThread
    suspend fun notificationRuleForBirthdayCount(birthdayId: Long): Int {
        return notificationRuleDao.count(birthdayId)
    }

    @Transaction
    suspend fun insertNewNotificationRuleAndGenerateNotification(
        birthday: Birthday,
        notificationRule: NotificationRule
    ) {
        notificationRule.version = 1
        notificationRule.id = notificationRuleDao.insert(notificationRule)
        val notification =
            notificationFactory.next(birthday, notificationRule)
        insertNewNotification(notification)
    }

    @WorkerThread
    suspend fun insertNewNotification(notification: Notification) {
        notificationDao.insert(listOf(notification))
    }

    @WorkerThread
    suspend fun insertNewNotifications(notifications: List<Notification>) {
        notificationDao.insert(notifications)
    }

    @WorkerThread
    suspend fun deleteNotifications(notifications: List<Notification>) {
        notificationDao.delete(notifications)
    }

    @WorkerThread
    fun deleteNotificationsByIds(notificationIds: LongArray) {
        notificationDao.deleteByIds(notificationIds)
    }

    @WorkerThread
    suspend fun getNotificationData(currentDateTime: OffsetDateTime): List<NotificationWithNotificationRuleAndBirthday> {
        return notificationWithNotificationRuleAndBirthdayDao.getNotificationWithBirthdayAndNotificationRule(
            currentDateTime
        )
    }

    @WorkerThread
    suspend fun updateActivationStatus(birthday: Birthday, value: Boolean) {
        birthday.notificationActive = value
        updateBirthday(birthday)
    }
}

