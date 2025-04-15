package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.RequestBinding

class RequestRecyclerViewAdapter(
    private val onItemClick: (Request) -> Unit
)
 : ListAdapter<Request, RequestRecyclerViewAdapter.ViewHolder>(RequestCallback()){


    inner class ViewHolder(private val binding : RequestBinding) : RecyclerView.ViewHolder(binding.root){
        init {
            itemView.setOnClickListener {
                onItemClick(getItem(adapterPosition))
            }
        }

        fun bind(request: Request) {
            binding.title.text = request.title ?: "No title"
            binding.requestDescription.text = request.description ?: "No description"
            binding.childName.text = request.childName ?: "No name"
            binding.price.text = "${request.price ?: 0.0} ₸"
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