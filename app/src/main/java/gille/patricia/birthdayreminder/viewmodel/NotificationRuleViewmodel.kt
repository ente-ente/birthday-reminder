package gille.patricia.birthdayreminder.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import gille.patricia.birthdayreminder.model.NotificationRule
import gille.patricia.birthdayreminder.persistence.BirthdayRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationRuleViewmodel(private val repository: BirthdayRepository) : ViewModel() {
    private var notificationRuleLiveData: LiveData<NotificationRule>? = null

    val birthdayId = MutableLiveData<Long>()

    val daysBeforeNotification = MutableLiveData<String>()

    val repeatInterval = MutableLiveData<String>()

    val lastReminder = MutableLiveData<String>()

    val notificationActive = MutableLiveData<Boolean>()

    private var notificationRuleId: Long = -1


    private fun setBirthdayId(id: Long) {
        birthdayId.value = id
    }

    private fun setNotificationActive(birthdayId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            notificationActive.postValue(repository.getBirthday(birthdayId).notificationActive)
        }
    }

    private fun initNotificationRuleFields(birthdayId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val notificationRule: NotificationRule
            if (notificationExistsForBirthday(birthdayId)) {
                notificationRule = repository.getNotificationRule(birthdayId)
                notificationRuleId = notificationRule.id
            } else {
                notificationRule = NotificationRule(birthdayId, 0, 0, 0)
            }
            daysBeforeNotification.postValue(notificationRule.firstNotification.toString())
            repeatInterval.postValue(notificationRule.repeat.toString())
            lastReminder.postValue(notificationRule.lastNotification.toString())
        }
    }

    fun saveRule() {
        val notificationRule =
            NotificationRule(
                birthdayId.value!!,
                daysBeforeNotification.value!!.toInt(),
                repeatInterval.value!!.toInt(),
                lastReminder.value!!.toInt()
            )

        CoroutineScope(Dispatchers.IO).launch {
            if (notificationRuleId > -1) {
                notificationRule.id = notificationRuleId
                //TODO only if something has changed
                notificationRule.version = notificationRule.version + 1
                update(notificationRule)
            } else {
                notificationRule.version = 1
                insert(notificationRule)
            }
            repository.toggleNotification(birthdayId.value!!, notificationActive.value!!)
        }
    }

    private suspend fun notificationExistsForBirthday(birthdayId: Long): Boolean {
        return repository.notificationRuleForBirthdayCount(birthdayId) > 0
    }

    private suspend fun insert(notificationRule: NotificationRule): Long {
        return repository.insertNotificationRule(notificationRule)
    }

    private suspend fun update(notificationRule: NotificationRule) {
        repository.updateNotificationRule(notificationRule)
    }

    fun initLiveData(birthdayId: Long) {
        setBirthdayId(birthdayId)
        setNotificationActive(birthdayId)
        initNotificationRuleFields(birthdayId)
    }
}