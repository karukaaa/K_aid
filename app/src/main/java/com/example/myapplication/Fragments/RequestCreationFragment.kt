package com.example.myapplication.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentLogInBinding
import com.example.myapplication.databinding.FragmentRequestCreationBinding

class RequestCreationFragment : Fragment() {

    private var _binding: FragmentRequestCreationBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRequestCreationBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        fun newInstance(param1: String, param2: String) = RequestCreationFragment()
    }
}