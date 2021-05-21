package gille.patricia.birthdayreminder.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import gille.patricia.birthdayreminder.adapter.MonthAdapter
import gille.patricia.birthdayreminder.databinding.FragmentMonthListBinding

class MonthListFragment : Fragment() {

    private var _binding: FragmentMonthListBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonthListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = MonthAdapter(this.requireContext())
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
        }
    }
}