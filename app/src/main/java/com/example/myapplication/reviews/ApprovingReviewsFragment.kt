package com.example.myapplication.reviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.google.firebase.firestore.FirebaseFirestore


class ApprovingReviewsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReviewAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private val reviewList = mutableListOf<Review>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_approving_reviews, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.review_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ReviewAdapter()
        recyclerView.adapter = adapter

        loadPendingReviews()

        val backButton = view.findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun loadPendingReviews() {
        firestore.collection("reviews")
            .whereEqualTo("status", "Waiting")
            .get()
            .addOnSuccessListener { result ->
                val reviews = result.documents.mapNotNull { doc ->
                    val review = doc.toObject(Review::class.java)
                    review?.copy(id = doc.id) // Set the Firestore document ID into Review object
                }
                adapter.submitList(reviews)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load reviews", Toast.LENGTH_SHORT)
                    .show()
            }
    }
}