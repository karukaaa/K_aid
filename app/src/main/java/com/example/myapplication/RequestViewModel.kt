package com.example.myapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class RequestsViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _requests = MutableLiveData<List<Request>>()
    val requests: LiveData<List<Request>> get() = _requests

    init {
        fetchRequests()
    }

    private fun fetchRequests() {
        db.collection("requests")
            .whereNotEqualTo("status", "Done") // this hides done ones
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _requests.value = emptyList()
                    return@addSnapshotListener
                }

                val list = snapshot?.documents?.mapNotNull { it.toObject(Request::class.java) }
                    ?: emptyList()
                _requests.value = list
            }
    }

}
