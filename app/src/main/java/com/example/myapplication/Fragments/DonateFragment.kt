package com.example.myapplication.Fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
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

        val instructionText = view.findViewById<TextView>(R.id.instruction_text)
        val descriptionText = view.findViewById<TextView>(R.id.request_description)
        val titleText = view.findViewById<TextView>(R.id.title)
        val priceText = view.findViewById<TextView>(R.id.price)
        val shopUrlText = view.findViewById<TextView>(R.id.shop_url)
        val emailButton = view.findViewById<Button>(R.id.email_button)
        val backButton = view.findViewById<ImageView>(R.id.back_button)

        if (request == null) {
            instructionText.text = "Something went wrong. Request is missing."
            return view
        }

        titleText.text = request.title
        descriptionText.text = request.description
        priceText.text = String.format("%.0f ₸", request.price)

        shopUrlText.apply {
            text = "🛒 Open in Kaspi Shop"
            paint.isUnderlineText = true
            setTextColor(resources.getColor(R.color.blue, null))
            setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(request.kaspiUrl)))
            }
        }

        // Fetch orphanage address using childID
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("children")
            .document(request.childID)
            .get()
            .addOnSuccessListener { childSnapshot ->
                val orphanageID = childSnapshot.getString("orphanageID")
                if (orphanageID != null) {
                    firestore.collection("orphanages")
                        .document(orphanageID)
                        .get()
                        .addOnSuccessListener { orphanageSnapshot ->
                            val address = orphanageSnapshot.getString("address") ?: "Unknown address"
                            instructionText.text = """
                                You're about to donate the requested item: "${request.title}".
                                
                                To make this donation:
                                1. Tap the link below to open the product in Kaspi online shop.
                                2. Purchase the item and set the delivery address to:
                                   📦 $address
                                3. Save the Kaspi check after purchase.
                                4. Tap the button below and attach the check to your email.
                                5. Once the delivery arrives and the child receives the gift, you'll receive a photo report in email.
                            """.trimIndent()
                        }
                } else {
                    instructionText.text = "Orphanage info not found."
                }
            }

        emailButton.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf("arukhanym.zhaidary@kbtu.kz"))
                putExtra(Intent.EXTRA_SUBJECT, "Donation Receipt for \"${request.title}\"")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Hi,\n\nPlease find attached the receipt for the donation of \"${request.title}\".\n\nBest regards."
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
