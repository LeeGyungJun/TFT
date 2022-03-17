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


class UnitsTierRVAdapter(private val context: Context, private val units : JSONArray) : RecyclerView.Adapter<UnitsTierRVAdapter.ViewHolder>() {

    private val const = Const(context)

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgUnitTier: ImageView = itemView.findViewById<ImageView>(R.id.img_unit_tier)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.unit_tier_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rarity = JSONObject(units[position].toString()).get("rarity").toString()
        val tier = JSONObject(units[position].toString()).get("tier").toString()
        val url = const.championStars + "cost${rarity.toInt()+1}_stars${tier}" + ".png"
        Glide.with(context)//소환사 아이콘
            .load(url)
            .into(holder.imgUnitTier)
    }

    override fun getItemCount(): Int {
        return units.length()
    }
}
