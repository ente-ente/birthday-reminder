package gille.patricia.birthdayreminder.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
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

        birthdayViewmodel.setDayAndMonth(args.day, args.month)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            birthdayViewmodel.setLivedataEntriesToEmpty()
        }
        callback.handleOnBackPressed()
        binding.viewModel = birthdayViewmodel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.addBirthdayFragment = this@AddBirthdayFragment

        // show the spinner when [MainViewModel.spinner] is true
        birthdayViewmodel.spinner.observe(viewLifecycleOwner) { value ->
            value.let { show ->
                binding.spinner.visibility = if (show) View.VISIBLE else View.GONE
            }
        }

        // Show a snackbar whenever the [ViewModel.snackbar] is updated a non-null value
        birthdayViewmodel.snackbar.observe(viewLifecycleOwner) { text ->
            text?.let {
                val viewModel = binding.viewModel
                val snackbar = Snackbar.make(binding.root, text, Snackbar.LENGTH_LONG)
                snackbar.setAction("GO TO DAY OVERVIEW", object : View.OnClickListener {
                    override fun onClick(v: View) {
                        goToDayOverviewScreen(viewModel!!)
                    }
                })
                StaticViewHelperFunctions.hideKeyboardInFragment(requireView())
                snackbar.show()
                birthdayViewmodel.onSnackbarShown()
            }
        }
    }

    private fun goToDayOverviewScreen(birthdayViewModel: BirthdayViewModel) {
        val title = resources.getString(
                R.string.day_title,
                birthdayViewModel.day.value,
                resources.getStringArray(R.array.months)[birthdayViewModel.month.value!! - 1]
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