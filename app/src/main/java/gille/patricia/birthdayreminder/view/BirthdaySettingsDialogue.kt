package gille.patricia.birthdayreminder.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import gille.patricia.birthdayreminder.R
import gille.patricia.birthdayreminder.databinding.BirthdaySettingsDialogueBinding

class BirthdaySettingsDialogue : BottomSheetDialogFragment() {
    private var _binding: BirthdaySettingsDialogueBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.birthday_settings_dialogue, container, false)
    }
}