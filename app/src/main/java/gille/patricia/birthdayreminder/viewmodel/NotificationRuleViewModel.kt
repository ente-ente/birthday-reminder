package gille.patricia.birthdayreminder.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import gille.patricia.birthdayreminder.LiveDataValidator
import gille.patricia.birthdayreminder.LiveDataValidatorResolver
import gille.patricia.birthdayreminder.model.NotificationRule
import gille.patricia.birthdayreminder.persistence.BirthdayRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationRuleViewModel(private val repository: BirthdayRepository) : ViewModel() {

    val birthdayId = MutableLiveData<Long>()
    val lastReminder = MutableLiveData<String>()
    val lastReminderValidator = LiveDataValidator(lastReminder).apply {
        //Whenever the condition of the predicate is true, the error message should be emitted
        addRule("erforderlich") { it.isNullOrBlank() || it == "" }
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
        addRule("erforderlich") { it.isNullOrBlank() || it == "" }
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
        addRule("erforderlich") { it.isNullOrBlank() || it == "" }
        addRule("Anzahl darf nicht größer sein als Tage zwischen erster und letzter Erinnerung liegen") {
            try {
                it!!.toInt() > daysBeforeNotification.value!!.toInt() - lastReminder.value!!.toInt()
            } catch (e: NumberFormatException) {
                true
            }
        }
    }

    val nextReminderDate = MutableLiveData<String>()

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
            val nextReminderDateTmp: String
            if (notificationRuleExistsForBirthday(birthdayId)) {
                notificationRule = repository.getNotificationRule(birthdayId)
                notificationRuleId = notificationRule.id
                nextReminderDateTmp =
                    repository.getNextNotificationByBirthdayId(birthdayId).date.toLocalDate().toString()
            } else {
                notificationRule = NotificationRule(birthdayId, 0, 0, 0)
                nextReminderDateTmp = "kein Wert"
            }

            daysBeforeNotification.postValue(notificationRule.firstNotification.toString())
            repeatInterval.postValue(notificationRule.repeat.toString())
            lastReminder.postValue(notificationRule.lastNotification.toString())
            nextReminderDate.postValue(nextReminderDateTmp)
        }
    }

    fun saveChanges(setupNotification: (Long, String) -> Unit) {
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
            if (notificationActive.value != null && birthday.notificationActive != notificationActive.value!!) {
                repository.updateActivationStatus(birthday, notificationActive.value!!)
            }


            if (notificationRuleId > -1) {
                notificationRule.id = notificationRuleId
                repository.updateNotificationRuleAndNotification(birthday, notificationRule)
            } else {
                repository.insertNewNotificationRuleAndGenerateNotification(
                    birthday,
                    notificationRule
                )
            }
            val next = repository.getNextNotificationByBirthdayId(birthdayId.value!!).date.toLocalDate()
            setupNotification(birthdayId.value!!, next.toString())
        }
    }

    private suspend fun notificationRuleExistsForBirthday(birthdayId: Long): Boolean {
        return repository.notificationRuleForBirthdayCount(birthdayId) > 0
    }

    fun initLiveData(birthdayId: Long) {
        setBirthdayId(birthdayId)
        setNotificationActive(birthdayId)
        initNotificationRuleFields(birthdayId)
    }

    override fun onCleared() {
        // remove sources from mediator
        isFormValidMediator.removeSource(daysBeforeNotification)
        isFormValidMediator.removeSource(repeatInterval)
        isFormValidMediator.removeSource(lastReminder)
    }
}