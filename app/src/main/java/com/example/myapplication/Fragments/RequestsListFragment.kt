package com.example.myapplication.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.Request
import com.example.myapplication.RequestRecyclerViewAdapter

class RequestsListFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_requests_list, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        val adapter = RequestRecyclerViewAdapter()

        recyclerView.layoutManager = LinearLayoutManager(requireContext()) // Set LayoutManager
        recyclerView.adapter = adapter


        val sampleRequests = listOf(
            Request( id = 1, requestedObject = "Shoes"),
            Request( id = 2, requestedObject = "School bag"),
            Request( id = 3, requestedObject = "Bicycle"),
            Request( id = 4, requestedObject = "Sweets"),
            Request( id = 5, requestedObject = "Books"),

        )

        adapter.submitList(sampleRequests) // Update the RecyclerView

        return view
    }

    companion object {
        fun newInstance() {}
    }
}