package com.example.myapplication.childprofile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.ItemRequestBinding
import com.example.myapplication.requestlist.Request
import com.example.myapplication.requestlist.RequestCallback


class RequestAdapter(
    private val onDonateClick: (Request) -> Unit,
    private val isAdmin: Boolean
) : ListAdapter<Request, RequestAdapter.ViewHolder>(RequestCallback()) {

    inner class ViewHolder(val binding: ItemRequestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(request: Request, onDonateClick: (Request) -> Unit) {
            binding.title.text = request.title
            binding.requestDescription.text = request.description
            binding.price.text = "${request.price} ₸"

            val donateButton = binding.donateButton
            val statusText = binding.requestStatus
            val lineView = binding.line

            when (request.status) {
                "Waiting" -> {
                    lineView.setBackgroundResource(R.color.blue)
                    statusText.setTextColor(binding.root.context.getColor(R.color.blue))

                    if (isAdmin) {
                        donateButton.visibility = View.GONE
                        statusText.visibility = View.VISIBLE
                        statusText.text = "Status: Waiting"
                    } else {
                        donateButton.visibility = View.VISIBLE
                        statusText.visibility = View.GONE
                        donateButton.setOnClickListener {
                            onDonateClick(request)
                        }
                    }
                }

                "In process" -> {
                    lineView.setBackgroundResource(R.color.yellow)
                    statusText.setTextColor(binding.root.context.getColor(R.color.yellow))

                    donateButton.visibility = View.GONE
                    statusText.visibility = View.VISIBLE
                    statusText.text = "Status: In process"
                }

                "Done" -> {
                    lineView.setBackgroundResource(R.color.green)
                    statusText.setTextColor(binding.root.context.getColor(R.color.green))

                    donateButton.visibility = View.GONE
                    statusText.visibility = View.VISIBLE
                    statusText.text = "Status: Done"
                }

                else -> {
                    lineView.setBackgroundResource(R.color.blue)
                    statusText.setTextColor(binding.root.context.getColor(R.color.blue))

                    donateButton.visibility = View.GONE
                    statusText.visibility = View.VISIBLE
                    statusText.text = "Status: Unknown"
                }
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

