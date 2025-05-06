package com.example.myapplication.profileauth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.childprofile.ChildProfileFragment
import com.example.myapplication.requestlist.Request
import com.example.myapplication.requestlist.RequestRecyclerViewAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class HistoryFragment : Fragment() {

    private lateinit var adapter: RequestRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        adapter = RequestRecyclerViewAdapter(
            onItemClick = { request -> openChildProfileFragment(request.childID) },
            onStatusChanged = {}
        )


        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            FirebaseFirestore.getInstance().collection("requests")
                .whereEqualTo("donatedBy", currentUserId)
                .get()
                .addOnSuccessListener { result ->
                    val requests = result.documents.mapNotNull { it.toObject(Request::class.java) }
                    adapter.submitList(requests)
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to load donation history", Toast.LENGTH_SHORT).show()
                }
        }

        val backButton = view.findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

    }

    private fun openChildProfileFragment(childID: String?) {
        val fragment = ChildProfileFragment.newInstance(childID)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}