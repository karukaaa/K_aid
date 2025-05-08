package com.example.myapplication.requestlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.perf.FirebasePerformance

class RequestsViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _requests = MutableLiveData<List<Request>>()
    val requests: LiveData<List<Request>> get() = _requests

    private var allRequests: List<Request> = emptyList()

    init {
        fetchRequests()
    }

    private fun fetchRequests() {
        val trace = FirebasePerformance.getInstance().newTrace("load_requests_trace")
        trace.start()

        var firstSnapshotHandled = false

        db.collection("requests")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _requests.value = emptyList()
                    allRequests = emptyList()
                    return@addSnapshotListener
                }

                if (!firstSnapshotHandled) {
                    trace.stop()
                    firstSnapshotHandled = true
                }

                val list = snapshot?.documents?.mapNotNull { doc ->
                    val request = doc.toObject(Request::class.java)
                    request?.firestoreId = doc.id
                    request
                }?.filter { it.status in listOf("Waiting", "In process", "Done") }
                    ?: emptyList()

                allRequests = list
                _requests.value = list.filter { it.status != "Waiting approval" && it.status != "Rejected" }
            }

    }

    fun filterRequestsByStatus(status: String) {
        val filtered = when (status) {
            "All" -> allRequests.filter { it.status != "Waiting approval" }
            else -> allRequests.filter { it.status == status }
        }

        _requests.value = filtered
    }
}
