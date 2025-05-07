package com.example.myapplication.addingchildren

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class ApproveChildrenAdapter(
    private var children: List<Pair<PendingChild, String>> = emptyList(),
    private val onApprove: (PendingChild, String) -> Unit,
    private val onReject: (String) -> Unit
) : RecyclerView.Adapter<ApproveChildrenAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val childName: TextView = view.findViewById(R.id.child_name)
        val childAge: TextView = view.findViewById(R.id.child_age)
        val childGender: TextView = view.findViewById(R.id.child_gender)
        val childBio: TextView = view.findViewById(R.id.child_bio)
        val approveButton: Button = view.findViewById(R.id.approve_button)
        val rejectButton: Button = view.findViewById(R.id.reject_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pending_child, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = children.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (child, docId) = children[position]
        holder.childName.text = "Name: ${child.childName}"
        holder.childAge.text = "Age: ${child.childAge}"
        holder.childGender.text = "Gender: ${child.childGender}"
        holder.childBio.text = "Bio: ${child.childBio}"

        holder.approveButton.setOnClickListener {
            onApprove(child, docId)
        }
        holder.rejectButton.setOnClickListener {
            onReject(docId)
        }
    }

    fun submitList(newList: List<Pair<PendingChild, String>>) {
        children = newList
        notifyDataSetChanged()
    }
}
