package gille.patricia.birthdayreminder.model

import androidx.room.*
import gille.patricia.birthdayreminder.Birthday
import java.time.OffsetDateTime

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
data class Notification(
    @ColumnInfo(index = true)
    val birthdayId: Long,
    val date: OffsetDateTime
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L
}

data class BirthdayWithNotifications(
    @Embedded val birthday: Birthday,
    @Relation(
        parentColumn = "id",
        entityColumn = "birthdayId"
    )
    val notifications: List<Notification>
)

