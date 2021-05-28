package gille.patricia.birthdayreminder.viewmodel

import android.text.TextUtils
import androidx.lifecycle.*
import gille.patricia.birthdayreminder.Birthday
import gille.patricia.birthdayreminder.LiveDataValidator
import gille.patricia.birthdayreminder.LiveDataValidatorResolver
import gille.patricia.birthdayreminder.Person
import gille.patricia.birthdayreminder.persistence.BirthdayRepository
import gille.patricia.birthdayreminder.persistence.BirthdaySaveError
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

    val surName = MutableLiveData<String>()

    val nameInput = MutableLiveData<String>()
    val nameInputValidator = LiveDataValidator(nameInput).apply {
        addRule("Name erforderlich") { it.isNullOrBlank() || it.equals("") }
    }

    //We will use a mediator so we can update the error state of our form fields
    //and the enabled state of our login button as the form data changes
    val isFormValidMediator = MediatorLiveData<Boolean>()

    init {
        setLivedataEntriesToEmpty()
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

    private val _snackBar = MutableLiveData<String?>()
    val snackbar: LiveData<String?>
        get() = _snackBar

    fun onSnackbarShown() {
        _snackBar.value = null
    }

    private val _spinner = MutableLiveData<Boolean>(false)
    val spinner: LiveData<Boolean>
        get() = _spinner

    private val _month = MutableLiveData<Int>()
    val month: LiveData<Int> = _month

    private val _day = MutableLiveData<Int>()
    val day: LiveData<Int> = _day

    fun setDayAndMonth(day: Int, month: Int) {
        _month.value = month
        _day.value = day
    }

    fun setLivedataEntriesToEmpty() {
        yearInput.value = ""
        nameInput.value = ""
        surName.value = ""
    }

    fun saveBirthday() {
        viewModelScope.launch {
            try {
                _spinner.value = true
                if (repository.insertIfNotExists(
                        Birthday(
                            day.value!!, month.value!!, yearInput.value!!.toInt(),
                            false,
                            Person(nameInput.value!!, surName.value ?: "")
                        )
                    ) == 0) {
                    _snackBar.value = "Birthday saved."
                } else {
                    _snackBar.value = "Birthday already in database."
                }
            } catch (error: BirthdaySaveError) {
                _snackBar.value = error.message
            } finally {
                _spinner.value = false
            }
        }
    }

    override fun onCleared() {
        // DO NOT forget to remove sources from mediator
        isFormValidMediator.removeSource(nameInput)
        isFormValidMediator.removeSource(yearInput)
    }
}

