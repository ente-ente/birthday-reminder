package gille.patricia.birthdayreminder.workers


import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.testing.TestListenableWorkerBuilder
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class RetrieveCurrentNotificationsWorkerTest {
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testRetrieveCurrentNotificationsWorker() {
        val worker =
            TestListenableWorkerBuilder<RetrieveCurrentNotificationsWorker>(context).build()
        runBlocking {
            val result = worker.doWork()
            assertThat(result, `is`(androidx.work.ListenableWorker.Result.success()))
        }
    }
}