package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.profileauth.LogInFragment
import com.example.myapplication.reviews.Review
import com.example.myapplication.reviews.ReviewAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.perf.FirebasePerformance

class HomeFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReviewAdapter
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        return view
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LogInFragment())
                .commit()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.review_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = ReviewAdapter(approvalMode = false) // reuse adapter in read-only mode
        recyclerView.adapter = adapter

        loadApprovedReviews()
    }

    private fun loadApprovedReviews() {
        //Performance testing
        val trace = FirebasePerformance.getInstance().newTrace("load_requests_history")
        trace.start()

        firestore.collection("reviews")
            .whereEqualTo("status", "Approved")
            .get()
            .addOnSuccessListener { result ->
                trace.stop()
                val reviews = result.documents.mapNotNull { doc ->
                    val review = doc.toObject(Review::class.java)
                    review?.copy(id = doc.id)
                }
                adapter.submitList(reviews)
            }
            .addOnFailureListener {
                trace.stop()
                Toast.makeText(requireContext(), "Failed to load reviews", Toast.LENGTH_SHORT)
                    .show()
            }
    }
}
