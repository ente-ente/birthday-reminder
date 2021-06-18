package gille.patricia.birthdayreminder.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import gille.patricia.birthdayreminder.Birthday
import gille.patricia.birthdayreminder.persistence.BirthdayRepository

private val numberOfDaysMap = mapOf(
    0 to 0,
    1 to 31,
    2 to 29,
    3 to 31,
    4 to 30,
    5 to 31,
    6 to 30,
    7 to 31,
    8 to 31,
    9 to 30,
    10 to 31,
    11 to 30,
    12 to 31
)

class OverviewViewModel(repository: BirthdayRepository) : ViewModel() {

    private val _day = MutableLiveData<Int>()
    val day: LiveData<Int> = _day

    private val _month = MutableLiveData<Int>()
    val month: LiveData<Int> = _month

    private val _numberOfDays = MutableLiveData<Int>()
    val numberOfDays: LiveData<Int> = _numberOfDays

    init {
        resetDate()
    }

    private fun resetDate() {
        _day.value = 0
        _month.value = 0
        _numberOfDays.value = 0
    }

    fun setMonth(monthId: Int) {
        _month.value = monthId
        _numberOfDays.value = numberOfDaysMap[_month.value]
    }

    fun setDay(dayId: Int) {
        _day.value = dayId
    }

    val allBirthdays: LiveData<List<Birthday>> = repository.allBirthdays

    fun birthdaysForCurrentDay(): LiveData<List<Birthday>> {
        return Transformations.map(allBirthdays) {
            it.filter {
                it.day == day.value && it.month == month.value
            }
        }
    }

    fun birthdaysForCurrentMonth(): LiveData<List<Birthday>> {
        return Transformations.map(allBirthdays) {
            it.filter {
                it.month == month.value
            }
        }
    }
}
