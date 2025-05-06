package com.example.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.requestlist.Request
import com.example.myapplication.requestlist.RequestRecyclerViewAdapter
import com.example.myapplication.reviews.Review
import com.example.myapplication.reviews.ReviewAdapter
import com.google.firebase.firestore.FirebaseFirestore


class ApprovingRequestsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RequestRecyclerViewAdapter
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_approving_requests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = RequestRecyclerViewAdapter(
            onItemClick = {},
            onStatusChanged = { loadPendingRequests() }
        )
        recyclerView.adapter = adapter

        loadPendingRequests()

        val backButton = view.findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun loadPendingRequests() {
        firestore.collection("requests")
            .whereEqualTo("status", "Waiting approval")
            .get()
            .addOnSuccessListener { result ->
                val requests = result.documents.mapNotNull { doc ->
                    val request = doc.toObject(Request::class.java)
                    request?.copy(firestoreId = doc.id) // Set the Firestore document ID into Review object
                }
                adapter.submitList(requests)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load reviews", Toast.LENGTH_SHORT)
                    .show()
            }
    }
}