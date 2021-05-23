package gille.patricia.birthdayreminder.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import gille.patricia.birthdayreminder.BirthdayApplication
import gille.patricia.birthdayreminder.R
import gille.patricia.birthdayreminder.databinding.FragmentAddBirthdayBinding
import gille.patricia.birthdayreminder.viewmodel.BirthdayViewModel
import gille.patricia.birthdayreminder.viewmodel.BirthdayViewModelFactory

class AddBirthdayFragment : Fragment() {
    private var _binding: FragmentAddBirthdayBinding? = null
    private val binding get() = _binding!!
    private val args: AddBirthdayFragmentArgs by navArgs()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBirthdayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val birthdayViewmodel: BirthdayViewModel by activityViewModels {
            BirthdayViewModelFactory(
                (
                        requireActivity().application as BirthdayApplication).birthdayRepository
            )
        }
        birthdayViewmodel.setMonth(args.month)
        birthdayViewmodel.setDay(args.day)
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            birthdayViewmodel.resetBirthday()
        }
        callback.handleOnBackPressed()
        binding.viewModel = birthdayViewmodel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.addBirthdayFragment = this@AddBirthdayFragment
    }


    fun save(viewModel: BirthdayViewModel) {
        if (viewModel.alreadySaved()) {
            Toast.makeText(
                context, "Birthday is already in database. Check the name. " +
                        "Use back button to leave screen.", Toast.LENGTH_LONG
            ).show()
        } else {
            viewModel.insertBirthday()
            Toast.makeText(context, "Birthday saved.", Toast.LENGTH_LONG).show()
            goToDayOverviewScreen(viewModel)

        }
        // hide keyboard
        StaticViewHelperFunctions.hideKeyboardInFragment(requireView())
    }

    private fun goToDayOverviewScreen(birthdayViewModel: BirthdayViewModel) {
        val title = resources.getString(
            R.string.day_title,
            args.day,
            resources.getStringArray(R.array.months)[args.month - 1]
        )
        findNavController().navigate(
            AddBirthdayFragmentDirections.actionAddBirthdayFragmentToDayFragment(
                args.day,
                args.month,
                title
            )
        )
    }
}