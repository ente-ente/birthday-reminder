package gille.patricia.birthdayreminder.viewmodel

import android.text.TextUtils
import androidx.lifecycle.*
import gille.patricia.birthdayreminder.Birthday
import gille.patricia.birthdayreminder.LiveDataValidator
import gille.patricia.birthdayreminder.LiveDataValidatorResolver
import gille.patricia.birthdayreminder.Person
import gille.patricia.birthdayreminder.persistence.BirthdayRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class BirthdayViewModel(
    private val repository: BirthdayRepository,
) : ViewModel() {
    val yearInput = MutableLiveData<String>()
    private val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val yearInputValidator = LiveDataValidator(yearInput).apply {
        //Whenever the condition of the predicate is true, the error message should be emitted
        addRule("Jahr erforderlich") { it.isNullOrBlank() || it.equals("") }
        addRule("Jahr ist ganze nichtnegative Zahl") {
            !TextUtils.isDigitsOnly(it.toString())
        }
        addRule("Jahr darf nicht in der Zukunft liegen") {
            try {
                it!!.toInt() > currentYear
            } catch (e: NumberFormatException) {
                true
            }
        }
    }
    val allBirthdays: LiveData<List<Birthday>> = repository.allBirthdays
    private val _month = MutableLiveData<Int>()
    val month: LiveData<Int> = _month

    private val _day = MutableLiveData<Int>()
    val day: LiveData<Int> = _day

    val nameInput = MutableLiveData<String>()
    val nameInputValidator = LiveDataValidator(nameInput).apply {
        addRule("Name erforderlich") { it.isNullOrBlank() || it.equals("") }
    }

    val surName = MutableLiveData<String>()

    fun setMonth(month: Int) {
        _month.value = month
    }

    fun setDay(day: Int) {
        _day.value = day
    }

    fun resetBirthday() {
        yearInput.value = ""
        nameInput.value = ""
        surName.value = ""
    }

    //We will use a mediator so we can update the error state of our form fields
    //and the enabled state of our login button as the form data changes
    val isFormValidMediator = MediatorLiveData<Boolean>()

    init {
        resetBirthday()
        isFormValidMediator.value = false
        isFormValidMediator.addSource(nameInput) { validateForm() }
        isFormValidMediator.addSource(yearInput) { validateForm() }

    }

    //This is called whenever the nameInputLiveData and yearInputLiveData changes
    private fun validateForm() {
        val validators = listOf(nameInputValidator, yearInputValidator)
        val validatorResolver = LiveDataValidatorResolver(validators)
        isFormValidMediator.value = validatorResolver.isValid()
    }

    fun insertBirthday() = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(
            Birthday(
                day.value!!, month.value!!, yearInput.value!!.toInt(),
                Person(nameInput.value!!, surName.value ?: "")
            )
        )
    }

    override fun onCleared() {
        // DO NOT forget to remove sources from mediator
        isFormValidMediator.removeSource(nameInput)
        isFormValidMediator.removeSource(yearInput)
    }

    fun alreadySaved(): Boolean {
        val b = Birthday(
            day.value!!,
            month.value!!,
            yearInput.value!!.toInt(),
            Person(nameInput.value!!, surName.value)
        )
        return allBirthdays.value!!.contains(b)
    }
}

class BirthdayViewModelFactory(
    private val repository: BirthdayRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BirthdayViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BirthdayViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}