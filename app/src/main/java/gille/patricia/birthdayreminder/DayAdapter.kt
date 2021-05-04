package gille.patricia.birthdayreminder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView


class DayAdapter(private val numberOfDays: Int, context: Context) :
    RecyclerView.Adapter<DayAdapter.DayViewHolder>() {
    private val days = 1.rangeTo(numberOfDays).toList()

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
        val item = days.get(position)
        holder.button.text = item.toString()
        holder.button.setOnClickListener {
            val action = DayListFragmentDirections.actionDayListFragmentToDayFragment(item)
            holder.view.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return days.size
    }

}
