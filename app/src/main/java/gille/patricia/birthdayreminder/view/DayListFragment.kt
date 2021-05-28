package gille.patricia.birthdayreminder.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import gille.patricia.birthdayreminder.BirthdayApplication
import gille.patricia.birthdayreminder.R
import gille.patricia.birthdayreminder.adapter.DayAdapter
import gille.patricia.birthdayreminder.databinding.FragmentDayListBinding
import gille.patricia.birthdayreminder.viewmodel.OverviewViewModel
import gille.patricia.birthdayreminder.viewmodel.ViewModelFactory


class DayListFragment : Fragment() {
    private var _binding: FragmentDayListBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private val args: DayListFragmentArgs by navArgs()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDayListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val overviewViewModel: OverviewViewModel by activityViewModels {
            ViewModelFactory((requireActivity().application as BirthdayApplication).birthdayRepository)
        }
        overviewViewModel.setMonth(args.monthId)
        val numberOfDays = overviewViewModel.numberOfDays.value ?: 0
        val monthName = this.requireContext().resources.getStringArray(R.array.months)[args.monthId - 1]

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(context, 4)
        recyclerView.adapter = DayAdapter(numberOfDays, args.monthId, monthName)

        binding.lifecycleOwner = viewLifecycleOwner
    }
}