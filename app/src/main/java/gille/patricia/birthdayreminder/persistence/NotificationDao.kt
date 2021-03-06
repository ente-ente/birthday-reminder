package gille.patricia.birthdayreminder.persistence

import androidx.room.*
import gille.patricia.birthdayreminder.model.Notification
import java.time.OffsetDateTime

@Dao
interface NotificationDao {

    @Query("SELECT * FROM notification WHERE id = :id LIMIT 1")
    fun findById(id: Long): Notification

    @Query("SELECT * FROM notification WHERE birthdayId = :birthdayId")
    fun findByBirthdayId(birthdayId: Long): List<Notification>

    @Query("SELECT * FROM notification WHERE birthdayId = :birthdayId ORDER BY date ASC LIMIT 1")
    fun nextNotificationByBirthdayId(birthdayId: Long): Notification

    @Query("DELETE FROM notification WHERE notificationRuleId = :notificationRuleId")
    fun deleteByNotificationRuleId(notificationRuleId: Long)

    @Query("SELECT * FROM notification WHERE date <= :current_date")
    fun getDueNotifications(current_date: OffsetDateTime): List<Notification>

    @Insert
    suspend fun insert(notifications: List<Notification>)

    @Delete
    suspend fun delete(notification: Notification)

    @Delete
    suspend fun delete(notifications: List<Notification>)

    @Update
    suspend fun update(notification: Notification)

    @Query("DELETE FROM notification WHERE id in (:ids)")
    fun deleteByIds(ids: LongArray)
}