package com.example.myapplication.Fragments

import android.health.connect.datatypes.units.Length
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentLogInBinding

class LogInFragment : Fragment() {

    private var _binding: FragmentLogInBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLogInBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {

        fun newInstance(param1: String, param2: String) = LogInFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginButton.setOnClickListener{
            Toast.makeText(requireContext(), "Login button clicked!", Toast.LENGTH_SHORT).show()
        }

        binding.signUp.setOnClickListener{
            Toast.makeText(requireContext(), "Sign up button clicked!", Toast.LENGTH_SHORT).show()
        }

        binding.forgotYourPassword.setOnClickListener{
            Toast.makeText(requireContext(), "Forgot Password Clicked!", Toast.LENGTH_SHORT).show()
        }

    }
}