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

            if (confirmation.photoUrl.isNotEmpty()) {
                binding.photoPreview.visibility = View.VISIBLE
                Glide.with(binding.photoPreview.context)
                    .load(confirmation.photoUrl)
                    .placeholder(R.drawable.baseline_account_circle_24)
                    .into(binding.photoPreview)
            } else {
                binding.photoPreview.visibility = View.GONE
            }

            binding.dateText.text = confirmation.submittedAt?.toDate()?.toString() ?: ""
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
