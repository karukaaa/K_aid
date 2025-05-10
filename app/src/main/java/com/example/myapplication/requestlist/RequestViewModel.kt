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
    private var currentStatus: String = "All"
    private var currentSearchQuery: String = ""

    init {
        fetchRequests()
    }

    private fun fetchRequests() {
        val trace = FirebasePerformance.getInstance().newTrace("load_requests_trace")
        trace.start()

        db.collection("requests")
            .addSnapshotListener { snapshot, error ->
                trace.stop()
                if (error != null || snapshot == null) {
                    _requests.value = emptyList()
                    allRequests = emptyList()
                    return@addSnapshotListener
                }

                allRequests = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Request::class.java)?.apply {
                        firestoreId = doc.id
                    }
                }.filter {
                    it.status in listOf("Waiting", "In process", "Done")
                }

                applyFilters()
            }
    }


    fun filterRequestsByStatus(status: String) {
        currentStatus = status
        applyFilters()
    }

    fun searchRequests(query: String) {
        currentSearchQuery = query
        applyFilters()
    }

    private fun applyFilters() {
        val filtered = allRequests
            .filter {
                currentStatus == "All" || it.status == currentStatus
            }
            .filter {
                currentSearchQuery.isBlank() || listOf(
                    it.title ?: "",
                    it.description ?: "",
                    it.childName ?: ""
                ).any { field -> field.contains(currentSearchQuery, ignoreCase = true) }
            }

        _requests.value = filtered
    }
}
