package gille.patricia.birthdayreminder

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import gille.patricia.birthdayreminder.adapter.BirthdayListAdapter

@BindingAdapter("listData")
fun bindRecyclerView(
    recyclerView: RecyclerView,
    data: List<Birthday>?
) {
    val adapter = recyclerView.adapter as BirthdayListAdapter
    adapter.submitList(data)
}