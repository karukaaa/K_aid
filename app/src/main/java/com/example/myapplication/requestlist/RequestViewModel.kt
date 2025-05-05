package com.example.myapplication.requestlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class RequestsViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _requests = MutableLiveData<List<Request>>()
    val requests: LiveData<List<Request>> get() = _requests

    private var allRequests: List<Request> = emptyList()


    init {
        fetchRequests()
    }

    private fun fetchRequests() {
        db.collection("requests")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _requests.value = emptyList()
                    allRequests = emptyList()
                    return@addSnapshotListener
                }

                val list = snapshot?.documents?.mapNotNull { it.toObject(Request::class.java) }
                    ?: emptyList()
                allRequests = list
                _requests.value = list
            }
    }


    fun filterRequestsByStatus(status: String?) {
        _requests.value = if (status == null || status == "All") {
            allRequests
        } else {
            allRequests.filter { it.status == status }
        }
    }



}