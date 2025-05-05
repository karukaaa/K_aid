package com.example.myapplication.requestlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.RequestBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RequestRecyclerViewAdapter(
    private val onItemClick: (Request) -> Unit
) : ListAdapter<Request, RequestRecyclerViewAdapter.ViewHolder>(RequestCallback()) {

    private val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid

    inner class ViewHolder(private val binding: RequestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                onItemClick(getItem(adapterPosition))
            }
        }

        fun bind(request: Request) {
            binding.title.text = request.title
            binding.requestDescription.text = request.description
            binding.childName.text = request.childName
            binding.price.text = "${request.price?.toInt()} ₸"

            // Change line color based on request status
            when (request.status) {
                "Done" -> binding.line.setBackgroundResource(R.color.green)
                "In process" -> binding.line.setBackgroundResource(R.color.yellow)
                else -> binding.line.setBackgroundResource(R.color.blue)
            }

            val currentUser = auth.currentUser
            if (currentUser != null) {
                db.collection("users").document(currentUser.uid).get()
                    .addOnSuccessListener { document ->
                        val role = document.getString("role")
                        if (role == "admin") {
                            binding.markAsDoneButton.visibility = View.VISIBLE
                            binding.markAsDoneButton.setOnClickListener {
                                markRequestAsDone(request)
                            }
                        } else {
                            binding.markAsDoneButton.visibility = View.GONE
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this.itemView.context, "Ошибка при получении роли", Toast.LENGTH_SHORT).show()
                        binding.markAsDoneButton.visibility = View.GONE
                    }
            }

            val db = FirebaseFirestore.getInstance()
            val childId = request.childID
            if (!childId.isNullOrEmpty()) {
                db.collection("children")
                    .document(childId)
                    .get()
                    .addOnSuccessListener { doc ->
                        val photoUrl = doc.getString("photoUrl")
                        if (!photoUrl.isNullOrEmpty()) {
                            Glide.with(binding.childProfilePicture.context)
                                .load(photoUrl)
                                .placeholder(R.drawable.baseline_account_circle_24)
                                .into(binding.childProfilePicture)
                        }
                    }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private fun markRequestAsDone(request: Request) {
        val db = FirebaseFirestore.getInstance()

        db.collection("requests")
            .whereEqualTo("title", request.title)
            .whereEqualTo("description", request.description)
            .whereEqualTo("childID", request.childID)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    db.collection("requests")
                        .document(document.id)
                        .update("status", "Done")
                }
            }
    }

}