package gille.patricia.birthdayreminder.persistence

import androidx.room.*
import gille.patricia.birthdayreminder.model.NotificationRule

@Dao
interface NotificationRuleDao {
    @Query("SELECT * FROM notificationRule WHERE id = :id LIMIT 1")
    fun findById(id: Long): NotificationRule

    @Insert
    suspend fun insert(notificationRule: NotificationRule): Long

    @Delete
    suspend fun delete(notificationRule: NotificationRule)

    @Update
    suspend fun update(notificationRule: NotificationRule)

    @Query("SELECT * FROM notificationRule WHERE birthdayId = :birthdayId LIMIT 1")
    suspend fun getForBirthdayId(birthdayId: Long): NotificationRule
}