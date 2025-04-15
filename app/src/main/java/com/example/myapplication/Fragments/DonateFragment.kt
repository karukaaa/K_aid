package com.example.myapplication.Fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.myapplication.R

class DonateFragment : Fragment() {

    companion object {
        private const val ARG_DESCRIPTION = "description"
        private const val ARG_TITLE = "title"
        private const val ARG_PRICE = "price"
        private const val ARG_KASPI_URL = "kaspiUrl"

        fun newInstance(title: String, description: String, price: Double, kaspiUrl: String): DonateFragment {
            val fragment = DonateFragment()
            val bundle = Bundle().apply {
                putString(ARG_TITLE, title)
                putString(ARG_DESCRIPTION, description)
                putDouble(ARG_PRICE, price)
                putString(ARG_KASPI_URL, kaspiUrl)
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

        val instructionText = view.findViewById<TextView>(R.id.instruction_text)
        val descriptionText = view.findViewById<TextView>(R.id.request_description)
        val titleText = view.findViewById<TextView>(R.id.title)
        val priceText = view.findViewById<TextView>(R.id.price)
        val shopUrlText = view.findViewById<TextView>(R.id.shop_url)
        val emailButton = view.findViewById<Button>(R.id.email_button)
        val backButton = view.findViewById<ImageView>(R.id.back_button)

        val title = arguments?.getString(ARG_TITLE) ?: "the item"
        val description = arguments?.getString(ARG_DESCRIPTION) ?: ""
        val price = arguments?.getDouble(ARG_PRICE) ?: 0.0
        val kaspiUrl = arguments?.getString(ARG_KASPI_URL) ?: "https://kaspi.kz/shop"

        titleText.text = title
        descriptionText.text = description
        priceText.text = String.format("%.0f ₸", price)

        shopUrlText.apply {
            text = "🛒 Open in Kaspi Shop"
            setTextColor(resources.getColor(R.color.blue, null))
            paint.isUnderlineText = true
            setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(kaspiUrl))
                startActivity(browserIntent)
            }
        }



        shopUrlText.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(kaspiUrl))
            startActivity(browserIntent)
        }

        instructionText.text = """
            You're about to donate the requested item: "$title".

            To make this donation:
            1. Tap the link below to open the product in Kaspi online shop.
            2. Purchase the item and set the delivery address to:
               📦 Auezova 54, Orphanage #3, Almaty, Kazakhstan
            3. Save the Kaspi check after purchase.
            4. Tap the button below and attach the check to your email.
        """.trimIndent()

        emailButton.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:example@kaid.kz") // Replace with your email
                putExtra(Intent.EXTRA_SUBJECT, "Donation Receipt for \"$title\"")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Hi,\n\nPlease find attached the receipt for the donation of \"$title\".\n\nBest regards."
                )
            }

            if (emailIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(emailIntent)
            }
        }

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }
}
