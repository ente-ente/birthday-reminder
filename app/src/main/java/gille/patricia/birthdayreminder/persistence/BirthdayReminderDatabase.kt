package gille.patricia.birthdayreminder.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import gille.patricia.birthdayreminder.Birthday
import gille.patricia.birthdayreminder.Person
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * This is the backend. The database. This used to be done by the OpenHelper.
 * The fact that this has very few comments emphasizes its coolness.
 */
@Database(entities = [Birthday::class], version = 1)
abstract class BirthdayReminderDatabase : RoomDatabase() {

    abstract fun birthdayDao(): BirthdayDao

    companion object {
        @Volatile
        private var INSTANCE: BirthdayReminderDatabase? = null

        fun getDatabase(
                context: Context,
                scope: CoroutineScope
        ): BirthdayReminderDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        BirthdayReminderDatabase::class.java,
                        "birthday_reminder_database"
                )
                        // Wipes and rebuilds instead of migrating if no Migration object.
                        // Migration is not part of this codelab.
                        .fallbackToDestructiveMigration()
                        .addCallback(BirthdayReminderDatabaseCallback(scope))
                        .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        private class BirthdayReminderDatabaseCallback(
                private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            /**
             * Override the onCreate method to populate the database.
             */
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // If you want to keep the data through app restarts,
                // comment out the following line.
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.birthdayDao())
                    }
                }
            }
        }

        /**
         * Populate the database in a new coroutine.
         * If you want to start with more words, just add them.
         */
        suspend fun populateDatabase(birthdayDao: BirthdayDao) {
            // Start the app with a clean database every time.
            // Not needed if you only populate on creation.
            birthdayDao.deleteAll()

            val birthDays = arrayOf(
                    Birthday(22, 11, 1979, Person("Patricia", "Gille")),
                    Birthday(9, 7, 1986, Person("Stefan", "Gille")),
                    Birthday(9, 7, 1983, Person("Marie", "Reuther"))
            )
            birthdayDao.insertBirthdays(*birthDays)
        }
    }
}
