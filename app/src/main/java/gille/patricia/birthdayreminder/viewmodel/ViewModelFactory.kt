package gille.patricia.birthdayreminder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import gille.patricia.birthdayreminder.persistence.BirthdayRepository

class ViewModelFactory(private val repository: BirthdayRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OverviewViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OverviewViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(NotificationRuleViewmodel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NotificationRuleViewmodel(repository) as T
        } else if (modelClass.isAssignableFrom(BirthdayViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BirthdayViewModel(repository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
