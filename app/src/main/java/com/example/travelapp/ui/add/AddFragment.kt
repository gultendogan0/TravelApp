package com.example.travelapp.ui.add

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.travelapp.R
import com.example.travelapp.databinding.FragmentAddBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.UUID

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var selectedPicture: Uri? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.mainImage.setOnClickListener {
            if (checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            } else {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }

        registerLauncher()
        auth = Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage

        binding.addButton.setOnClickListener {

            val uuid = UUID.randomUUID()
            val imageName = "$uuid.jpg"

            val reference = storage.reference
            val imageReference = reference.child("images").child(imageName)

            if (selectedPicture != null){
                imageReference.putFile(selectedPicture!!).addOnSuccessListener {

                    val uploadPictureReference = storage.reference.child("images").child(imageName)
                    uploadPictureReference.downloadUrl.addOnSuccessListener {
                        val downloadUrl = it.toString()
                        if (auth.currentUser != null){
                            val postMap = hashMapOf<String,Any>()
                            postMap.put("downloadUrl",downloadUrl)
                            postMap.put("userEmail",auth.currentUser!!.email!!)
                            postMap.put("title",binding.etTitle.text.toString())
                            postMap.put("city",binding.etCity.text.toString())
                            postMap.put("description",binding.etDescription.text.toString())
                            postMap.put("date",Timestamp.now())

                            firestore.collection("Posts").add(postMap).addOnSuccessListener {
                                findNavController().navigate(R.id.action_addFragment_to_nav_home)
                            }.addOnFailureListener{
                                Toast.makeText(context,it.localizedMessage,Toast.LENGTH_LONG).show()
                            }
                        }
                    }

                }.addOnFailureListener{
                    Toast.makeText(context,it.localizedMessage,Toast.LENGTH_LONG).show()
                }
            }
        }

        return view
    }

    private fun registerLauncher() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val intentFromResult = result.data
                if (intentFromResult != null) {
                    selectedPicture = intentFromResult.data
                    selectedPicture?.let {
                        binding.mainImage.setImageURI(it)
                    }
                }
            }
        }
    }
}