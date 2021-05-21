package gille.patricia.birthdayreminder.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import gille.patricia.birthdayreminder.BirthdayApplication
import gille.patricia.birthdayreminder.R
import gille.patricia.birthdayreminder.adapter.BirthdayListAdapter
import gille.patricia.birthdayreminder.databinding.FragmentDayBinding
import gille.patricia.birthdayreminder.viewmodel.OverviewViewModel
import gille.patricia.birthdayreminder.viewmodel.OverviewViewModelFactory

class DayFragment : Fragment() {
    private var _binding: FragmentDayBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private val args: DayFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val overviewViewModel: OverviewViewModel by activityViewModels {
            OverviewViewModelFactory((requireActivity().application as BirthdayApplication).repository)
        }
        overviewViewModel.setDay(args.day)
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = BirthdayListAdapter()
        recyclerView.adapter = adapter
        overviewViewModel.birthdaysForCurrentDay().observe(viewLifecycleOwner) { birthdays ->
            birthdays.let { adapter.submitList(it) }
        }

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = overviewViewModel
        binding.dayFragment = this@DayFragment
    }

    fun goToAddScreen() {
        val title = resources.getString(
            R.string.add_birthday_title,
            args.day,
            resources.getStringArray(R.array.months)[args.monthId - 1]
        )

        findNavController().navigate(
            DayFragmentDirections.actionDayFragmentToAddBirthdayFragment(
                args.day,
                args.monthId,
                title
            )
        )
    }

}