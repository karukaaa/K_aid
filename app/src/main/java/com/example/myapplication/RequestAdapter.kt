package com.example.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemRequestBinding
import com.google.android.material.button.MaterialButton


class RequestAdapter(
    private val onDonateClick: (Request) -> Unit
) : ListAdapter<Request, RequestAdapter.ViewHolder>(RequestCallback()) {

    class ViewHolder(val binding: ItemRequestBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(request: Request, onDonateClick: (Request) -> Unit) {
            binding.title.text = request.title
            binding.requestDescription.text = request.description
            binding.price.text = "${request.price} ₸"

            binding.root.findViewById<MaterialButton>(R.id.donate_button).setOnClickListener {
                onDonateClick(request)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onDonateClick)
    }
}

