package com.example.myapplication.Fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.myapplication.Request
import com.example.myapplication.databinding.FragmentRequestCreationBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class RequestCreationFragment : Fragment() {

    private var _binding: FragmentRequestCreationBinding? = null
    private val binding get() = _binding!!

    private val childNames = mutableListOf<String>()
    private val childIdMap = mutableMapOf<String, String>()

    private var imageUri: Uri? = null
    private val storage = FirebaseStorage.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imageUri = uri
            binding.imagePreview.setImageURI(uri)
        }
    }

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

        binding.selectImageButton.setOnClickListener {
            pickImage.launch("image/*")
        }

        fetchChildren()

        binding.publishButton.setOnClickListener {
            val title = binding.title.text.toString().trim()
            val description = binding.description.text.toString().trim()
            val childName = binding.childSpinner.selectedItem?.toString()?.trim()
            val childID = childIdMap[childName]
            val priceText = binding.price.text.toString().trim()
            val kaspiUrl = binding.shopUrl.text.toString().trim()

            if (title.isEmpty() || description.isEmpty() || childName.isNullOrEmpty() || priceText.isEmpty() || kaspiUrl.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val price = priceText.toDoubleOrNull()
            if (price == null || price <= 0) {
                Toast.makeText(requireContext(), "Please enter a valid price", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (imageUri != null) {
                uploadImageAndSaveRequest(title, description, childName, childID, price, kaspiUrl, imageUri!!)
            } else {
                val request = Request(
                    title = title,
                    description = description,
                    childName = childName,
                    childID = childID ?: "",
                    price = price,
                    photoUrl = "",  // you can generate UUID here if needed
                    kaspiUrl = kaspiUrl,
                )
                saveRequestToFirestore(request)
            }
        }
    }

    private fun uploadImageAndSaveRequest(
        title: String,
        description: String,
        childName: String,
        childID: String?,
        price: Double,
        kaspiUrl: String,
        imageUri: Uri
    ) {
        val fileName = "photos/${UUID.randomUUID()}.jpg"
        val imageRef = storage.reference.child(fileName)

        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val request = Request(
                        title = title,
                        description = description,
                        childName = childName,
                        childID = childID ?: "",
                        price = price,
                        photoUrl = uri.toString(),
                        kaspiUrl = kaspiUrl
                    )
                    saveRequestToFirestore(request)
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Photo upload failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveRequestToFirestore(request: Request) {
        firestore.collection("requests")
            .add(request)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Request created successfully!", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error saving request", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchChildren() {
        firestore.collection("children").get()
            .addOnSuccessListener { result ->
                childNames.clear()
                childIdMap.clear()
                for (document in result) {
                    val name = document.getString("childName") ?: continue
                    val id = document.id
                    childNames.add(name)
                    childIdMap[name] = id
                }

                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, childNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.childSpinner.adapter = adapter
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load children", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = RequestCreationFragment()
    }
}
