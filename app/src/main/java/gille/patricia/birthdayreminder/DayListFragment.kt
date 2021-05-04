package gille.patricia.birthdayreminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import gille.patricia.birthdayreminder.databinding.FragmentDayListBinding

class DayListFragment : Fragment() {
    private var _binding: FragmentDayListBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    val args: DayListFragmentArgs by navArgs()

    private val dateViewModel: DateViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dateViewModel.setMonth(args.monthId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDayListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(context, 4)
        recyclerView.adapter =
            DayAdapter(dateViewModel.numberOfDays.value ?: 0, this.requireContext())
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
        }
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            dateViewModel.monthName.value
    }
}