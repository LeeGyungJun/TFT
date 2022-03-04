package com.augustin26.tft

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.databinding.DataBindingUtil
import com.augustin26.tft.databinding.ActivityMainBinding
import com.google.gson.JsonParser
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import okhttp3.*
import timber.log.Timber
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private var count = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        Timber.plant(Timber.DebugTree())
        Logger.addLogAdapter(AndroidLogAdapter())

        binding.btnOk.setOnClickListener {
            getSummonerPuuid()
        }
    }

    private fun getSummonerPuuid() {
        val url = Const().summonerUrl + binding.edtSummoner.text.toString() + "?api_key=" + Const().key
        val okHttpClient = OkHttpClient()
        val request = Request.Builder().url(url).build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Logger.d("getSummonerPuuidFailed")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    object : Handler(Looper.getMainLooper()) {
                        override fun handleMessage(msg: Message) {
                            try {
                                val body = response.body?.string()
                                val rootObj = JsonParser().parse(body.toString()).asJsonObject
                                getMatches(rootObj.get("puuid").asString)
                                Logger.d(rootObj)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }.sendEmptyMessage(1)
                }
            }
        })
    }

    private fun getMatches(puuid: String) {
        val url = Const().matchesUrl + puuid + "/ids?count=" + count + "&api_key=" + Const().key
        Timber.d(url)
        val okHttpClient = OkHttpClient();
        val request = Request.Builder().url(url).build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Logger.d("getMatchesFailed")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    object : Handler(Looper.getMainLooper()) {
                        override fun handleMessage(msg: Message) {
                            try {
                                val body = response.body?.string()
                                val rootObj = JsonParser().parse(body.toString()).asJsonArray
                                getMatch(rootObj[0].asString)
                                Logger.d(rootObj[0].asString)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }.sendEmptyMessage(1)
                }
            }
        })
    }

    private fun getMatch(match: String) {
        val url = Const().matchUrl + match + "?api_key=" + Const().key
        Timber.d(url)
        val okHttpClient = OkHttpClient();
        val request = Request.Builder().url(url).build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Logger.d("getMatchFailed")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    object : Handler(Looper.getMainLooper()) {
                        override fun handleMessage(msg: Message) {
                            try {
                                val body = response.body?.string()
                                val rootObj = JsonParser().parse(body.toString()).asJsonObject
                                Logger.d(rootObj)
                                val intent = Intent(applicationContext, ResultActivity::class.java)
                                intent.putExtra("result", rootObj.toString())
                                startActivity(intent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }.sendEmptyMessage(1)
                }
            }
        })
    }
}