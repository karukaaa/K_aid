package com.example.myapplication.Fragments

import com.example.myapplication.Fragments.LogInFragment  // Если фрагмент находится в папке Fragments
import com.example.myapplication.Fragments.SignUpFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Инициализируем FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Возвращаем разметку фрагмента
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Кнопка выхода из аккаунта
        val logoutButton: Button = view.findViewById(R.id.logout_button)
        logoutButton.setOnClickListener {
            auth.signOut()  // Выход из Firebase
            // Переход к экрану логина
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LogInFragment())
                .commit()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // Если пользователь не авторизован, редиректим на экран логина
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LogInFragment())
                .commit()
        }
    }
}
