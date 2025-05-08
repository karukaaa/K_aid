package com.example.myapplication.requestshistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.FragmentRequestDoneConfirmationBinding
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class RequestDoneConfirmationFragment : Fragment() {

    private var _binding: FragmentRequestDoneConfirmationBinding? = null
    private val binding get() = _binding!!

    private val firestore = FirebaseFirestore.getInstance()

    companion object {
        private const val ARG_REQUEST_ID = "request_id"
        private const val ARG_DONOR_ID = "donor_id"

        fun newInstance(requestId: String, donorId: String?): RequestDoneConfirmationFragment {
            val fragment = RequestDoneConfirmationFragment()
            val args = Bundle()
            args.putString(ARG_REQUEST_ID, requestId)
            args.putString(ARG_DONOR_ID, donorId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequestDoneConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val requestId = arguments?.getString(ARG_REQUEST_ID)
        val donorId = arguments?.getString(ARG_DONOR_ID)

        binding.submitButton.setOnClickListener {
            val thankYouText = binding.thankYouText.text.toString().trim()
            val photoUrl = binding.photoUrl.text.toString().trim()

            if (thankYouText.isEmpty() && photoUrl.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill at least one field", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val confirmation = hashMapOf(
                "thankYouText" to thankYouText,
                "photoUrl" to photoUrl,
                "videoUrl" to "",
                "audioUrl" to "",
                "submittedAt" to Timestamp.now(),
                "requestID" to requestId,
                "donorID" to donorId
            )

            firestore.collection("confirmations")
                .add(confirmation)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Confirmation submitted", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to submit confirmation", Toast.LENGTH_SHORT).show()
                }
        }

        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
