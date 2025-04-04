package com.example.myapplication.Fragments

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.ChildSampleData
import com.example.myapplication.Request
import com.example.myapplication.RequestsViewModel
import com.example.myapplication.databinding.FragmentRequestCreationBinding
import kotlin.random.Random


class RequestCreationFragment : Fragment() {

    private var _binding: FragmentRequestCreationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RequestsViewModel by lazy {
        ViewModelProvider(requireActivity()).get(RequestsViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRequestCreationBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        fun newInstance() = RequestCreationFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backButton: ImageView = binding.backButton
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val children = ChildSampleData.sampleChildren
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, children)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.childSpinner.adapter = adapter

        val titleEditText: EditText = binding.title
        val descriptionEditText: EditText = binding.description
        val priceEditText: EditText = binding.price
        val publishButton: Button = binding.publishButton

        publishButton.setOnClickListener {
            val title = titleEditText.text.toString().trim()
            val description = descriptionEditText.text.toString().trim()
            val selectedChild = binding.childSpinner.selectedItem?.toString()?.trim() ?: ""
            val priceText = priceEditText.text.toString().trim()

            if (title.isEmpty() || description.isEmpty() || selectedChild.isEmpty() || priceText.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields!", Toast.LENGTH_SHORT)
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

            val newRequest = Request(
                id = Random.nextInt(1000),
                requestedObject = title,
                description = description,
                child = selectedChild,
                price = price
            )

            viewModel.addRequest(newRequest)
            Toast.makeText(requireContext(), "Request created successfully!", Toast.LENGTH_SHORT)
                .show()

            parentFragmentManager.popBackStack()
        }
    }
}