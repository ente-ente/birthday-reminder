package gille.patricia.birthdayreminder.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import gille.patricia.birthdayreminder.BirthdayApplication
import gille.patricia.birthdayreminder.databinding.NotificationRulesDialogueBinding
import gille.patricia.birthdayreminder.viewmodel.NotificationRuleViewmodel
import gille.patricia.birthdayreminder.viewmodel.ViewModelFactory

class NotificationRulesDialogue : BottomSheetDialogFragment() {
    private var _binding: NotificationRulesDialogueBinding? = null
    private val binding get() = _binding!!
    val args: NotificationRulesDialogueArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NotificationRulesDialogueBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val notificationRuleViewmodel: NotificationRuleViewmodel by activityViewModels {
            ViewModelFactory(
                (requireActivity().application as BirthdayApplication)
                    .birthdayRepository
            )
        }
        binding.viewModel = notificationRuleViewmodel
        binding.lifecycleOwner = viewLifecycleOwner
        notificationRuleViewmodel.initLiveData(args.birthdayId)


        // When the user clicks the Done button, use the data here to either update
        // an existing item or create a new one
        binding.doneButton.setOnClickListener {
            notificationRuleViewmodel.saveRule()
            dismiss()
        }

        // User clicked the Cancel button; just exit the dialog without saving the data
        binding.cancelButton.setOnClickListener {
            dismiss()
        }
    }

}