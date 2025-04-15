package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.RequestBinding
import com.google.firebase.firestore.FirebaseFirestore

class RequestRecyclerViewAdapter(
    private val onItemClick: (Request) -> Unit
) : ListAdapter<Request, RequestRecyclerViewAdapter.ViewHolder>(RequestCallback()) {


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
            binding.price.text = "$" + request.price.toString()

//            if (isAdmin) {                      TODO: when role based access is implemented add the check
            binding.markAsDoneButton.visibility = View.VISIBLE
            binding.markAsDoneButton.setOnClickListener {
                markRequestAsDone(request)
//                }
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