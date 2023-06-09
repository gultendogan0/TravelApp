package com.example.travelapp.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.travelapp.HomeActivity
import com.example.travelapp.MainActivity
import com.example.travelapp.R
import com.example.travelapp.databinding.FragmentFirstBinding
import com.example.travelapp.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root

        auth = Firebase.auth

        val currentUser = auth.currentUser
        if(currentUser != null){
            startActivity(Intent( context, HomeActivity::class.java))
            requireActivity().finish()
        }

        binding.loginButton.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()){
                auth.signInWithEmailAndPassword(email,password).addOnSuccessListener {
                    startActivity(Intent( context, HomeActivity::class.java))
                    requireActivity().finish()
                }.addOnFailureListener {
                    Toast.makeText(context,it.localizedMessage,Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(context,"Fields should not be left blank", Toast.LENGTH_LONG).show()
            }
        }

        binding.registerButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment2_to_registerFragment)
        }

        binding.forgotPassword.setOnClickListener {
            val email = binding.etEmail.text.toString()
            if (email.isNotEmpty()){
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Verification code has been sent to your e-mail address.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "An error occurred while sending the verification code.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }else{
                Toast.makeText(context, "Do not leave the email field blank.", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

}