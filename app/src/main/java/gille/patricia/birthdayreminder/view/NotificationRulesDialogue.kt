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
import gille.patricia.birthdayreminder.viewmodel.NotificationRuleViewModel
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

        val notificationRuleViewModel: NotificationRuleViewModel by activityViewModels {
            ViewModelFactory(
                (requireActivity().application as BirthdayApplication)
                    .birthdayRepository
            )
        }
        binding.viewModel = notificationRuleViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        notificationRuleViewModel.initLiveData(args.birthdayId)

        binding.doneButton.setOnClickListener {
            notificationRuleViewModel.saveRule()
            dismiss()
        }

        // User clicked the Cancel button; just exit the dialog without saving the data
        binding.cancelButton.setOnClickListener {
            dismiss()
        }
    }

}