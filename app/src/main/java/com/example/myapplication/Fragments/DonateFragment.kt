package com.example.myapplication.Fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.Request
import com.google.firebase.firestore.FirebaseFirestore

class DonateFragment : Fragment() {

    companion object {
        private const val ARG_REQUEST = "request"

        fun newInstance(request: Request): DonateFragment {
            val fragment = DonateFragment()
            val bundle = Bundle().apply {
                putSerializable(ARG_REQUEST, request)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_donate, container, false)

        val request = arguments?.getSerializable(ARG_REQUEST) as? Request


        val shopUrlText = view.findViewById<TextView>(R.id.shop_url)
        val emailButton = view.findViewById<Button>(R.id.email_button)
        val backButton = view.findViewById<ImageView>(R.id.back_button)

        shopUrlText.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(request?.kaspiUrl)))

        }

        // Fetch orphanage address using childID
        val firestore = FirebaseFirestore.getInstance()
        if (request != null) {
            request.childID?.let {
                firestore.collection("children")
                    .document(it)
                    .get()
                    .addOnSuccessListener { childSnapshot ->
                        val orphanageID = childSnapshot.getString("orphanageID")
                        if (orphanageID != null) {
                            firestore.collection("orphanages")
                                .document(orphanageID)
                                .get()
                                .addOnSuccessListener { orphanageSnapshot ->
                                    val address =
                                        orphanageSnapshot.getString("address") ?: "Unknown address"

                                    val addressTextView = view.findViewById<TextView>(R.id.address)
                                    addressTextView.text = address
                                }
                                .addOnFailureListener { e ->
                                    val addressTextView = view.findViewById<TextView>(R.id.address)
                                    addressTextView.text = "Failed to fetch address"
                                }
                        }
                    }
            }
        }

        emailButton.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf("arukhanym.zhaidary@kbtu.kz"))
                putExtra(Intent.EXTRA_SUBJECT, "Donation Receipt for \"${request?.title}\"")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Hi,\n\nPlease find attached the receipt for the donation of \"${request?.title}\".\n\nBest regards."
                )
            }
            startActivity(Intent.createChooser(emailIntent, "Send email via..."))
        }

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }
}