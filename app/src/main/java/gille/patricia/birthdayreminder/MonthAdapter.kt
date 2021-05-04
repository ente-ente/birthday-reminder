package gille.patricia.birthdayreminder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView

class MonthAdapter : RecyclerView.Adapter<MonthAdapter.MonthViewHolder>() {

    // Generates a [CharRange] from 'A' to 'Z' and converts it to a list
    private val list = (1).rangeTo(12).toList()

    class MonthViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val button: Button = view.findViewById<Button>(R.id.button_item)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.month_item_view, parent, false)
        return MonthViewHolder(layout)
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        val item = list.get(position)
        holder.button.text = item.toString()
        holder.button.setOnClickListener {
            val action = MonthListFragmentDirections.actionMonthListFragmentToDayListFragment(item)
            holder.view.findNavController().navigate(action)
        }
    }
}