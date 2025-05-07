package com.example.myapplication.addingchildren

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.google.firebase.firestore.FirebaseFirestore

class ApproveChildrenFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ApproveChildrenAdapter
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_approve_children, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ApproveChildrenAdapter(onApprove = { child, docId ->
            approveChild(child, docId)
        }, onReject = { docId ->
            rejectChild(docId)
        })
        recyclerView.adapter = adapter

        view.findViewById<ImageView>(R.id.back_button).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        loadPendingChildren()
    }

    private fun loadPendingChildren() {
        firestore.collection("pending_children")
            .whereEqualTo("status", "Waiting approval")
            .get()
            .addOnSuccessListener { result ->
                val children = result.documents.mapNotNull { doc ->
                    val child = doc.toObject(PendingChild::class.java)
                    child?.let { it to doc.id }
                }
                adapter.submitList(children)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load children", Toast.LENGTH_SHORT).show()
            }
    }

    private fun approveChild(child: PendingChild, docId: String) {
        firestore.collection("children")
            .add(child.copy(status = null.toString())) // Remove status field from final version
            .addOnSuccessListener {
                firestore.collection("pending_children").document(docId)
                    .update("status", "Approved")
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Child approved", Toast.LENGTH_SHORT).show()
                        loadPendingChildren()
                    }
            }
    }

    private fun rejectChild(docId: String) {
        firestore.collection("pending_children").document(docId)
            .update("status", "Rejected")
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Child rejected", Toast.LENGTH_SHORT).show()
                loadPendingChildren()
            }
    }
}