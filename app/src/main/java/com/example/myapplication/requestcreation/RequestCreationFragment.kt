package com.example.myapplication.requestcreation

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.databinding.FragmentRequestCreationBinding
import com.example.myapplication.requestlist.Request
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

class RequestCreationFragment : Fragment() {

    private var _binding: FragmentRequestCreationBinding? = null
    private val binding get() = _binding!!

    private val childNames = mutableListOf<String>()
    private val childIdMap = mutableMapOf<String, String>()

    private val firestore = FirebaseFirestore.getInstance()

    private lateinit var database: AppDatabase


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequestCreationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        database = AppDatabase.getDatabase(requireContext())

        fetchChildren()

        binding.publishButton.setOnClickListener {
            val title = binding.title.text.toString().trim()
            val description = binding.description.text.toString().trim()
            val childName = binding.childSpinner.selectedItem?.toString()?.trim()
            val childID = childIdMap[childName]
            val priceText = binding.price.text.toString().trim()
            val kaspiUrl = binding.shopUrl.text.toString().trim()

            if (title.isEmpty() || description.isEmpty() || childName.isNullOrEmpty() || priceText.isEmpty() || kaspiUrl.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val price = priceText.toDoubleOrNull()
            if (price == null || price <= 0) {
                Toast.makeText(
                    requireContext(),
                    "Please enter a valid price (numbers only).",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val request = Request(
                title = title,
                description = description,
                childName = childName,
                childID = childID ?: "",
                price = price,
                photoUrl = "",
                kaspiUrl = kaspiUrl,
                status = "Waiting approval"
            )
            saveRequestToFirestore(request)

        }
    }


    private fun saveRequestToFirestore(request: Request) {
        if (isOnline()) {
            firestore.collection("requests")
                .add(request)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Request uploaded!", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
                .addOnFailureListener {
                    saveRequestLocally(request)
                }
        } else {
            saveRequestLocally(request)
        }
    }

    private fun saveRequestLocally(request: Request) {
        val db = AppDatabase.getDatabase(requireContext())
        lifecycleScope.launch {
            db.requestDao().insertRequest(request)
        }

        Toast.makeText(
            requireContext(),
            "Saved locally. Will upload when online.",
            Toast.LENGTH_SHORT
        ).show()
    }


    private fun fetchChildren() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        firestore.collection("users").document(currentUserId).get()
            .addOnSuccessListener { userDoc ->
                val userOrphanageId = userDoc.getString("orphanageID") ?: ""

                firestore.collection("children")
                    .whereEqualTo("orphanageID", userOrphanageId)
                    .get()
                    .addOnSuccessListener { result ->
                        childNames.clear()
                        childIdMap.clear()
                        for (document in result) {
                            val name = document.getString("childName") ?: continue
                            val id = document.id
                            childNames.add(name)
                            childIdMap[name] = id
                        }

                        val adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            childNames
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.childSpinner.adapter = adapter
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to load children", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load user info", Toast.LENGTH_SHORT).show()
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun isOnline(): Boolean {
        val cm =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo?.isConnectedOrConnecting == true
    }
}
