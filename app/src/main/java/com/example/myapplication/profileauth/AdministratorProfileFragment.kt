package com.example.myapplication.profileauth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.requestcreation.RequestCreationFragment
import com.example.myapplication.databinding.FragmentAdministratorProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdministratorProfileFragment : Fragment() {

    private var _binding: FragmentAdministratorProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdministratorProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val uid = auth.currentUser?.uid
        Toast.makeText(requireContext(), "UID: $uid", Toast.LENGTH_SHORT).show()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            db.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { document ->
                    val role = document.getString("role")
                    Toast.makeText(requireContext(), "Роль: $role", Toast.LENGTH_SHORT).show()
                    if (role == "admin") {
                        binding.btnCreateRequest.visibility = View.VISIBLE
                    } else {
                        binding.btnCreateRequest.visibility = View.GONE
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Ошибка при получении роли", Toast.LENGTH_SHORT).show()
                    binding.btnCreateRequest.visibility = View.GONE
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
