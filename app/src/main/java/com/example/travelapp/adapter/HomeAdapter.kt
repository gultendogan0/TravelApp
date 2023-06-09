package com.example.travelapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.travelapp.databinding.ItemRowBinding
import com.example.travelapp.model.Post
import com.squareup.picasso.Picasso

class HomeAdapter(val postList: ArrayList<Post>, private val itemDeleteListener: ItemDeleteListener): RecyclerView.Adapter<HomeAdapter.PostHolder>() {

    class PostHolder(val binding: ItemRowBinding): RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding = ItemRowBinding.inflate(LayoutInflater.from(parent.context))
        return PostHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.binding.itemTitle.text = postList.get(position).title
        holder.binding.itemCity.text = postList.get(position).city
        holder.binding.itemDescription.text = postList.get(position).description
        Picasso.get()
            .load(postList[position].downloadUrl)
            .into(holder.binding.itemImage)

        holder.binding.itemDelete.setOnClickListener {
            itemDeleteListener.onItemDelete(position)
        }
    }



}