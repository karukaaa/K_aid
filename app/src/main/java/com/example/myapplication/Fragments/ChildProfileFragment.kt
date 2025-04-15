package com.example.myapplication.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.Request
import com.example.myapplication.RequestAdapter
import com.google.firebase.firestore.FirebaseFirestore

class ChildProfileFragment : Fragment() {

    private lateinit var requestAdapter: RequestAdapter

    companion object {
        private const val ARG_CHILD_ID = "childID"

        fun newInstance(childID: String?): ChildProfileFragment {
            val fragment = ChildProfileFragment()
            val args = Bundle()
            args.putString(ARG_CHILD_ID, childID)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_child_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.child_requests_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        requestAdapter = RequestAdapter { request ->
            val fragment = DonateFragment.newInstance(request.title, request.description, request.price, request.kaspiUrl)
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        recyclerView.adapter = requestAdapter

        val nameText = view.findViewById<TextView>(R.id.child_name)
        val ageText = view.findViewById<TextView>(R.id.child_age)
        val aboutText = view.findViewById<TextView>(R.id.about_text)
        val bioText = view.findViewById<TextView>(R.id.bio_text)
        val backButton = view.findViewById<ImageView>(R.id.back_button)

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val childID = arguments?.getString(ARG_CHILD_ID)
        if (childID == null) {
            Toast.makeText(requireContext(), "Child ID is missing", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        val firestore = FirebaseFirestore.getInstance()

        // Load child info first
        firestore.collection("children")
            .document(childID)
            .get()
            .addOnSuccessListener { doc ->
                val name = doc.getString("childName") ?: "Unknown"
                val age = doc.getLong("childAge")?.toInt() ?: -1
                val bio = doc.getString("childBio") ?: "No bio available."

                nameText.text = name
                ageText.text = if (age > 0) "Is $age years old" else "Age unknown"
                bioText.text = bio
                aboutText.text = "✨ About $name"

                // 🔍 Load requests for this child
                firestore.collection("requests")
                    .whereEqualTo("childName", name)
                    .get()
                    .addOnSuccessListener { result ->
                        val requests = result.documents.mapNotNull { it.toObject(Request::class.java) }
                        requestAdapter.submitList(requests)
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to load requests", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load child info", Toast.LENGTH_SHORT).show()
            }
    }
}
