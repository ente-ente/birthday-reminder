package gille.patricia.birthdayreminder.viewmodel

import androidx.lifecycle.ViewModel
import gille.patricia.birthdayreminder.persistence.BirthdayRepository

class NotificationViewmodel(private val repository: BirthdayRepository) : ViewModel()