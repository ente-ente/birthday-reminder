package gille.patricia.birthdayreminder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

val monthNames = mapOf(
    1 to "Januar",
    2 to "Februar",
    3 to "März",
    4 to "April",
    5 to "Mai",
    6 to "Juni",
    7 to "Juli",
    8 to "August",
    9 to "September",
    10 to "Oktober",
    11 to "November",
    12 to "Dezember"
)
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

class DateViewModel : ViewModel() {
    private val _day = MutableLiveData<Int>()
    val day: LiveData<Int> = _day

    private val _month = MutableLiveData<Int>()
    val month: LiveData<Int> = _month

    private val _monthName = MutableLiveData<String>()
    val monthName: LiveData<String> = _monthName

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
        _monthName.value = monthNames[_month.value]
    }

    fun setDay(dayId: Int) {
        _day.value = dayId
    }

    fun getGermanDateString(): String {
        return "${day.value}. ${monthName.value}"
    }
}