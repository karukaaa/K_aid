package com.example.myapplication.childprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.FragmentEditChildProfileBinding
import com.google.firebase.firestore.FirebaseFirestore

class EditChildProfileFragment : Fragment() {
    private var _binding: FragmentEditChildProfileBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_CHILD_ID = "childID"

        fun newInstance(childID: String): EditChildProfileFragment {
            val fragment = EditChildProfileFragment()
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
        _binding = FragmentEditChildProfileBinding.inflate(inflater, container, false)

        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val childID = arguments?.getString(ARG_CHILD_ID) ?: return
        val db = FirebaseFirestore.getInstance()

        // Set up gender spinner
        val genderOptions = listOf("Male", "Female", "Other")
        val genderAdapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, genderOptions)
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.editGenderSpinner.adapter = genderAdapter

        // Load existing data
        db.collection("children").document(childID).get()
            .addOnSuccessListener { doc ->
                val age = doc.getLong("childAge")?.toString() ?: ""
                val gender = doc.getString("childGender") ?: ""
                val bio = doc.getString("childBio") ?: ""
                val profilePhotoUrl = doc.getString("photoUrl") ?: ""
                val photosList =
                    (doc.get("photos") as? List<*>)?.filterIsInstance<String>() ?: emptyList()

                binding.editAge.setText(age)
                binding.editBio.setText(bio)
                binding.editProfilePhotoUrl.setText(profilePhotoUrl)
                binding.editExtraPhotos.setText(photosList.joinToString(", "))

                val genderIndex = genderOptions.indexOf(gender)
                if (genderIndex != -1) binding.editGenderSpinner.setSelection(genderIndex)

                binding.saveButton.setOnClickListener {
                    val updatedAge = binding.editAge.text.toString().trim().toIntOrNull()
                    val updatedGender = binding.editGenderSpinner.selectedItem.toString()
                    val updatedBio = binding.editBio.text.toString().trim()
                    val updatedProfilePhoto = binding.editProfilePhotoUrl.text.toString().trim()
                    val updatedPhotos =
                        binding.editExtraPhotos.text.toString().split(",").map { it.trim() }
                            .filter { it.isNotEmpty() }

                    val updates = mapOf(
                        "childAge" to updatedAge,
                        "childGender" to updatedGender,
                        "childBio" to updatedBio,
                        "photoUrl" to updatedProfilePhoto,
                        "photos" to updatedPhotos
                    )

                    db.collection("children").document(childID)
                        .update(updates)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT)
                                .show()
                            parentFragmentManager.popBackStack()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Update failed", Toast.LENGTH_SHORT)
                                .show()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load child info", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
