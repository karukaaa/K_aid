package com.example.myapplication.reviews

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


class LeavingReviewFragment : Fragment() {

    private lateinit var reviewInput: EditText
    private lateinit var submitButton: Button
    private val firestore = FirebaseFirestore.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_leaving_review, container, false)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reviewInput = view.findViewById(R.id.reviewInput)
        submitButton = view.findViewById(R.id.submit_review_button)
        val infoMessage = view.findViewById<TextView>(R.id.review_info_text)
        val backButton = view.findViewById<ImageView>(R.id.back_button)

        reviewInput.filters = arrayOf(InputFilter.LengthFilter(500))
        submitButton.visibility = View.GONE // hide by default
        infoMessage.visibility = View.GONE  // hide by default

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance().collection("requests")
            .whereEqualTo("donatedBy", userId)
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    // User is not eligible — disable input and show info message
                    reviewInput.isEnabled = false
                    infoMessage.visibility = View.VISIBLE
                    infoMessage.text = "✨ You must fulfill at least one request to leave a review."
                } else {
                    // User is eligible
                    submitButton.visibility = View.VISIBLE
                    submitButton.setOnClickListener {
                        val reviewText = reviewInput.text.toString().trim()

                        if (reviewText.isEmpty()) {
                            Toast.makeText(
                                requireContext(),
                                "Please write a review first.",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@setOnClickListener
                        }

                        val review = hashMapOf(
                            "text" to reviewText,
                            "timestamp" to FieldValue.serverTimestamp(),
                            "status" to "Waiting",
                            "userId" to userId
                        )

                        firestore.collection("reviews")
                            .add(review)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    requireContext(),
                                    "Review submitted for approval",
                                    Toast.LENGTH_SHORT
                                ).show()
                                parentFragmentManager.popBackStack()
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    requireContext(),
                                    "Error submitting review",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Failed to check donation status",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

}