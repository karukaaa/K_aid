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
            binding.requestedObject.text = request.requestedObject
            binding.requestDescription.text = request.description
            binding.requestChild.text = request.child
            binding.requestPrice.text = "$" + request.price.toString()
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