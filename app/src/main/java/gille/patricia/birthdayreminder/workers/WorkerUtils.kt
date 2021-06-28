package gille.patricia.birthdayreminder.workers

import android.app.PendingIntent
import android.content.Context
import androidx.navigation.NavDeepLinkBuilder
import gille.patricia.birthdayreminder.R
import gille.patricia.birthdayreminder.model.NotificationWithNotificationRuleAndBirthday
import gille.patricia.birthdayreminder.view.BirthdayDetailsFragmentArgs
import gille.patricia.birthdayreminder.view.NotificationRulesDialogueArgs
import java.time.OffsetDateTime

class WorkerUtils {
    fun makeBirthdayNotificationsForToday(
        reminderData: List<NotificationWithNotificationRuleAndBirthday>,
        context: Context
    ) {

        var id = 0L
        val currentDate = OffsetDateTime.now()
        for (reminder in reminderData) {
            val message =
                "${reminder.name} hat in  ${
                    reminder.getDaysUntilNextBirthday(
                        currentDate.toLocalDate()
                    )
                } Tagen Geburtstag."

            val birthdayDetailsIntent: PendingIntent = NavDeepLinkBuilder(context)
                .setGraph(R.navigation.nav_graph)
                .setDestination(R.id.birthdayDetailsFragment)
                .setArguments(BirthdayDetailsFragmentArgs(reminder.birthdayId).toBundle())
                .createPendingIntent()

            val editNotificationIntent: PendingIntent = NavDeepLinkBuilder(context)
                .setGraph(R.navigation.nav_graph)
                .setDestination(R.id.notificationRulesDialogue)
                .setArguments(NotificationRulesDialogueArgs(reminder.birthdayId).toBundle())
                .createPendingIntent()

            Notifier.postNotification(
                id,
                context,
                birthdayDetailsIntent,
                editNotificationIntent,
                message
            )
            id += 1
        }
    }
}
