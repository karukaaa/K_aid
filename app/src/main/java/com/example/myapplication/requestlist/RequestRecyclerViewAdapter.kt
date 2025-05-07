package com.example.myapplication.requestlist

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.RequestBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RequestRecyclerViewAdapter(
    private val onItemClick: (Request) -> Unit,
    private val onStatusChanged: () -> Unit
) : ListAdapter<Request, RequestRecyclerViewAdapter.ViewHolder>(RequestCallback()) {

    private val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    inner class ViewHolder(private val binding: RequestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                onItemClick(getItem(adapterPosition))
            }
        }

        fun updateRequestStatus(requestId: String?, newStatus: String) {
            if (requestId == null) {
                Log.d("RequestAdapter", "Request ID is null, cannot update status")
                return
            }

            Log.d("RequestAdapter", "Attempting to update request $requestId to $newStatus")

            db.collection("requests").document(requestId)
                .update("status", newStatus)
                .addOnSuccessListener {
                    Log.d("RequestAdapter", "Successfully updated status to $newStatus for $requestId")
                    Toast.makeText(itemView.context, "Status updated", Toast.LENGTH_SHORT).show()
                    onStatusChanged()
                }
                .addOnFailureListener { e ->
                    Log.d("RequestAdapter", "Failed to update status: ${e.message}")
                    Toast.makeText(itemView.context, "Failed to update status", Toast.LENGTH_SHORT).show()
                }
        }



        fun bind(request: Request) {
            binding.title.text = request.title
            binding.requestDescription.text = request.description
            binding.childName.text = request.childName
            binding.price.text = "${request.price?.toInt()} ₸"

            val spinner = binding.statusSpinner
            val approveButton = binding.approveButton
            val rejectButton = binding.rejectButton

            // Color line by status
            when (request.status) {
                "Done" -> binding.line.setBackgroundResource(R.color.green)
                "In process" -> binding.line.setBackgroundResource(R.color.yellow)
                else -> binding.line.setBackgroundResource(R.color.blue)
            }

            if (request.status == "Waiting approval") {
                approveButton.visibility = View.VISIBLE
                rejectButton.visibility = View.VISIBLE
                spinner.visibility = View.GONE
                approveButton.setOnClickListener {
                    updateRequestStatus(request.firestoreId, "Waiting")
                }
                rejectButton.setOnClickListener {
                    updateRequestStatus(request.firestoreId, "Rejected")
                }
            } else {
                approveButton.visibility = View.GONE
                rejectButton.visibility = View.GONE
            }

            val currentUser = auth.currentUser
            if (currentUser != null) {
                db.collection("users").document(currentUser.uid).get()
                    .addOnSuccessListener { userDoc ->
                        val role = userDoc.getString("role")
                        val userOrphanageId = userDoc.getString("orphanageID")
                        val childId = request.childID ?: return@addOnSuccessListener

                        if (role == "orphanage employee") {
                            db.collection("children").document(childId).get()
                                .addOnSuccessListener { childDoc ->
                                    val childOrphanageId = childDoc.getString("orphanageID")
                                    if (userOrphanageId == childOrphanageId) {
                                        spinner.visibility = View.VISIBLE

                                        // Set spinner
                                        val statuses = listOf("Waiting", "In process", "Done")
                                        val adapter = ArrayAdapter(
                                            itemView.context,
                                            android.R.layout.simple_spinner_item,
                                            statuses
                                        )
                                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                        spinner.adapter = adapter

                                        val currentIndex = statuses.indexOf(request.status)
                                        if (currentIndex >= 0) spinner.setSelection(currentIndex)

                                        val tag = "RequestAdapter"

                                        var isFirstSelection = true

                                        Log.d(tag, "Setting spinner for request: ${request.title} with status: ${request.status}")

                                        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                            override fun onItemSelected(
                                                parent: AdapterView<*>,
                                                view: View?,
                                                position: Int,
                                                id: Long
                                            ) {
                                                val newStatus = statuses[position]
                                                Log.d(tag, "Spinner item selected: $newStatus (isFirstSelection=$isFirstSelection)")

                                                if (isFirstSelection) {
                                                    isFirstSelection = false
                                                    return
                                                }

                                                if (newStatus != request.status) {
                                                    Log.d(tag, "Updating Firestore for request: ${request.firestoreId} to status: $newStatus")
                                                    updateRequestStatus(request.firestoreId, newStatus)
                                                } else {
                                                    Log.d(tag, "Selected status is the same as current. No update needed.")
                                                }
                                            }

                                            override fun onNothingSelected(parent: AdapterView<*>) {
                                                Log.d(tag, "Nothing selected in spinner")
                                            }
                                        }

                                    }
                                }
                        }
                    }
            }

            // Load child photo
            request.childID?.let { childId ->
                db.collection("children").document(childId).get()
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

}

class RequestCallback : ItemCallback<Request>() {
    override fun areItemsTheSame(oldItem: Request, newItem: Request): Boolean {
        return oldItem.description == newItem.description
    }

    override fun areContentsTheSame(oldItem: Request, newItem: Request): Boolean {
        return oldItem == newItem
    }
}