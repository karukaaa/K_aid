package com.example.myapplication.requestshistory


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemConfirmationBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

data class Confirmation(
    val thankYouText: String = "",
    val photoUrl: String = "",
    val videoUrl: String = "",
    val audioUrl: String = "",
    val submittedAt: Timestamp? = null,
    val requestID: String = "",
    val donorID: String = ""
)

class ConfirmationAdapter :
    ListAdapter<Confirmation, ConfirmationAdapter.ConfirmationViewHolder>(ConfirmationDiffCallback()) {

    inner class ConfirmationViewHolder(private val binding: ItemConfirmationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(confirmation: Confirmation) {
            binding.thankYouText.text = confirmation.thankYouText

            // Load optional photo
            if (confirmation.photoUrl.isNotEmpty()) {
                binding.photoPreview.visibility = View.VISIBLE
                Glide.with(binding.photoPreview.context)
                    .load(confirmation.photoUrl)
                    .placeholder(R.drawable.logo)
                    .into(binding.photoPreview)
            } else {
                binding.photoPreview.visibility = View.GONE
            }

            // Format and show date
            binding.dateText.text = confirmation.submittedAt?.toDate()?.toString() ?: ""

            // Load childName from request
            val db = FirebaseFirestore.getInstance()
            if (confirmation.requestID.isNotEmpty()) {
                db.collection("requests").document(confirmation.requestID)
                    .get()
                    .addOnSuccessListener { requestDoc ->
                        val childName = requestDoc.getString("childName") ?: "Unknown child"
                        binding.childName.text = childName
                    }
                    .addOnFailureListener {
                        binding.childName.text = "Failed to load child name"
                    }
            } else {
                binding.childName.text = "No request ID"
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConfirmationViewHolder {
        val binding = ItemConfirmationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ConfirmationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConfirmationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class ConfirmationDiffCallback : DiffUtil.ItemCallback<Confirmation>() {
    override fun areItemsTheSame(oldItem: Confirmation, newItem: Confirmation): Boolean {
        return oldItem.requestID == newItem.requestID
    }

    override fun areContentsTheSame(oldItem: Confirmation, newItem: Confirmation): Boolean {
        return oldItem == newItem
    }
}
