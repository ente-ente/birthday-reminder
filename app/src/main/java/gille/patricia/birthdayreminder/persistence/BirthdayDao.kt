package gille.patricia.birthdayreminder.persistence

import androidx.lifecycle.LiveData
import androidx.room.*
import gille.patricia.birthdayreminder.Birthday
import gille.patricia.birthdayreminder.model.BirthdayWithNotifications
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

    @Delete
    fun deleteBirthdays(vararg birthdays: Birthday)

    @Query("SELECT * FROM birthday WHERE id = :id LIMIT 1")
    fun findById(id: Long): Birthday


    @Query("SELECT * FROM birthday ORDER BY month, day, year, name ASC")
    fun loadAllBirthdays(): LiveData<List<Birthday>>

    @Query("SELECT * FROM birthday WHERE day = :day AND month = :month ORDER BY year, name ASC ")
    fun birthdaysWithDayAndMonth(day: Int, month: Int): Flow<List<Birthday>>

    @Transaction
    @Query("SELECT * FROM Birthday")
    fun getBirthdaysWithNotifications(): List<BirthdayWithNotifications>

    @Transaction
    @Query("SELECT * FROM birthday WHERE id = :id LIMIT 1")
    fun getBirthdayWithNotifications(id: Long): BirthdayWithNotifications
}
