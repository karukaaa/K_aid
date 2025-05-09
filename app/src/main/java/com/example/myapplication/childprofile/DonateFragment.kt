package com.example.myapplication.childprofile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.requestlist.Request
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class DonateFragment : Fragment() {

    private var selectedReceiptUri: Uri? = null
    private var uploadedReceiptUrl: String? = null
    private val PICK_RECEIPT_REQUEST = 1001
    private lateinit var uploadButton: Button
    private lateinit var sendEmailButton: Button
    private lateinit var receiptStatus: TextView

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
        uploadButton = view.findViewById(R.id.upload_receipt_button)
        sendEmailButton = view.findViewById(R.id.send_email_button)
        val backButton = view.findViewById<ImageView>(R.id.back_button)
        receiptStatus = view.findViewById(R.id.receipt_status_text)

        var orphanageEmail: String? = null

        // Load orphanage address and email
        val addressTextView = view.findViewById<TextView>(R.id.address)
        request?.childID?.let { childId ->
            FirebaseFirestore.getInstance().collection("children").document(childId)
                .get().addOnSuccessListener { childDoc ->
                    val orphanageID = childDoc.getString("orphanageID")
                    if (orphanageID != null) {
                        FirebaseFirestore.getInstance().collection("orphanages").document(orphanageID)
                            .get().addOnSuccessListener { orphanageDoc ->
                                addressTextView.text = orphanageDoc.getString("address") ?: "Unknown address"
                                orphanageEmail = orphanageDoc.getString("email")
                            }
                    }
                }
        }

        shopUrlText.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(request?.kaspiUrl)))
        }

        uploadButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            startActivityForResult(Intent.createChooser(intent, "Select Receipt"), PICK_RECEIPT_REQUEST)
        }

        sendEmailButton.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "Unknown"
            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                type = "*/*"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(orphanageEmail ?: "arukhanym.zhaidary@gmail.com"))
                putExtra(Intent.EXTRA_SUBJECT, "Donation Receipt for Child Request")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Hi,\n\nThis is the receipt for the donation.\nMy user ID is: $userId\n\nBest regards."
                )
                uploadedReceiptUrl?.let {
                    putExtra(Intent.EXTRA_TEXT, "Here is the donation receipt:\n$it\n\nRegards,\nDonor")
                }
            }
            startActivity(Intent.createChooser(emailIntent, "Send email via..."))
        }

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_RECEIPT_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedReceiptUri = data?.data
            selectedReceiptUri?.let { uri ->
                uploadToFirebase(uri)
            }
        }
    }

    private fun uploadToFirebase(uri: Uri) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val fileName = uri.lastPathSegment ?: "receipt_${System.currentTimeMillis()}"
        val storageRef = FirebaseStorage.getInstance().getReference("receipts/$userId/$fileName")

        receiptStatus.text = "Uploading..."
        storageRef.putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                storageRef.downloadUrl
            }
            .addOnSuccessListener { downloadUri ->
                uploadedReceiptUrl = downloadUri.toString()
                receiptStatus.text = "Uploaded successfully ✅"
            }
            .addOnFailureListener {
                receiptStatus.text = "Upload failed ❌"
            }
    }
}
