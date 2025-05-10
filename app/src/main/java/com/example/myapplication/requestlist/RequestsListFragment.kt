package com.example.myapplication.requestlist

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.childprofile.ChildProfileFragment
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback


class RequestsListFragment : Fragment() {

    private val viewModel: RequestsViewModel by lazy {
        ViewModelProvider(requireActivity()).get(RequestsViewModel::class.java)
    }
    private lateinit var adapter: RequestRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_requests_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            hideKeyboard()
        }


        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        adapter = RequestRecyclerViewAdapter(
            onItemClick = { request -> openChildProfileFragment(request.childID) },
            onStatusChanged = {}
        )

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

        val searchEditText: EditText = view.findViewById(R.id.search_bar)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchRequests(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        viewModel.requests.observe(viewLifecycleOwner) { newRequests ->
            if (isAdded) {
                adapter.submitList(newRequests)
            }
        }

    }


    private fun openChildProfileFragment(childID: String?) {
        val fragment = ChildProfileFragment.newInstance(childID)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }


    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        view?.let { v ->
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }
}
