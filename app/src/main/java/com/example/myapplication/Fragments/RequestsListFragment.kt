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
import com.example.myapplication.RequestSampleData

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
        val adapter = RequestRecyclerViewAdapter {
            openRequestDetailFragment() // Open new fragment
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext()) // Set LayoutManager
        recyclerView.adapter = adapter


        adapter.submitList(RequestSampleData.sampleRequests)
        return view
    }

    companion object {
        fun newInstance() {}
    }

    private fun openRequestDetailFragment() {
        val fragment = ChildProfileFragment.newInstance()

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment) // Replace with new fragment
            .addToBackStack(null) // Allow back navigation
            .commit()
    }
}