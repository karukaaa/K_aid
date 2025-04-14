package com.example.myapplication

import androidx.recyclerview.widget.DiffUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemRequestBinding


class RequestAdapter : ListAdapter<Request, RequestAdapter.ViewHolder>(RequestCallback()) {

    class ViewHolder(val binding: ItemRequestBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(request: Request) {
            binding.title.text = request.title
            binding.requestDescription.text = request.description
            binding.price.text = "${request.price} ₸"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

