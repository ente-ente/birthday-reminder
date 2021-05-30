package gille.patricia.birthdayreminder.persistence

import androidx.room.*
import gille.patricia.birthdayreminder.model.Notification

@Dao
interface NotificationDao {

    @Query("SELECT * FROM notification WHERE id = :id LIMIT 1")
    fun findById(id: Long): Notification

    @Insert
    suspend fun insert(notification: Notification): Long

    @Delete
    suspend fun delete(notification: Notification)

    @Update
    suspend fun update(notification: Notification)
}