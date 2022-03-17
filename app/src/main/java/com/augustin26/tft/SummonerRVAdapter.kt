package com.augustin26.tft

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orhanobut.logger.Logger
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException


class SummonerRVAdapter(private val context: Context, private val summonerInfo : Bundle) : RecyclerView.Adapter<SummonerRVAdapter.ViewHolder>() {

    val info = JSONArray(summonerInfo.get("info").toString())
    private val const = Const(context)

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvPlacement: TextView = itemView.findViewById<TextView>(R.id.tv_placement)
        var tvName: TextView = itemView.findViewById<TextView>(R.id.tv_name)
        var tvLastRound: TextView = itemView.findViewById<TextView>(R.id.tv_last_round)
        var tvEliminated: TextView = itemView.findViewById<TextView>(R.id.tv_eliminated)
        var tvGoldLeft: TextView = itemView.findViewById<TextView>(R.id.tv_gold_left)
        val unitRecyclerView: RecyclerView = itemView.findViewById<RecyclerView>(R.id.unit_recyclerview)
        val unitTierRecyclerView: RecyclerView = itemView.findViewById<RecyclerView>(R.id.unit_tier_recyclerview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.result_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val jsonObject = JSONObject(info[position].toString())

        val url = const.summonerUrlByPuuid + jsonObject.get("puuid").toString() + "?api_key=" + const.key
        val okHttpClient = OkHttpClient()
        val request = Request.Builder().url(url).build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Logger.d("getSummonerByPuuidFailed")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    object : Handler(Looper.getMainLooper()) {
                        override fun handleMessage(msg: Message) {
                            try {
                                val body = response.body?.string()
                                val rootObj = JSONObject(body.toString())
                                holder.tvName.text = rootObj.get("name").toString()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }.sendEmptyMessage(1)
                }
            }
        })
        val placement = jsonObject.get("placement").toString()

        holder.tvPlacement.text = "#$placement"
        when (placement.toInt()) {
            1 -> holder.tvPlacement.setBackgroundColor(Color.parseColor("#40E0D0"))
            in 2..4 -> holder.tvPlacement.setBackgroundColor(Color.parseColor("#6495ED"))
            in 5..8 -> holder.tvPlacement.setBackgroundColor(Color.parseColor("#999999"))
        }
        holder.tvLastRound.text = "라운드:" + jsonObject.get("last_round").toString()
        holder.tvEliminated.text = formatTime((jsonObject.get("time_eliminated").toString()).toDouble().toInt())
        holder.tvGoldLeft.text = jsonObject.get("gold_left").toString()
        holder.unitRecyclerView.layoutManager = LinearLayoutManager(context)
        (holder.unitRecyclerView.layoutManager as LinearLayoutManager).orientation = LinearLayoutManager.HORIZONTAL
        holder.unitRecyclerView.adapter = UnitsRVAdapter(context, JSONArray(jsonObject.get("units").toString()))
        holder.unitTierRecyclerView.layoutManager = LinearLayoutManager(context)
        (holder.unitTierRecyclerView.layoutManager as LinearLayoutManager).orientation = LinearLayoutManager.HORIZONTAL
        holder.unitTierRecyclerView.adapter = UnitsTierRVAdapter(context, JSONArray(jsonObject.get("units").toString()))
    }

    override fun getItemCount(): Int {
//        return allSummoners.size
        return info.length()
    }

    //시간 포맷 함수
    fun formatTime(time: Int) : String {
        val minute = String.format("%02d", (time/60)%60)
        val second = String.format("%02d", time%60)
        return "${minute}분${second}초"
    }

}
