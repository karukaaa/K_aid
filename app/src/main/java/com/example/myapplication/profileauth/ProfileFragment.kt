package com.example.myapplication.profileauth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentProfileBinding
import com.example.myapplication.requestcreation.RequestCreationFragment
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
                    val role = document.getString("role")
                    if (role == "admin") {
                        binding.approvingReviewsButton.visibility = View.VISIBLE
                    } else if(role == "user"){
                        binding.reviewButton.visibility = View.VISIBLE
                        binding.historyButton.visibility = View.VISIBLE
                    } else if(role == "orphanage employee"){
                        binding.btnCreateRequest.visibility = View.VISIBLE
                        binding.historyButton.visibility = View.VISIBLE
                        Toast.makeText(requireContext(), "Вы вошли как employee", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    if (!isAdded || _binding == null) return@addOnFailureListener
                    Toast.makeText(
                        requireContext(),
                        "Ошибка при получении роли",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.btnCreateRequest.visibility = View.GONE
                    binding.reviewButton.visibility = View.GONE
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
