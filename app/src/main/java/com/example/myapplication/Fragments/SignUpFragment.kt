package com.example.myapplication.Fragments

import android.os.Bundle
import com.example.myapplication.Fragments.LogInFragment
import com.example.myapplication.Fragments.HomeFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        // Клик на кнопку регистрации
        binding.nextButton.setOnClickListener {
            // Получаем введенные данные
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()
            val confirmPassword = binding.confirmPassword.text.toString().trim()

            // Проверяем, что данные введены правильно
            if (email.isNotEmpty() && password.length >= 6 && password == confirmPassword) {
                // Создание нового пользователя
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            val uid = user?.uid

                            val db = FirebaseFirestore.getInstance()
                            val userData = hashMapOf(
                                "role" to "user",
                                "email" to email
                            )

                            db.collection("users").document(uid!!)
                                .set(userData)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        requireContext(),
                                        "Регистрация прошла успешно!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    parentFragmentManager.beginTransaction()
                                        .replace(R.id.fragment_container, LogInFragment())
                                        .commit()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        requireContext(),
                                        "Ошибка при сохранении данных: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                        } else {
                            // Ошибка
                            Toast.makeText(
                                requireContext(),
                                "Ошибка: ${task.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            } else {
                // Если данные невалидны
                Toast.makeText(
                    requireContext(),
                    "Проверьте Email, пароль (мин. 6 символов) и совпадение паролей",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Переход к экрану входа
        binding.login.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LogInFragment())
                .commit()
        }

        // Для отладки, чтобы убедиться, что фрагмент загружен и кнопка работает
        binding.uploadPhoto.setOnClickListener {
            Toast.makeText(requireContext(), "SignUpFragment открыт", Toast.LENGTH_SHORT).show()
        }

        // Дополнительный Toast для проверки, что кнопка "Next" сработала
        Toast.makeText(requireContext(), "SignUpFragment загружен", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}