package gille.patricia.birthdayreminder.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import gille.patricia.birthdayreminder.Birthday

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Birthday::class,
            parentColumns = ["id"],
            childColumns = ["birthdayId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class NotificationRule(
    @ColumnInfo(index = true)
    val birthdayId: Long,
    val firstNotification: Int, // ... days before bd
    val repeat: Int,            // repeat every ... days
    val lastNotification: Int   // ... days before b
) {
    fun isEqual(notificationRule: NotificationRule): Boolean {
        return (birthdayId == notificationRule.birthdayId &&
                firstNotification == notificationRule.firstNotification &&
                repeat == notificationRule.repeat &&
                lastNotification == notificationRule.lastNotification)
    }

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L
    var version: Int = 0
}
