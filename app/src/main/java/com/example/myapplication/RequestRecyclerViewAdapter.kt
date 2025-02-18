package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.RequestBinding

class RequestRecyclerViewAdapter : ListAdapter<Request, RequestRecyclerViewAdapter.ViewHolder>(RequestCallback()){

    private val requestList = ArrayList<Request>()

    inner class ViewHolder(private val binding : RequestBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(request: Request) {
            binding.text.text = request.requestedObject
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding) // Directly return ViewHolder without extra brackets
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}