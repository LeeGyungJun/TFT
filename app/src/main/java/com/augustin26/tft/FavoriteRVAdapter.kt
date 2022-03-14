package com.augustin26.tft

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

class FavoriteRVAdapter(private val context: Context, private val noteClickDeleteInterface: FavoriteDeleteInterface, private val noteClickInterface: FavoriteClickInterface) :
    RecyclerView.Adapter<FavoriteRVAdapter.ViewHolder>() {

    private val allSummoners = ArrayList<Summoner>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById<TextView>(R.id.tv_favorite_name)
        val btnDelete: ImageView = itemView.findViewById<ImageView>(R.id.btn_favorite_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = allSummoners[position].name
        holder.btnDelete.setOnClickListener {
            noteClickDeleteInterface.onDeleteClick(allSummoners[position])
        }

        holder.itemView.setOnClickListener {
            noteClickInterface.onItemClick(allSummoners[position])
        }
    }

    override fun getItemCount(): Int {
        return allSummoners.size
    }

    fun updateList(newList: List<Summoner>) {
        allSummoners.clear()
        allSummoners.addAll(newList)
        notifyDataSetChanged()
    }

}
