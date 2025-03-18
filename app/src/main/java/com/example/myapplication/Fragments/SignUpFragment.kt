package com.example.myapplication.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentLogInBinding
import com.example.myapplication.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        fun newInstance(param1: String, param2: String) = SignUpFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nextButton.setOnClickListener{
            Toast.makeText(requireContext(), "Next button clicked!", Toast.LENGTH_SHORT).show()
        }

        binding.login.setOnClickListener {
            val newFragment = LogInFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, newFragment)
                .commit()
        }

        binding.uploadPhoto.setOnClickListener {
            Toast.makeText(requireContext(), "Upload photo button clicked!", Toast.LENGTH_SHORT).show()
        }
    }
}