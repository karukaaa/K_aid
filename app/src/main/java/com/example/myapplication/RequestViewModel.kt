package com.example.myapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject

class RequestsViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _requests = MutableLiveData<List<Request>>()
    val requests: LiveData<List<Request>> get() = _requests

    init {
        fetchRequests()
    }

    private fun fetchRequests() {
        db.collection("requests")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.documents.mapNotNull { it.toObject<Request>() }
                _requests.value = list
            }
            .addOnFailureListener {
                _requests.value = emptyList() // Optional: You can handle errors differently
            }
    }

    fun addRequest(request: Request) {
        val requestMap = hashMapOf(
            "id" to request.id,
            "requestedObject" to request.title,
            "description" to request.description,
            "child" to request.childName,
            "price" to request.price,
            "status" to "ожидает",
            "photoUrl" to request.photoUrl,
            "createdAt" to com.google.firebase.Timestamp.now()
        )

        db.collection("requests")
            .add(requestMap)
            .addOnSuccessListener {
                fetchRequests() // Refresh the list
            }
            .addOnFailureListener {
                // Optional: Show error message to user
            }
    }
}