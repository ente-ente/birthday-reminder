package gille.patricia.birthdayreminder.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import gille.patricia.birthdayreminder.Birthday
import gille.patricia.birthdayreminder.databinding.BirthdayItemViewBinding
import gille.patricia.birthdayreminder.view.DayFragmentDirections

class BirthdayListAdapter : ListAdapter<Birthday, BirthdayListAdapter.BirthdayViewHolder>(
    BIRTHDAY_COMPARATOR
) {
    companion object {
        private val BIRTHDAY_COMPARATOR = object : DiffUtil.ItemCallback<Birthday>() {
            override fun areItemsTheSame(oldItem: Birthday, newItem: Birthday): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Birthday, newItem: Birthday): Boolean {
                return oldItem.isEqual(newItem)
            }
        }

    }

    class BirthdayViewHolder(private var binding: BirthdayItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(current: Birthday?) {
            binding.birthday = current

            binding.executePendingBindings()
        }

        val button: Button = binding.buttonItem
        val alarmOn = binding.imageViewAlarmOn
        val alarmOff = binding.imageViewAlarmOff


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BirthdayViewHolder {
        return BirthdayViewHolder(
            BirthdayItemViewBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: BirthdayViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
        holder.button.setOnClickListener {
            val action =
                DayFragmentDirections.actionDayFragmentToBirthdaySettingsDialogue(current.id)
            Navigation.findNavController(it).navigate(action)
        }
        holder.alarmOn.visibility = if (current.notificationActive) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
        holder.alarmOff.visibility = if (!current.notificationActive) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }

}


