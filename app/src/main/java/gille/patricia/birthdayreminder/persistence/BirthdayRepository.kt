package gille.patricia.birthdayreminder.persistence

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import gille.patricia.birthdayreminder.Birthday

/**
 * Abstracted Repository as promoted by the Architecture Guide.
 * https://developer.android.com/topic/libraries/architecture/guide.html
 */
class BirthdayRepository(private val birthdayDao: BirthdayDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allBirthdays: LiveData<List<Birthday>> = birthdayDao.loadAllBirthdays()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @WorkerThread
    suspend fun insert(birthday: Birthday) {
        return birthdayDao.insertBirthday(birthday)
    }
}
