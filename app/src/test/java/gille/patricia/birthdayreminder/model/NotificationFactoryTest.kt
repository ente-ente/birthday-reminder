package gille.patricia.birthdayreminder.model

import gille.patricia.birthdayreminder.Birthday
import gille.patricia.birthdayreminder.Person
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime

class NotificationFactoryTest {
    private lateinit var birthdays: List<Birthday>
    private lateinit var notificationRules: MutableList<NotificationRule>
    private val notificationFactory = NotificationFactory()

    @Before
    fun initBirthdaysAndNotificationRules() {

        birthdays = listOf<Birthday>(
            Birthday(31, 12, 1999, false, Person("Am letzten Tag des Jahres Geburtstag", "Gille")),
            Birthday(9, 7, 1986, false, Person("Stefan", "Gille")),
            Birthday(13, 3, 2017, false, Person("Anna", "Gille"))
        )

        notificationRules = mutableListOf()
        for (i in 1..3) {
            for (c in birthdays.indices) {
                birthdays[c].id = c.toLong()
                var notificationRule: NotificationRule = NotificationRule(0, 0, 0, 0)
                notificationRule.version = 1
                notificationRule.id = c.toLong()

                when(i) {
                    1 -> {
                        notificationRule = NotificationRule(c.toLong(), 0, 0, 0)
                    }
                    2 -> {
                        notificationRule = NotificationRule(c.toLong(), 10, 5, 0)
                        notificationRule.version = 2
                    }
                    3 -> {
                        notificationRule = NotificationRule(c.toLong(), 30, 5, 3)
                        notificationRule.id = (c + birthdays.size).toLong()
                    }
                    else -> notificationRule = NotificationRule(0,0,0,0)
                }
                notificationRules.add(notificationRule)
            }
        }

    }

    @Test
    fun onlyOneNotificationOnBirthday() {
        val today = OffsetDateTime.now()
        for (c in birthdays.indices) {
            val result = notificationFactory.next(birthdays[c], notificationRules[c])
            if (LocalDate.of(
                    today.year,
                    birthdays[c].month,
                    birthdays[c].day
                ) <= today.toLocalDate()
            ) {
                MatcherAssert.assertThat(
                    birthdays[c].person.name,
                    result.date.toLocalDate(),
                    equalTo(LocalDate.of(today.year + 1, birthdays[c].month, birthdays[c].day))
                )
            } else {
                MatcherAssert.assertThat(
                    birthdays[c].person.name,
                    result.date.toLocalDate(),
                    equalTo(LocalDate.of(today.year, birthdays[c].month, birthdays[c].day))
                )
            }
            MatcherAssert.assertThat(birthdays[c].person.name + " birthday id is correctly set", result.birthdayId, equalTo(birthdays[c].id))
            MatcherAssert.assertThat(birthdays[c].person.name + " rule version is correctly set", result.notificationRuleVersion, equalTo(notificationRules[c].version))
            MatcherAssert.assertThat(birthdays[c].person.name + " rule id", result.notificationRuleId, equalTo(notificationRules[c].id))
            MatcherAssert.assertThat(birthdays[c].person.name + " notification step", result.step, equalTo(-1))
        }
    }

    @Test
    fun firstNotification10DaysBeforeBirthday() {
        val today = OffsetDateTime.now()
        for (c in birthdays.indices) {
            val result = notificationFactory.next(birthdays[c], notificationRules[c + birthdays.size])
            val tmpBd = LocalDate.of(today.year, birthdays[c].month, birthdays[c].day)
            var tmpNot = tmpBd.minusDays(notificationRules[c + birthdays.size].firstNotification.toLong())
            if (tmpNot <= today.toLocalDate()) {
                tmpNot = tmpNot.plusYears(1L)
            }
            MatcherAssert.assertThat("Notification for " + birthdays[c].person.name + " must be ten days earlier", result.date.toLocalDate(), equalTo(tmpNot))
        }
    }
}

