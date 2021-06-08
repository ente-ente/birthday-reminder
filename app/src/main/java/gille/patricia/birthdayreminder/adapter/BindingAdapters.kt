package gille.patricia.birthdayreminder.adapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import gille.patricia.birthdayreminder.Birthday

@BindingAdapter("listData")
fun bindRecyclerView(
    recyclerView: RecyclerView,
    data: List<Birthday>?
) {
    val adapter = recyclerView.adapter as BirthdayListAdapter
    adapter.submitList(data)
}