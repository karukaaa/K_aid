package com.example.myapplication.reviews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.google.firebase.firestore.FirebaseFirestore

class ReviewAdapter(private val approvalMode: Boolean = true) :
    ListAdapter<Review, ReviewAdapter.ViewHolder>(ReviewDiffCallback()) {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val reviewText: TextView = itemView.findViewById(R.id.review_text)
        val approveButton: Button = itemView.findViewById(R.id.approve_button)
        val rejectButton: Button = itemView.findViewById(R.id.reject_button)
        val timestampText: TextView = itemView.findViewById(R.id.timestamp_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_review, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val review = getItem(position)
        holder.reviewText.text = review.text
        holder.timestampText.text ="Published: ${review.timestamp?.toDate()?.toString()}" ?: "Unknown"

        if (approvalMode) {
            holder.approveButton.visibility = View.VISIBLE
            holder.rejectButton.visibility = View.VISIBLE

            holder.approveButton.setOnClickListener {
                FirebaseFirestore.getInstance().collection("reviews")
                    .document(review.id)
                    .update("status", "Approved")
                    .addOnSuccessListener {
                        val updatedList = currentList.toMutableList().apply {
                            removeAt(position)
                        }
                        submitList(updatedList)
                    }
            }

            holder.rejectButton.setOnClickListener {
                FirebaseFirestore.getInstance().collection("reviews")
                    .document(review.id)
                    .update("status", "Rejected")
                    .addOnSuccessListener {
                        val updatedList = currentList.toMutableList().apply {
                            removeAt(position)
                        }
                        submitList(updatedList)
                    }
            }
        } else {
            holder.approveButton.visibility = View.GONE
            holder.rejectButton.visibility = View.GONE
        }
    }

}

class ReviewDiffCallback : DiffUtil.ItemCallback<Review>() {
    override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
        return oldItem == newItem
    }
}