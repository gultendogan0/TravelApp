package com.example.travelapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelapp.adapter.HomeAdapter
import com.example.travelapp.adapter.ItemDeleteListener
import com.example.travelapp.databinding.FragmentHomeBinding
import com.example.travelapp.model.Post
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class HomeFragment : Fragment(), ItemDeleteListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var postArrayList: ArrayList<Post>
    private lateinit var homeAdapter: HomeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        auth = Firebase.auth
        db = Firebase.firestore

        postArrayList = ArrayList<Post>()

        getData()

        binding.homeRecyclerView.layoutManager = LinearLayoutManager(context)
        homeAdapter = HomeAdapter(postArrayList,this)
        binding.homeRecyclerView.adapter = homeAdapter

        return root
    }

    private fun getData(){
        val currentUserEmail = auth.currentUser?.email
        db.collection("Posts")
            .whereEqualTo("userEmail", currentUserEmail)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener{ value, error ->
            if (error != null){
                //Toast.makeText(context,error.localizedMessage,Toast.LENGTH_LONG).show()
            }else{
                if (value != null){
                    if (!value.isEmpty){
                        val documents = value.documents
                        postArrayList.clear()
                        for (document in documents){
                            val title = document.get("title") as String
                            val city = document.get("city") as String
                            val description = document.get("description") as String
                            val downloadUrl = document.get("downloadUrl") as String
                            val email = document.get("userEmail") as String
                            val post = Post(title, city, description, downloadUrl, email)
                            postArrayList.add(post)
                        }

                        homeAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onItemDelete(position: Int) {
        showDeleteConfirmationDialog(position)
    }

    private fun showDeleteConfirmationDialog(position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirmation")
            .setMessage("Are you sure you want to delete this item?")
            .setPositiveButton("Yes") { _, _ ->
                deleteItem(position)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteItem(position: Int) {
        val post = postArrayList[position]
        val postId = post.title
        val currentUserEmail = auth.currentUser?.email
        db.collection("Posts")
            .whereEqualTo("userEmail", currentUserEmail)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    //Toast.makeText(context,error.localizedMessage,Toast.LENGTH_LONG).show()
                } else {
                    if (value != null) {
                        if (!value.isEmpty) {
                            val documents = value.documents
                            for (document in documents) {
                                val title = document.getString("title")
                                if (title == postId) {
                                    val documentId = document.id
                                    db.collection("Posts")
                                        .document(documentId)
                                        .delete()
                                        .addOnSuccessListener {
                                            postArrayList.remove(post)
                                            homeAdapter.notifyItemRemoved(position)
                                        }
                                        .addOnFailureListener { exception ->
                                            Toast.makeText(
                                                context,
                                                "Delete failed: ${exception.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    break
                                }
                            }
                        }
                    }
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}