package gille.patricia.birthdayreminder.persistence

import androidx.lifecycle.LiveData
import androidx.room.*
import gille.patricia.birthdayreminder.Birthday
import gille.patricia.birthdayreminder.model.BirthdayWithNotifications

@Dao
interface BirthdayDao {
    @Query("DELETE FROM birthday")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBirthdays(vararg birthdays: Birthday)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertBirthday(birthday: Birthday)

    @Delete
    fun deleteBirthdays(vararg birthdays: Birthday)

    @Query("SELECT * FROM birthday ORDER BY month, day, year, name ASC")
    fun loadAllBirthdays(): LiveData<List<Birthday>>

    @Query("SELECT * FROM birthday WHERE day = :day AND month = :month ORDER BY year, name ASC ")
    fun loadBirthdaysFromDate(day: Int, month: Int): Array<Birthday>

    @Transaction
    @Query("SELECT * FROM Birthday")
    fun getBirthdaysWithNotifications(): List<BirthdayWithNotifications>
}
