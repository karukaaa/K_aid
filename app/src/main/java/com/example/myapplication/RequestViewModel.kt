package com.example.myapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RequestsViewModel : ViewModel() {

    private val _requests = MutableLiveData<List<Request>>(RequestSampleData.sampleRequests)
    val requests: LiveData<List<Request>> get() = _requests

    fun addRequest(request: Request) {
        val updatedList = _requests.value?.toMutableList() ?: mutableListOf()
        updatedList.add(0, request) // Add new request at the top
        _requests.value = updatedList
    }
}