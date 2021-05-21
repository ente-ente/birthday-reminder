package gille.patricia.birthdayreminder.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import gille.patricia.birthdayreminder.R
import gille.patricia.birthdayreminder.view.DayListFragmentDirections


class DayAdapter(private val numberOfDays: Int, val monthId: Int, val monthName: String) :
        RecyclerView.Adapter<DayAdapter.DayViewHolder>() {

    class DayViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val button: Button = view.findViewById<Button>(R.id.button_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val layout = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.day_item_view, parent, false)
        return DayViewHolder(layout)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val item = position + 1
        holder.button.text = item.toString()
        val title = "$item. $monthName"
        holder.button.setOnClickListener {
            val action = DayListFragmentDirections.actionDayListFragmentToDayFragment(item, monthId, title)
            holder.view.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return numberOfDays
    }
}
