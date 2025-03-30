package com.example.myapplication.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.RequestRecyclerViewAdapter
import com.example.myapplication.RequestsViewModel


class RequestsListFragment : Fragment() {

    private val viewModel: RequestsViewModel by lazy {
        ViewModelProvider(requireActivity()).get(RequestsViewModel::class.java)
    }
    private lateinit var adapter: RequestRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_requests_list, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        adapter = RequestRecyclerViewAdapter {
            openRequestDetailFragment()
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Observe the ViewModel for changes
        viewModel.requests.observe(viewLifecycleOwner) { newRequests ->
            adapter.submitList(newRequests)
        }

        return view
    }

    companion object {
        fun newInstance() {}
    }

    private fun openRequestDetailFragment() {
        val fragment = ChildProfileFragment.newInstance()

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}