package com.example.myapplication.profileauth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.addingchildren.AddChildrenFragment
import com.example.myapplication.addingchildren.ApproveChildrenFragment
import com.example.myapplication.databinding.FragmentProfileBinding
import com.example.myapplication.requestcreation.ApprovingRequestsFragment
import com.example.myapplication.requestcreation.RequestCreationFragment
import com.example.myapplication.requestshistory.HistoryFragment
import com.example.myapplication.reviews.ApprovingReviewsFragment
import com.example.myapplication.reviews.LeavingReviewFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    if (!isAdded || _binding == null) return@addOnSuccessListener

                    val firstName = document.getString("firstName") ?: ""
                    val lastName = document.getString("lastName") ?: ""
                    val role = document.getString("role") ?: ""

                    binding.userName.text = "$firstName $lastName"

                    when (role) {
                        "admin" -> {
                            binding.userRole.text = "Admin"
                            binding.userRole.visibility = View.VISIBLE
                            binding.approvingReviewsButton.visibility = View.VISIBLE
                            binding.approvingRequestsButton.visibility = View.VISIBLE
                            binding.approveChildren.visibility = View.VISIBLE
                        }

                        "orphanage employee" -> {
                            val orphanageId = document.getString("orphanageID")
                            if (!orphanageId.isNullOrEmpty()) {
                                db.collection("orphanages").document(orphanageId).get()
                                    .addOnSuccessListener { orphanageDoc ->
                                        val orphanageName =
                                            orphanageDoc.getString("orphanageName") ?: ""
                                        binding.userRole.text = "Employee at: $orphanageName"
                                        binding.userRole.visibility = View.VISIBLE
                                    }
                            }
                            binding.btnCreateRequest.visibility = View.VISIBLE
                            binding.historyButton.visibility = View.VISIBLE
                            binding.reviewButton.visibility = View.VISIBLE
                            binding.addChildren.visibility = View.VISIBLE
                        }

                        "user" -> {
                            binding.userRole.visibility = View.GONE
                            binding.reviewButton.visibility = View.VISIBLE
                            binding.historyButton.visibility = View.VISIBLE
                        }
                    }
                }
                .addOnFailureListener {
                    if (!isAdded || _binding == null) return@addOnFailureListener
                    Toast.makeText(requireContext(), "Error fetching user data", Toast.LENGTH_SHORT)
                        .show()
                }
        }

        binding.btnCreateRequest.setOnClickListener {
            val newFragment = RequestCreationFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, newFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            (activity as? MainActivity)?.onLogout()
        }

        binding.historyButton.setOnClickListener {
            val newFragment = HistoryFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, newFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.reviewButton.setOnClickListener {
            val newFragment = LeavingReviewFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, newFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.approvingReviewsButton.setOnClickListener {
            val newFragment = ApprovingReviewsFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, newFragment)
                .addToBackStack(null).commit()
        }

        binding.approvingRequestsButton.setOnClickListener {
            val newFragment = ApprovingRequestsFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, newFragment)
                .addToBackStack(null).commit()
        }

        binding.addChildren.setOnClickListener {
            val newFragment = AddChildrenFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, newFragment)
                .addToBackStack(null).commit()
        }

        binding.approveChildren.setOnClickListener {
            val newFragment = ApproveChildrenFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, newFragment)
                .addToBackStack(null).commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
