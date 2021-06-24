package gille.patricia.birthdayreminder.persistence

import androidx.room.Dao
import androidx.room.Query
import gille.patricia.birthdayreminder.model.NotificationWithNotificationRuleAndBirthday
import java.time.OffsetDateTime

@Dao
interface NotificationWithNotificationRuleAndBirthdayDao {
    @Query(
        "SELECT * FROM notificationWithNotificationRuleAndBirthday " +
                "WHERE notificationDate <= :currentDate"
    )
    suspend fun getNotificationWithBirthdayAndNotificationRule(currentDate: OffsetDateTime): List<NotificationWithNotificationRuleAndBirthday>


}