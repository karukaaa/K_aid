package com.example.myapplication

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.FragmentAddChildrenBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


data class PendingChild(
    val childName: String = "",
    val childAge: Int = 0,
    val childGender: String = "",
    val childBio: String = "",
    val photoUrl: String = "",
    val photos: List<String> = emptyList(),
    val orphanageID: String = "",
    val status: String = "Waiting approval",
    val createdAt: Timestamp = Timestamp.now()
)


class AddChildrenFragment : Fragment() {

    private var _binding: FragmentAddChildrenBinding? = null
    private val binding get() = _binding!!
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddChildrenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { doc ->
                val orphanageID = doc.getString("orphanageID") ?: return@addOnSuccessListener

                val genderOptions = listOf("Female", "Male")
                val genderAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, genderOptions)
                genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.genderSpinner.adapter = genderAdapter


                binding.submitButton.setOnClickListener {
                    val name = binding.childNameInput.text.toString().trim()
                    val age = binding.childAgeInput.text.toString().toIntOrNull() ?: 0
                    val gender = binding.genderSpinner.selectedItem?.toString()?.trim() ?: ""
                    val bio = binding.childBioInput.text.toString().trim()
                    val profilePhotoUrl = binding.profilePhotoUrlInput.text.toString().trim()
                    val photosRaw = binding.photoUrlsInput.text.toString().trim()
                    val photos = photosRaw.split(",  ").map { it.trim() }.filter { it.isNotEmpty() }

                    if (name.isEmpty() || gender == "Select gender" || bio.isEmpty() || profilePhotoUrl.isEmpty() || age <= 0) {
                        Toast.makeText(requireContext(), "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }


                    val newChild = PendingChild(
                        childName = name,
                        childAge = age,
                        childGender = gender,
                        childBio = bio,
                        photoUrl = profilePhotoUrl,
                        photos = photos,
                        orphanageID = orphanageID
                    )

                    firestore.collection("pending_children")
                        .add(newChild)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Child submitted for approval", Toast.LENGTH_SHORT).show()
                            parentFragmentManager.popBackStack()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Failed to submit child", Toast.LENGTH_SHORT).show()
                        }
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}