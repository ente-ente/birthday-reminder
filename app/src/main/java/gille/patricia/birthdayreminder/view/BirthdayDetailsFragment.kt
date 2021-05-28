package gille.patricia.birthdayreminder.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import gille.patricia.birthdayreminder.databinding.FragmentBirthdayDetailsBinding

class BirthdayDetailsFragment : Fragment() {
    private var _binding: FragmentBirthdayDetailsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBirthdayDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }
}