package com.example.myapplication.childprofile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.FragmentEditChildProfileBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class EditChildProfileFragment : Fragment() {
    private var _binding: FragmentEditChildProfileBinding? = null
    private val binding get() = _binding!!

    private val PICK_AUDIO_REQUEST = 1001
    private val PICK_VIDEO_REQUEST = 1002
    private var selectedAudioUri: Uri? = null
    private var selectedVideoUri: Uri? = null


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



        binding.selectAudioButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "audio/*"
            startActivityForResult(intent, PICK_AUDIO_REQUEST)
        }

        binding.selectVideoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "video/*"
            startActivityForResult(intent, PICK_VIDEO_REQUEST)
        }


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
                    val updatedPhotos = binding.editExtraPhotos.text.toString()
                        .split(", ").map { it.trim() }.filter { it.isNotEmpty() }

                    val storageRef = FirebaseStorage.getInstance().reference
                    val childDocRef = db.collection("children").document(childID)

                    val updates = mutableMapOf<String, Any>(
                        "childAge" to (updatedAge ?: 0),
                        "childGender" to updatedGender,
                        "childBio" to updatedBio,
                        "photoUrl" to updatedProfilePhoto,
                        "photos" to updatedPhotos
                    )

                    fun uploadAndSave(uri: Uri, path: String, onComplete: (String) -> Unit) {
                        val fileRef = storageRef.child("children/$childID/$path")
                        fileRef.putFile(uri).continueWithTask { fileRef.downloadUrl }
                            .addOnSuccessListener { uri -> onComplete(uri.toString()) }
                            .addOnFailureListener {
                                Toast.makeText(requireContext(), "Upload failed: $path", Toast.LENGTH_SHORT).show()
                            }
                    }

                    if (selectedAudioUri != null && selectedVideoUri != null) {
                        uploadAndSave(selectedAudioUri!!, "dream_audio.mp3") { audioUrl ->
                            updates["dreamAudioUrl"] = audioUrl
                            uploadAndSave(selectedVideoUri!!, "dream_video.mp4") { videoUrl ->
                                updates["dreamVideoUrl"] = videoUrl
                                childDocRef.update(updates).addOnSuccessListener {
                                    Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                                    parentFragmentManager.popBackStack()
                                }
                            }
                        }
                    } else if (selectedAudioUri != null) {
                        uploadAndSave(selectedAudioUri!!, "dream_audio.mp3") { audioUrl ->
                            updates["dreamAudioUrl"] = audioUrl
                            childDocRef.update(updates).addOnSuccessListener {
                                Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                                parentFragmentManager.popBackStack()
                            }
                        }
                    } else if (selectedVideoUri != null) {
                        uploadAndSave(selectedVideoUri!!, "dream_video.mp4") { videoUrl ->
                            updates["dreamVideoUrl"] = videoUrl
                            childDocRef.update(updates).addOnSuccessListener {
                                Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                                parentFragmentManager.popBackStack()
                            }
                        }
                    } else {
                        // No media selected
                        childDocRef.update(updates).addOnSuccessListener {
                            Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                            parentFragmentManager.popBackStack()
                        }
                    }
                }

            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load child info", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            when (requestCode) {
                PICK_AUDIO_REQUEST -> selectedAudioUri = data.data
                PICK_VIDEO_REQUEST -> selectedVideoUri = data.data
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
