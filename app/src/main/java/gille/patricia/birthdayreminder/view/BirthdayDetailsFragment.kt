package gille.patricia.birthdayreminder.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import gille.patricia.birthdayreminder.R
import gille.patricia.birthdayreminder.databinding.FragmentBirthdayDetailsBinding

class BirthdayDetailsFragment : Fragment() {
    private var _binding: FragmentBirthdayDetailsBinding? = null
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_birthday_details, container, false)
    }
}