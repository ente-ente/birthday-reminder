package gille.patricia.birthdayreminder

import android.app.Application
import gille.patricia.birthdayreminder.persistence.BirthdayReminderDatabase
import gille.patricia.birthdayreminder.persistence.BirthdayRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class BirthdayApplication : Application() {
    // No need to cancel this scope as it'll be torn down with the process
    private val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    private val database by lazy { BirthdayReminderDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { BirthdayRepository(database.birthdayDao()) }
}
