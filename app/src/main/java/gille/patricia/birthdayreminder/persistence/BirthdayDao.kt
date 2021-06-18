package gille.patricia.birthdayreminder.persistence

import androidx.lifecycle.LiveData
import androidx.room.*
import gille.patricia.birthdayreminder.Birthday
import kotlinx.coroutines.flow.Flow

@Dao
interface BirthdayDao {
    @Query("DELETE FROM birthday")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBirthdays(vararg birthdays: Birthday)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertBirthday(birthday: Birthday)

    @Query("SELECT COUNT() FROM birthday WHERE name = :name AND day = :day AND month = :month AND year = :year")
    fun count(name: String, day: Int, month: Int, year: Int): Int

    @Transaction
    suspend fun insertBirthdayIfNotExists(birthday: Birthday): Int {
        if (count(birthday.person.name, birthday.day, birthday.month, birthday.year) == 0) {
            insertBirthday(birthday)
            return 0
        }
        return -1
    }

    @Transaction
    suspend fun toggleNotification(birthdayId: Long, notificationActive: Boolean) {
        val birthday = findById(birthdayId)
        birthday.notificationActive = notificationActive
        update(birthday)
    }

    @Update
    suspend fun update(birthday: Birthday)

    @Delete
    suspend fun deleteBirthdays(vararg birthdays: Birthday)

    @Query("SELECT * FROM birthday WHERE id = :id LIMIT 1")
    fun findById(id: Long): Birthday

    @Query("SELECT * FROM birthday WHERE id in (:ids)")
    fun findBirthdaysByIds(ids: List<Long>): List<Birthday>

    @Query("SELECT * FROM birthday ORDER BY month, day, year, name ASC")
    fun loadAllBirthdays(): LiveData<List<Birthday>>

    @Query("SELECT * FROM birthday WHERE day = :day AND month = :month ORDER BY year, name ASC ")
    fun birthdaysWithDayAndMonth(day: Int, month: Int): Flow<List<Birthday>>


}
