package gille.patricia.birthdayreminder.viewmodel

import android.text.TextUtils
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import gille.patricia.birthdayreminder.Birthday
import gille.patricia.birthdayreminder.LiveDataValidator
import gille.patricia.birthdayreminder.LiveDataValidatorResolver
import gille.patricia.birthdayreminder.model.NotificationRule
import gille.patricia.birthdayreminder.persistence.BirthdayRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationRuleViewmodel(private val repository: BirthdayRepository) : ViewModel() {

    val birthdayId = MutableLiveData<Long>()
    val lastReminder = MutableLiveData<String>()
    val lastReminderValidator = LiveDataValidator(lastReminder).apply {
        //Whenever the condition of the predicate is true, the error message should be emitted
        addRule("erforderlich") { it.isNullOrBlank() || it.equals("") }
        addRule("ganze nichtnegative Zahl") {
            !TextUtils.isDigitsOnly(it.toString())
        }
        addRule("Anzahl Tage zu groß") {
            try {
                it!!.toInt() > 364
            } catch (e: NumberFormatException) {
                true
            }
        }
    }
    val daysBeforeNotification = MutableLiveData<String>()
    val daysBeforeNotificationValidator = LiveDataValidator(daysBeforeNotification).apply {
        //Whenever the condition of the predicate is true, the error message should be emitted
        addRule("erforderlich") { it.isNullOrBlank() || it.equals("") }
        addRule("ganze nichtnegative Zahl") {
            !TextUtils.isDigitsOnly(it.toString())
        }
        addRule("Anzahl Tage zu groß") {
            try {
                it!!.toInt() > 364
            } catch (e: NumberFormatException) {
                true
            }
        }
        addRule("Anzahl muss größer sein als letzte Erinnerung") {
            try {
                it!!.toInt() < lastReminder.value!!.toInt()
            } catch (e: NumberFormatException) {
                true
            }
        }
    }

    val repeatInterval = MutableLiveData<String>()
    val repeatIntervalValidator = LiveDataValidator(repeatInterval).apply {
        //Whenever the condition of the predicate is true, the error message should be emitted
        addRule("erforderlich") { it.isNullOrBlank() || it.equals("") }
        addRule("ganze nichtnegative Zahl") {
            !TextUtils.isDigitsOnly(it.toString())
        }
        addRule("Anzahl darf nicht größer sein als Tage zwischen erster und letzter Erinnerung liegen") {
            try {
                it!!.toInt() > daysBeforeNotification.value!!.toInt() - lastReminder.value!!.toInt()
            } catch (e: NumberFormatException) {
                true
            }
        }
    }


    val isFormValidMediator = MediatorLiveData<Boolean>()

    init {
        isFormValidMediator.value = false
        isFormValidMediator.addSource(daysBeforeNotification) { validateForm() }
        isFormValidMediator.addSource(lastReminder) { validateForm() }
        isFormValidMediator.addSource(repeatInterval) { validateForm() }
    }

    //This is called whenever the nameInputLiveData and yearInputLiveData changes
    private fun validateForm() {
        val validators =
            listOf(lastReminderValidator, daysBeforeNotificationValidator, repeatIntervalValidator)
        val validatorResolver = LiveDataValidatorResolver(validators)
        isFormValidMediator.value = validatorResolver.isValid()
    }

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
            if (notificationRuleExistsForBirthday(birthdayId)) {
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
            val birthday = repository.getBirthday(birthdayId.value!!)
            // set activation
            birthday.notificationActive = notificationActive.value!!
            repository.updateBirthday(birthday)
            if (notificationRuleId > -1) {
                notificationRule.id = notificationRuleId
                //check if something has changed
                val oldRule = repository.findNotificationRuleById(notificationRuleId)
                if (!oldRule.isEqual(notificationRule)) {
                    notificationRule.version = notificationRule.version + 1
                    update(birthday, notificationRule)
                }
            } else {
                notificationRule.version = 1
                insert(birthday, notificationRule)
            }
        }
    }

    private suspend fun notificationRuleExistsForBirthday(birthdayId: Long): Boolean {
        return repository.notificationRuleForBirthdayCount(birthdayId) > 0
    }

    private suspend fun insert(birthday: Birthday, notificationRule: NotificationRule) {
        return repository.insertNewNotificationRuleAndGenerateFirstNotification(
            birthday,
            notificationRule
        )
    }

    private suspend fun update(birthday: Birthday, notificationRule: NotificationRule) {
        repository.updateNotificationRuleAndNotification(birthday, notificationRule)
    }

    fun initLiveData(birthdayId: Long) {
        setBirthdayId(birthdayId)
        setNotificationActive(birthdayId)
        initNotificationRuleFields(birthdayId)
    }

    override fun onCleared() {
        // DO NOT forget to remove sources from mediator
        isFormValidMediator.removeSource(daysBeforeNotification)
        isFormValidMediator.removeSource(repeatInterval)
        isFormValidMediator.removeSource(lastReminder)
    }
}