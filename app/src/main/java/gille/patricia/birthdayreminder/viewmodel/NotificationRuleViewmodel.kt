package gille.patricia.birthdayreminder.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import gille.patricia.birthdayreminder.model.NotificationRule
import gille.patricia.birthdayreminder.persistence.BirthdayRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationRuleViewmodel(private val repository: BirthdayRepository) : ViewModel() {
    private var notificationRuleLiveData: LiveData<NotificationRule>? = null

    private lateinit var birthdayId: LiveData<Long>

    private lateinit var notificationActive: LiveData<Boolean>

    fun get(): LiveData<NotificationRule> {
        return notificationRuleLiveData ?: liveData {
            if (birthdayId.value != null) {
                emit(repository.getNotificationRule(birthdayId.value!!))
            }
        }.also {
            notificationRuleLiveData = it
        }
    }

    fun addData(
        notificationId: Long,
        birthdayId: Long,
        firstNotification: Int,
        repeatEvery: Int,
        lastNotification: Int
    ) {
        val notificationRule =
            NotificationRule(birthdayId, firstNotification, repeatEvery, lastNotification)

        CoroutineScope(Dispatchers.Main.immediate).launch {

            if (notificationId > 0) {
                notificationRule.id = notificationId
                update(notificationRule)
            } else {
                insert(notificationRule)
            }
        }
    }

    private suspend fun insert(notificationRule: NotificationRule): Long {
        return repository.insertNotificationRule(notificationRule)
    }

    private fun update(notificationRule: NotificationRule) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateNotificationRule(notificationRule)
    }

    private fun persistNotificationActiveState() {
        if (birthdayId.value != null) {
            CoroutineScope(Dispatchers.Main.immediate).launch {
                repository.toggleNotification(birthdayId.value!!, notificationActive.value!!)
            }
        }
    }
}