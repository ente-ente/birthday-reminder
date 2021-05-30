package gille.patricia.birthdayreminder.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import gille.patricia.birthdayreminder.R
import gille.patricia.birthdayreminder.databinding.NotificationRulesDialogueBinding

class BirthdaySettingsDialogue : BottomSheetDialogFragment() {
    private var _binding: NotificationRulesDialogueBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.notification_rules_dialogue, container, false)
    }
}