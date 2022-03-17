package com.augustin26.tft

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.orhanobut.logger.Logger
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class APIManager(context : Context) {

    private val const = Const(context)

    private lateinit var url : String
    private lateinit var okHttpClient: OkHttpClient
    private lateinit var request: Request

    fun getSummonerPuuid(name : String) : String {
        var result = ""
        url = const.summonerUrl + name + "?api_key=" + const.key
        okHttpClient = OkHttpClient()
        request = Request.Builder().url(url).build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                result = "SummonerPuuidFailed1"
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    object : Handler(Looper.getMainLooper()) {
                        override fun handleMessage(msg: Message) {
                            try {
                                val body = response.body?.string()
                                result = JSONObject(body.toString()).toString()
                                Logger.d(result)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }.sendEmptyMessage(1)
                }else {
                    result = "SummonerPuuidFailed2"
                }
            }
        })
        return result
    }

    fun getSummonerNameByPuuid(puuid : String) : String{
        var result = ""
        val url = const.summonerUrlByPuuid + puuid + "?api_key=" + const.key
        val okHttpClient = OkHttpClient()
        val request = Request.Builder().url(url).build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                result = "SummonerByPuuidFailed1"
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    object : Handler(Looper.getMainLooper()) {
                        override fun handleMessage(msg: Message) {
                            try {
                                val body = response.body?.string()
                                val rootObj = JSONObject(body.toString())
                                result = rootObj.get("name").toString()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }.sendEmptyMessage(1)
                }else {
                    result = "SummonerByPuuidFailed2"
                }
            }
        })
        return result
    }
}