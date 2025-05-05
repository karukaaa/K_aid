package com.example.myapplication.requestlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.childprofile.ChildProfileFragment


class RequestsListFragment : Fragment() {

    private val viewModel: RequestsViewModel by lazy {
        ViewModelProvider(requireActivity()).get(RequestsViewModel::class.java)
    }
    private lateinit var adapter: RequestRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_requests_list, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        adapter = RequestRecyclerViewAdapter { request ->
            openChildProfileFragment(request.childID)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        val spinner: Spinner = view.findViewById(R.id.status_filter_spinner)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedStatus = parent.getItemAtPosition(position) as String
                viewModel.filterRequestsByStatus(selectedStatus)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Observe the ViewModel for changes
        viewModel.requests.observe(viewLifecycleOwner) { newRequests ->
            adapter.submitList(newRequests)
        }

        return view
    }

    companion object {
        fun newInstance() {}
    }

    private fun openChildProfileFragment(childID: String?) {
        val fragment = ChildProfileFragment.newInstance(childID)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

}