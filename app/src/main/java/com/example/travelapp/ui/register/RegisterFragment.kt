package com.example.travelapp.ui.register

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.travelapp.HomeActivity
import com.example.travelapp.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view = binding.root

        auth = Firebase.auth

        binding.registerButton.setOnClickListener {

            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val againPassword = binding.etAgainPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && againPassword.isNotEmpty()){
                if(password == againPassword){
                    auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener {
                        startActivity(Intent( context, HomeActivity::class.java))
                        requireActivity().finish()
                    }.addOnFailureListener {
                        Toast.makeText(context,it.localizedMessage,Toast.LENGTH_LONG).show()
                    }
                }else{
                    Toast.makeText(context,"Passwords must be the same",Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(context,"Fields should not be left blank",Toast.LENGTH_LONG).show()
            }
        }

        return view
    }

}