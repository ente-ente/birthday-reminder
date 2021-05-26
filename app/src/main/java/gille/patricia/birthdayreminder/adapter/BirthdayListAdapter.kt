package gille.patricia.birthdayreminder.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
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
        holder.itemView.setOnClickListener {
            val action = DayFragmentDirections.actionDayFragmentToBirthdayDetailsFragment(current.id)
            holder.itemView.findNavController().navigate(action)
        }
    }

}


