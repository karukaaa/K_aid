package com.example.myapplication.requestlist

import androidx.recyclerview.widget.DiffUtil.ItemCallback

class RequestCallback : ItemCallback<Request>() {
    override fun areItemsTheSame(oldItem: Request, newItem: Request): Boolean {
        return oldItem.description == newItem.description
    }

    override fun areContentsTheSame(oldItem: Request, newItem: Request): Boolean {
        return oldItem == newItem
    }
}