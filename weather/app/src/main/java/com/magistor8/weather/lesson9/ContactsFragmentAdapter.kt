package com.magistor8.weather.lesson9

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.magistor8.weather.R


class ContactsFragmentAdapter :
    RecyclerView.Adapter<ContactsFragmentAdapter.ContactsViewHolder>() {

    private var dataList: MutableList<Contact> = mutableListOf()
    private var onItemViewClickListener: ContactsListFragment.OnItemViewClickListener? = null

    fun setOnClickListener(onClickListener: ContactsListFragment.OnItemViewClickListener) {
        onItemViewClickListener = onClickListener
    }

    fun addData(data: Contact) {
        dataList.add(itemCount, data)
        notifyItemInserted(itemCount - 1)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ContactsViewHolder {
        return ContactsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_contacts_recycler_item, parent, false) as View
        )
    }

    inner class ContactsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(data: Contact) {
            itemView.apply {
                findViewById<TextView>(R.id.recyclerViewItem).text = data.name
                setOnClickListener{
                    onItemViewClickListener?.onItemViewClick(data)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}
