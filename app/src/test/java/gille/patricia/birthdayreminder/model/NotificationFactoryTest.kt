package gille.patricia.birthdayreminder.model

import gille.patricia.birthdayreminder.Birthday
import gille.patricia.birthdayreminder.Person
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime

class NotificationFactoryTest {
    lateinit var birthdayWithNotificationActive: Birthday
    private lateinit var standardNotificationRule: NotificationRule
    private lateinit var oneDayEarlierNotificationRule: NotificationRule
    lateinit var twoTimesNotificationRule: NotificationRule
    lateinit var fourTimesExactNotificationRule: NotificationRule
    lateinit var fourTimesInExactNotificationRule: NotificationRule
    private var mockCurrentTime: LocalTime = LocalTime.of(0, 0, 0, 0)
    private val notificationFactory: NotificationFactory = NotificationFactory()

    @Before
    fun initBirthdayAndNotificationRule() {
        birthdayWithNotificationActive = Birthday(22, 11, 1979, true, Person("Patricia", "Gille"))
        birthdayWithNotificationActive.id = 1
        standardNotificationRule = NotificationRule(birthdayWithNotificationActive.id, 0, 0, 0)
        standardNotificationRule.id = 1
        standardNotificationRule.version = 1
        oneDayEarlierNotificationRule = NotificationRule(birthdayWithNotificationActive.id, 1, 0, 1)
        oneDayEarlierNotificationRule.id = 2
        oneDayEarlierNotificationRule.version = 1
        twoTimesNotificationRule = NotificationRule(birthdayWithNotificationActive.id, 7, 0, 1)
        twoTimesNotificationRule.id = 3
        twoTimesNotificationRule.version = 1
        fourTimesExactNotificationRule =
            NotificationRule(birthdayWithNotificationActive.id, 7, 2, 1)
        fourTimesExactNotificationRule.id = 4
        fourTimesExactNotificationRule.version = 1
        fourTimesInExactNotificationRule =
            NotificationRule(birthdayWithNotificationActive.id, 9, 3, 1)


    }

    // Standard Rule means only notification on the same day as birthday
    @Test
    fun get_notification_for_birthday_standard_rule_birthday_next_day() {
        // Rule: make notification
        val notification = notificationFactory.next(
            birthdayWithNotificationActive,
            standardNotificationRule,
            OffsetDateTime.of(
                LocalDate.of(2020, 11, 21),

                mockCurrentTime,
                OffsetDateTime.now().offset
            )
        )
        MatcherAssert.assertThat(
            notification.date.dayOfMonth,
            CoreMatchers.`is`(birthdayWithNotificationActive.day)
        )
        MatcherAssert.assertThat(notification.date.year, CoreMatchers.`is`(2020))
        MatcherAssert.assertThat(
            notification.date.month.value,
            CoreMatchers.`is`(birthdayWithNotificationActive.month)
        )

        MatcherAssert.assertThat(
            notification.birthdayId,
            CoreMatchers.`is`(birthdayWithNotificationActive.id)
        )
        MatcherAssert.assertThat(
            notification.notificationRuleId,
            CoreMatchers.`is`(standardNotificationRule.id)
        )
        MatcherAssert.assertThat(
            notification.notificationRuleVersion,
            CoreMatchers.`is`(standardNotificationRule.version)
        )
        MatcherAssert.assertThat(
            notification.step,
            CoreMatchers.`is`(-1)
        ) // last notification before birthday has -1 as step
    }

    @Test
    fun get_notification_for_birthday_standard_rule_birthday_same_day() {
        // Rule: make notification for next birthday
        val notification = notificationFactory.next(
            birthdayWithNotificationActive,
            standardNotificationRule,
            OffsetDateTime.of(
                LocalDate.of(2020, 11, 22),
                mockCurrentTime,
                OffsetDateTime.now().offset
            )
        )
        MatcherAssert.assertThat(
            notification.date.dayOfMonth,
            CoreMatchers.`is`(birthdayWithNotificationActive.day)
        )
        MatcherAssert.assertThat(notification.date.year, CoreMatchers.`is`(2021))
        MatcherAssert.assertThat(
            notification.date.month.value,
            CoreMatchers.`is`(birthdayWithNotificationActive.month)
        )
    }

    @Test
    fun get_notification_for_birthday_standard_rule_birthday_already_passed() {
        // Rule: make notification for next birthday
        val notification = notificationFactory.next(
            birthdayWithNotificationActive,
            standardNotificationRule,
            OffsetDateTime.of(
                LocalDate.of(2020, 12, 25),
                mockCurrentTime,
                OffsetDateTime.now().offset
            )
        )
        MatcherAssert.assertThat(
            notification.date.dayOfMonth,
            CoreMatchers.`is`(birthdayWithNotificationActive.day)
        )
        MatcherAssert.assertThat(notification.date.year, CoreMatchers.`is`(2021))
        MatcherAssert.assertThat(
            notification.date.month.value,
            CoreMatchers.`is`(birthdayWithNotificationActive.month)
        )
    }

    @Test
    fun get_notification_exactly_one_set_one_day_earlier_birthday_in_two_days() {
// Rule: make notification for next birthday

        val notification = notificationFactory.next(
            birthdayWithNotificationActive,
            oneDayEarlierNotificationRule,
            OffsetDateTime.of(
                LocalDate.of(2020, 11, 20),
                mockCurrentTime,
                OffsetDateTime.now().offset
            )
        )
        MatcherAssert.assertThat(notification.date.dayOfMonth, CoreMatchers.`is`(21))
        MatcherAssert.assertThat(notification.date.year, CoreMatchers.`is`(2020))
        MatcherAssert.assertThat(notification.date.month.value, CoreMatchers.`is`(11))
    }

    @Test
    fun get_notification_exactly_one_set_one_day_earlier_birthday_tomorrow() {
// Rule: make notification for next birthday
        val notification = notificationFactory.next(
            birthdayWithNotificationActive,
            oneDayEarlierNotificationRule,
            OffsetDateTime.of(
                LocalDate.of(2020, 11, 21),
                mockCurrentTime,
                OffsetDateTime.now().offset
            )
        )
        MatcherAssert.assertThat(notification.date.dayOfMonth, CoreMatchers.`is`(21))
        MatcherAssert.assertThat(notification.date.year, CoreMatchers.`is`(2021))
        MatcherAssert.assertThat(notification.date.month.value, CoreMatchers.`is`(11))
        MatcherAssert.assertThat(
            notification.notificationRuleId,
            CoreMatchers.`is`(oneDayEarlierNotificationRule.id)
        )
        MatcherAssert.assertThat(
            notification.step,
            CoreMatchers.`is`(-1)
        ) // last notification before birthday has -1 as step
    }

    @Test
    fun get_notification_exactly_one_set_one_day_earlier_birthday_passed() {
// Rule: make notification for next birthday
        val notification = notificationFactory.next(
            birthdayWithNotificationActive,
            oneDayEarlierNotificationRule,
            OffsetDateTime.of(
                LocalDate.of(2020, 12, 25),
                mockCurrentTime,
                OffsetDateTime.now().offset
            )
        )
        MatcherAssert.assertThat(notification.date.dayOfMonth, CoreMatchers.`is`(21))
        MatcherAssert.assertThat(notification.date.year, CoreMatchers.`is`(2021))
        MatcherAssert.assertThat(notification.date.month.value, CoreMatchers.`is`(11))
        MatcherAssert.assertThat(
            notification.step,
            CoreMatchers.`is`(-1)
        ) // last notification before birthday has -1 as step
    }


    @Test
    fun get_1st_notification_exactly_two_set() {
// date before first notification date

        val notification = notificationFactory.next(
            birthdayWithNotificationActive,
            twoTimesNotificationRule,
            OffsetDateTime.of(
                LocalDate.of(2020, 11, 14),
                mockCurrentTime,
                OffsetDateTime.now().offset
            )
        )
        MatcherAssert.assertThat(notification.date.dayOfMonth, CoreMatchers.`is`(15))
        MatcherAssert.assertThat(notification.date.year, CoreMatchers.`is`(2020))
        MatcherAssert.assertThat(notification.date.month.value, CoreMatchers.`is`(11))
        MatcherAssert.assertThat(
            notification.notificationRuleId,
            CoreMatchers.`is`(twoTimesNotificationRule.id)
        )
        MatcherAssert.assertThat(
            notification.step,
            CoreMatchers.`is`(0)
        )
    }

    @Test
    fun get_2nd_notification_exactly_two_set_none_fired() {
// day of first notification

        val notification = notificationFactory.next(
            birthdayWithNotificationActive,
            twoTimesNotificationRule,
            OffsetDateTime.of(
                LocalDate.of(2020, 11, 15),
                mockCurrentTime,
                OffsetDateTime.now().offset
            )
        )
        MatcherAssert.assertThat(notification.date.dayOfMonth, CoreMatchers.`is`(21))
        MatcherAssert.assertThat(notification.date.year, CoreMatchers.`is`(2020))
        MatcherAssert.assertThat(notification.date.month.value, CoreMatchers.`is`(11))
        MatcherAssert.assertThat(
            notification.step,
            CoreMatchers.`is`(-1)
        )
    }

    @Test
    fun get_2nd_notification_exactly_two_set_one_fired() {
        val notification = notificationFactory.next(
            birthdayWithNotificationActive,
            twoTimesNotificationRule,
            OffsetDateTime.of(
                LocalDate.of(2020, 11, 15),
                mockCurrentTime,
                OffsetDateTime.now().offset
            ),
            notificationFactory.next(
                birthdayWithNotificationActive,
                twoTimesNotificationRule,
                OffsetDateTime.of(
                    LocalDate.of(2020, 11, 14),
                    mockCurrentTime,
                    OffsetDateTime.now().offset
                )
            )
        )
        MatcherAssert.assertThat(notification.date.dayOfMonth, CoreMatchers.`is`(21))
        MatcherAssert.assertThat(notification.date.year, CoreMatchers.`is`(2020))
        MatcherAssert.assertThat(notification.date.month.value, CoreMatchers.`is`(11))
        MatcherAssert.assertThat(
            notification.step,
            CoreMatchers.`is`(-1)
        )
    }

    // This test is ok, because the same day the notification is sent, the next notification is to be generated
    @Test
    fun get_2nd_notification_exactly_two_set_one_fired_with_different_notification_rule() {
        // checks if robust towards former notification rules
        val notification = notificationFactory.next(
            birthdayWithNotificationActive,
            twoTimesNotificationRule,
            OffsetDateTime.of(
                LocalDate.of(2020, 11, 15),
                mockCurrentTime,
                OffsetDateTime.now().offset
            ),
            notificationFactory.next(
                birthdayWithNotificationActive,
                standardNotificationRule,
                OffsetDateTime.of(
                    LocalDate.of(2020, 11, 14),
                    mockCurrentTime,
                    OffsetDateTime.now().offset
                )
            )
        )
        MatcherAssert.assertThat(notification.date.dayOfMonth, CoreMatchers.`is`(21))
        MatcherAssert.assertThat(notification.date.year, CoreMatchers.`is`(2020))
        MatcherAssert.assertThat(notification.date.month.value, CoreMatchers.`is`(11))
        MatcherAssert.assertThat(
            notification.notificationRuleId,
            CoreMatchers.`is`(twoTimesNotificationRule.id)
        )
        MatcherAssert.assertThat(
            notification.step,
            CoreMatchers.`is`(-1)
        )
    }

    @Test
    fun get_2nd_notification_four_set_one_fired() {
        // checks if robust towards former notification rules
        val notification = notificationFactory.next(
            birthdayWithNotificationActive,
            fourTimesExactNotificationRule,
            OffsetDateTime.of(
                LocalDate.of(2020, 11, 15),
                mockCurrentTime,
                OffsetDateTime.now().offset
            ),
            notificationFactory.next(
                birthdayWithNotificationActive,
                fourTimesExactNotificationRule,
                OffsetDateTime.of(
                    LocalDate.of(2020, 11, 14),
                    mockCurrentTime,
                    OffsetDateTime.now().offset
                )
            )
        )
        MatcherAssert.assertThat(notification.date.dayOfMonth, CoreMatchers.`is`(17))
        MatcherAssert.assertThat(notification.date.year, CoreMatchers.`is`(2020))
        MatcherAssert.assertThat(notification.date.month.value, CoreMatchers.`is`(11))
        MatcherAssert.assertThat(
            notification.notificationRuleId,
            CoreMatchers.`is`(fourTimesExactNotificationRule.id)
        )
        MatcherAssert.assertThat(
            notification.step,
            CoreMatchers.`is`(1)
        )
    }

    @Test
    fun get_3nd_notification_four_set_two_fired() {
        // checks if robust towards former notification rules
        val notification = notificationFactory.next(
            birthdayWithNotificationActive, fourTimesExactNotificationRule, OffsetDateTime.of(
                LocalDate.of(2020, 11, 17),
                mockCurrentTime,
                OffsetDateTime.now().offset
            ),
            notificationFactory.next(
                birthdayWithNotificationActive,
                fourTimesExactNotificationRule,
                OffsetDateTime.of(
                    LocalDate.of(2020, 11, 15),
                    mockCurrentTime,
                    OffsetDateTime.now().offset
                ),
                notificationFactory.next(
                    birthdayWithNotificationActive,
                    fourTimesExactNotificationRule,
                    OffsetDateTime.of(
                        LocalDate.of(2020, 11, 14),
                        mockCurrentTime,
                        OffsetDateTime.now().offset
                    )
                )
            )
        )
        MatcherAssert.assertThat(notification.date.dayOfMonth, CoreMatchers.`is`(19))
        MatcherAssert.assertThat(notification.date.year, CoreMatchers.`is`(2020))
        MatcherAssert.assertThat(notification.date.month.value, CoreMatchers.`is`(11))
        MatcherAssert.assertThat(
            notification.notificationRuleId,
            CoreMatchers.`is`(fourTimesExactNotificationRule.id)
        )
        MatcherAssert.assertThat(
            notification.step,
            CoreMatchers.`is`(2)
        )
    }

    @Test
    fun get_last_notification_four_set_three_fired() {
        // checks if robust towards former notification rules
        val notification = notificationFactory.next(
            birthdayWithNotificationActive, fourTimesExactNotificationRule, OffsetDateTime.of(
                LocalDate.of(2020, 11, 19),
                mockCurrentTime,
                OffsetDateTime.now().offset
            ),
            notificationFactory.next(
                birthdayWithNotificationActive, fourTimesExactNotificationRule, OffsetDateTime.of(
                    LocalDate.of(2020, 11, 17),
                    mockCurrentTime,
                    OffsetDateTime.now().offset
                ),
                notificationFactory.next(
                    birthdayWithNotificationActive,
                    fourTimesExactNotificationRule,
                    OffsetDateTime.of(
                        LocalDate.of(2020, 11, 15),
                        mockCurrentTime,
                        OffsetDateTime.now().offset
                    ),
                    notificationFactory.next(
                        birthdayWithNotificationActive,
                        fourTimesExactNotificationRule,
                        OffsetDateTime.of(
                            LocalDate.of(2020, 11, 14),
                            mockCurrentTime,
                            OffsetDateTime.now().offset
                        )
                    )
                )
            )
        )
        MatcherAssert.assertThat(notification.date.dayOfMonth, CoreMatchers.`is`(21))
        MatcherAssert.assertThat(notification.date.year, CoreMatchers.`is`(2020))
        MatcherAssert.assertThat(notification.date.month.value, CoreMatchers.`is`(11))
        MatcherAssert.assertThat(
            notification.notificationRuleId,
            CoreMatchers.`is`(fourTimesExactNotificationRule.id)
        )
        MatcherAssert.assertThat(
            notification.step,
            CoreMatchers.`is`(-1)
        )
    }

    @Test
    fun get_last_notification_inexact_four_set_three_fired() {
        // checks if robust towards former notification rules
        val notification = notificationFactory.next(
            birthdayWithNotificationActive, fourTimesInExactNotificationRule, OffsetDateTime.of(
                LocalDate.of(2020, 11, 19),
                mockCurrentTime,
                OffsetDateTime.now().offset
            ),
            notificationFactory.next(
                birthdayWithNotificationActive, fourTimesInExactNotificationRule, OffsetDateTime.of(
                    LocalDate.of(2020, 11, 16),
                    mockCurrentTime,
                    OffsetDateTime.now().offset
                ),
                notificationFactory.next(
                    birthdayWithNotificationActive,
                    fourTimesInExactNotificationRule,
                    OffsetDateTime.of(
                        LocalDate.of(2020, 11, 13),
                        mockCurrentTime,
                        OffsetDateTime.now().offset
                    ),
                    notificationFactory.next(
                        birthdayWithNotificationActive,
                        fourTimesInExactNotificationRule,
                        OffsetDateTime.of(
                            LocalDate.of(2020, 11, 12),
                            mockCurrentTime,
                            OffsetDateTime.now().offset
                        )
                    )
                )
            )
        )
        MatcherAssert.assertThat(notification.date.dayOfMonth, CoreMatchers.`is`(21))
        MatcherAssert.assertThat(notification.date.year, CoreMatchers.`is`(2020))
        MatcherAssert.assertThat(notification.date.month.value, CoreMatchers.`is`(11))
        MatcherAssert.assertThat(
            notification.notificationRuleId,
            CoreMatchers.`is`(fourTimesInExactNotificationRule.id)
        )
        MatcherAssert.assertThat(
            notification.step,
            CoreMatchers.`is`(-1)
        )
    }

    @Test
    fun get_last_notification_inexact_four_set_three_fired_change_rule_version() {
        // checks if robust towards notification rules changes
        val formerNotification = notificationFactory.next(
            birthdayWithNotificationActive, fourTimesInExactNotificationRule, OffsetDateTime.of(
                LocalDate.of(2020, 11, 16),
                mockCurrentTime,
                OffsetDateTime.now().offset
            ),
            notificationFactory.next(
                birthdayWithNotificationActive,
                fourTimesInExactNotificationRule,
                OffsetDateTime.of(
                    LocalDate.of(2020, 11, 13),
                    mockCurrentTime,
                    OffsetDateTime.now().offset
                ),
                notificationFactory.next(
                    birthdayWithNotificationActive,
                    fourTimesInExactNotificationRule,
                    OffsetDateTime.of(
                        LocalDate.of(2020, 11, 12),
                        mockCurrentTime,
                        OffsetDateTime.now().offset
                    )
                )
            )
        )
        fourTimesExactNotificationRule.version = 2
        val notification = notificationFactory.next(
            birthdayWithNotificationActive, fourTimesInExactNotificationRule, OffsetDateTime.of(
                LocalDate.of(2020, 11, 19),
                mockCurrentTime,
                OffsetDateTime.now().offset
            ),
            formerNotification
        )
        MatcherAssert.assertThat(notification.date.dayOfMonth, CoreMatchers.`is`(21))
        MatcherAssert.assertThat(notification.date.year, CoreMatchers.`is`(2020))
        MatcherAssert.assertThat(notification.date.month.value, CoreMatchers.`is`(11))
        MatcherAssert.assertThat(
            notification.notificationRuleId,
            CoreMatchers.`is`(fourTimesInExactNotificationRule.id)
        )
        MatcherAssert.assertThat(
            notification.step,
            CoreMatchers.`is`(-1)
        )
    }
}

