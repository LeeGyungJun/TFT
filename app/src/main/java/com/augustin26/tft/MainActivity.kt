package com.augustin26.tft

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.augustin26.tft.databinding.ActivityMainBinding
import com.google.gson.JsonParser
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import okhttp3.*
import timber.log.Timber
import java.io.IOException


class MainActivity : AppCompatActivity(), FavoriteClickInterface, FavoriteDeleteInterface  {

    private lateinit var binding : ActivityMainBinding

    private lateinit var viewModal: SummonerViewModal
    private val const = Const(this)

    private var isRunning = false //검색중일 때 버튼막는 flag
    private var count = 20
    private lateinit var summonerInfo : Bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        Timber.plant(Timber.DebugTree()) //팀버 로그
        Logger.addLogAdapter(AndroidLogAdapter()) //로거 로그

        binding.btnOk.setOnClickListener {
            if (binding.edtSummoner.text.isEmpty()) return@setOnClickListener
            if (isRunning==false) {
                summonerInfo = Bundle()
                binding.progressCircular.visibility = View.VISIBLE
                isRunning = true
                getSummonerPuuid(binding.edtSummoner.text.toString())
            }
        }
        binding.favoriteRecyclerview.layoutManager = LinearLayoutManager(this)
        (binding.favoriteRecyclerview.layoutManager as LinearLayoutManager).orientation = LinearLayoutManager.HORIZONTAL
        val favoriteRVAdapter = FavoriteRVAdapter(this, this, this)
        binding.favoriteRecyclerview.adapter = favoriteRVAdapter
        viewModal = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))[SummonerViewModal::class.java]
        viewModal.allSummoners.observe(this, Observer { list ->
            list?.let {
                favoriteRVAdapter.updateList(it)
            }
        })

    }

    private fun getSummonerPuuid(name : String) {
        val url = const.summonerUrl + name + "?api_key=" + const.key
        val okHttpClient = OkHttpClient()
        val request = Request.Builder().url(url).build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Logger.d("getSummonerPuuidFailed")
                binding.progressCircular.visibility = View.INVISIBLE
                isRunning = false
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    object : Handler(Looper.getMainLooper()) {
                        override fun handleMessage(msg: Message) {
                            try {
                                val body = response.body?.string()
                                val rootObj = JsonParser().parse(body.toString()).asJsonObject
                                summonerInfo.putString("name", rootObj.get("name").asString)
                                summonerInfo.putString("id", rootObj.get("id").asString)
                                summonerInfo.putString("profileIconId", rootObj.get("profileIconId").asString)
                                summonerInfo.putString("summonerLevel", rootObj.get("summonerLevel").asString)
                                summonerInfo.putString("puuid", rootObj.get("puuid").asString)
                                summonerInfo.putString("summonerid", rootObj.get("id").asString)
                                getMatches(rootObj.get("puuid").asString)
                                getEntry(rootObj.get("id").asString)
                                Logger.d(rootObj)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                isRunning = false
                                binding.progressCircular.visibility = View.INVISIBLE
                            }
                        }
                    }.sendEmptyMessage(1)
                }else {
                    val body = response.body?.string()
                    val rootObj = JsonParser().parse(body.toString()).asJsonObject
                    isRunning = false
                    runOnUiThread {
                        binding.progressCircular.visibility = View.INVISIBLE
                        if (rootObj.get("status").asJsonObject.get("status_code").asInt==404) {
                            Toast.makeText(applicationContext, R.string.summoner_search_error, Toast.LENGTH_SHORT).show()
                        }else {
                            Toast.makeText(applicationContext, R.string.invalidate_key, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }

    private fun getEntry(id: String) {
        val url = Const(this).summonerEntry + id + "?api_key=" + Const(this).key
        val okHttpClient = OkHttpClient();
        val request = Request.Builder().url(url).build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Logger.d("getEntryFailed")
                binding.progressCircular.visibility = View.INVISIBLE
                isRunning = false
            }

            override fun onResponse(call: Call, response: Response) {
                Timber.d("$response")
                if (response.isSuccessful) {
                    object : Handler(Looper.getMainLooper()) {
                        override fun handleMessage(msg: Message) {
                            try {
                                val body = response.body?.string()
                                val rootObj = JsonParser().parse(body.toString()).toString().split(",")
                                if (rootObj.size!=1) {
                                    summonerInfo.putString("tier", rootObj[2].split(":")[1].replace("\"",""))
                                    summonerInfo.putString("rank", rootObj[3].split(":")[1].replace("\"",""))
                                    summonerInfo.putString("leaguePoints", rootObj[6].split(":")[1])
                                }else {
                                    summonerInfo.putString("tier", "")
                                    summonerInfo.putString("rank", "")
                                    summonerInfo.putString("leaguePoints", "")
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                isRunning = false
                                binding.progressCircular.visibility = View.INVISIBLE
                            }
                        }
                    }.sendEmptyMessage(1)
                }else {
                    isRunning = false
                    runOnUiThread {
                        binding.progressCircular.visibility = View.INVISIBLE
                        Toast.makeText(applicationContext, R.string.summoner_entry_error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun getMatches(puuid: String) {
        val url = const.matchesUrl + puuid + "/ids?count=" + count + "&api_key=" + const.key
        val okHttpClient = OkHttpClient();
        val request = Request.Builder().url(url).build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Logger.d("getMatchesFailed")
                binding.progressCircular.visibility = View.INVISIBLE
                isRunning = false
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
                                isRunning = false
                                binding.progressCircular.visibility = View.INVISIBLE
                            }
                        }
                    }.sendEmptyMessage(1)
                }else {
                    isRunning = false
                    runOnUiThread {
                        binding.progressCircular.visibility = View.INVISIBLE
                        Toast.makeText(applicationContext, R.string.matches_search_error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun getMatch(match: String) {
        val url = const.matchUrl + match + "?api_key=" + const.key
        Timber.d(url)
        val okHttpClient = OkHttpClient();
        val request = Request.Builder().url(url).build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Logger.d("getMatchFailed")
                binding.progressCircular.visibility = View.INVISIBLE
                isRunning = false
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
                                intent.putExtra("summonerInfo", summonerInfo)
                                startActivity(intent)
                                binding.progressCircular.visibility = View.INVISIBLE
                                isRunning = false
                            } catch (e: Exception) {
                                e.printStackTrace()
                                isRunning = false
                                binding.progressCircular.visibility = View.INVISIBLE
                            }
                        }
                    }.sendEmptyMessage(1)
                }else {
                    isRunning = false
                    runOnUiThread {
                        binding.progressCircular.visibility = View.INVISIBLE
                        Toast.makeText(applicationContext, R.string.match_search_error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    override fun onDeleteClick(summoner: Summoner) {
        viewModal.deleteSummoner(summoner)
    }

    override fun onItemClick(summoner: Summoner) {
        if (isRunning==false) {
            summonerInfo = Bundle()
            binding.progressCircular.visibility = View.VISIBLE
            isRunning = true
            getSummonerPuuid(summoner.name)
        }
    }
}