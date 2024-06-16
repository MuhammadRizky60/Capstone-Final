package com.example.agronect.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.agronect.R
import com.example.agronect.data.response.Animal

class ListCoffeeAdapter(private val listCoffee: ArrayList<Animal>): RecyclerView.Adapter<ListCoffeeAdapter.ListViewHolder>() {
    private lateinit var onItemCallback: OnItemCallback

    fun setOnItemClickCallBack(onItemCallback: OnItemCallback){
        this.onItemCallback = onItemCallback
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cfPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
        val cfName: TextView = itemView.findViewById(R.id.tv_item_name)
        val cfDescription: TextView = itemView.findViewById(R.id.tv_item_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_row_history, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int = listCoffee.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (name, description, photo) = listCoffee[position]
        holder.cfPhoto.setImageResource(photo)
        holder.cfName.text = name
        holder.cfDescription.text = description
        holder.itemView.setOnClickListener{onItemCallback.onItemClicked(listCoffee[holder.adapterPosition])}
    }

    interface OnItemCallback{
        fun onItemClicked(data: Animal)
    }
}