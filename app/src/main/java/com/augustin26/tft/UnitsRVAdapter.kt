package com.augustin26.tft

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.json.JSONArray
import org.json.JSONObject


class UnitsRVAdapter(private val context: Context, private val units : JSONArray) : RecyclerView.Adapter<UnitsRVAdapter.ViewHolder>() {

    private val const = Const(context)

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgUnit: ImageView = itemView.findViewById<ImageView>(R.id.img_unit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.unit_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val unitName = JSONObject(units[position].toString()).get("character_id").toString().split("_")[1]
        val url = const.championIcon + unitName + ".png"
        Glide.with(context)//소환사 아이콘
            .load(url)
            .into(holder.imgUnit)
    }

    override fun getItemCount(): Int {
        return units.length()
    }
}
