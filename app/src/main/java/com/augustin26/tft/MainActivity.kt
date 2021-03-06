package com.augustin26.tft

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.augustin26.tft.databinding.ActivityMainBinding
import com.google.gson.JsonElement
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), FavoriteClickInterface, FavoriteDeleteInterface  {

    private lateinit var binding : ActivityMainBinding
    private lateinit var viewModal: SummonerViewModal
    private val const = Const(this)
    private var isRunning = false //검색중일 때 버튼막는 flag
    private var count = 20 //matches 검색 개수
    private lateinit var summonerInfo : Bundle //ResultActivity로 넘길 Bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        Timber.plant(Timber.DebugTree()) //팀버 로그
        Logger.addLogAdapter(AndroidLogAdapter()) //로거 로그

        binding.btnOk.setOnClickListener { //검색버튼
            if (binding.edtSummoner.text.isEmpty()) return@setOnClickListener //검색어가 없으면 리턴
            if (isRunning==false) { //검색중이 아니면 실행
                summonerInfo = Bundle() //번들을 새로 초기화
                binding.progressCircular.visibility = View.VISIBLE // progressbar 돌리기
                isRunning = true //검색중으로 바꿈
                getSummonerPuuid(binding.edtSummoner.text.toString()) //puuid부터 검색
            }
        }
        binding.favoriteRecyclerview.layoutManager = LinearLayoutManager(this) //리사이클러뷰 레이아웃매니저 초기화
        (binding.favoriteRecyclerview.layoutManager as LinearLayoutManager).orientation = LinearLayoutManager.HORIZONTAL //레이아웃매니저의 orientation 변경
        val favoriteRVAdapter = FavoriteRVAdapter( this, this) //즐겨찾기 어댑터 초기화
        binding.favoriteRecyclerview.adapter = favoriteRVAdapter //리사이클러뷰에 어댑터 설정
        viewModal = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))[SummonerViewModal::class.java] //뷰모델 초기화
        viewModal.allSummoners.observe(this, Observer { list -> //옵저버 달기
            list?.let {
                favoriteRVAdapter.updateList(it) //업데이트
            }
        })
    }

    //소환사 puuid
    private fun getSummonerPuuid(name : String) {
        val url = const.summonerUrl + name + "?api_key=" + const.key
        val okHttpClient = OkHttpClient()
        val request = Request.Builder().url(url).build()
        Timber.d(url)

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
                                val rootObj = JSONObject(body.toString())
                                summonerInfo.putString("name", rootObj.get("name").toString())
                                summonerInfo.putString("id", rootObj.get("id").toString())
                                summonerInfo.putString("profileIconId", rootObj.get("profileIconId").toString())
                                summonerInfo.putString("summonerLevel", rootObj.get("summonerLevel").toString())
                                summonerInfo.putString("puuid", rootObj.get("puuid").toString())
                                summonerInfo.putString("summonerid", rootObj.get("id").toString())
                                getEntry(rootObj.get("id").toString())
                                getMatches(rootObj.get("puuid").toString())
                                Logger.d(rootObj)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                isRunning = false
                                binding.progressCircular.visibility = View.INVISIBLE
                            }
                        }
                    }.sendEmptyMessage(1)
                }else {
                    try {
                        val body = response.body?.string()
                        val rootObj = JSONObject(body.toString())
                        isRunning = false
                        runOnUiThread {
                            binding.progressCircular.visibility = View.INVISIBLE
                            if (JSONObject(rootObj.get("status").toString()).get("status_code").toString()=="404") {
                                Toast.makeText(applicationContext, R.string.summoner_search_error, Toast.LENGTH_SHORT).show()
                            }else {
                                Toast.makeText(applicationContext, R.string.invalidate_key, Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        isRunning = false
                        binding.progressCircular.visibility = View.INVISIBLE
                    }
                }
            }
        })
    }

    //소환사 랭크
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

                                val jsonObject = JSONObject(JSONArray(body.toString())[0].toString())
                                if (jsonObject.length()!=0) {
                                    summonerInfo.putString("tier", jsonObject.get("tier").toString())
                                    summonerInfo.putString("rank", jsonObject.get("rank").toString())
                                    summonerInfo.putString("leaguePoints", jsonObject.get("leaguePoints").toString())
                                }else {
                                    summonerInfo.putString("tier", "")
                                    summonerInfo.putString("rank", "")
                                    summonerInfo.putString("leaguePoints", "")
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                summonerInfo.putString("tier", "")
                                summonerInfo.putString("rank", "")
                                summonerInfo.putString("leaguePoints", "")
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

    //소환사 경기기록
    private fun getMatches(puuid: String) {
        val url = const.matchesUrl + puuid + "/ids?count=" + count + "&api_key=" + const.key
        val okHttpClient = OkHttpClient();
        val request = Request.Builder().url(url).build()
        Timber.d(url)

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
                                val rootObj = JSONArray(body.toString())
                                getMatch(rootObj[0].toString())
                                Logger.d(rootObj.toString())
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


    //경기 정보
    private fun getMatch(match: String) {
        val url = const.matchUrl + match + "?api_key=" + const.key
        val okHttpClient = OkHttpClient();
        val request = Request.Builder().url(url).build()
        Timber.d(url)

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Logger.d("getMatchFailed")
                runOnUiThread {
                    binding.progressCircular.visibility = View.INVISIBLE
                }
                isRunning = false
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    object : Handler(Looper.getMainLooper()) {
                        override fun handleMessage(msg: Message) {
                            try {
                                val body = response.body?.string()
                                val jsonObject = JSONObject(body.toString())

                                val metadata = JSONObject(jsonObject.get("metadata").toString())
                                val info = JSONObject(jsonObject.get("info").toString())
                                val participantsUUID = JSONArray(metadata.get("participants").toString())
                                val participants = JSONArray(info.get("participants").toString())

                                //info의 participants 정보를 소트
                                val sortedJsonArray = JSONArray()

                                val jsonValues: MutableList<JSONObject> = ArrayList()
                                for (i in 0 until participants.length()) {
                                    jsonValues.add(participants.getJSONObject(i))
                                }
                                Collections.sort(jsonValues, object : Comparator<JSONObject?> {
                                    private val KEY_NAME = "placement"
                                    override fun compare(p0: JSONObject?, p1: JSONObject?): Int {
                                        var valA = String()
                                        var valB = String()
                                        try {
                                            valA = p0?.get(KEY_NAME).toString()
                                            valB = p1?.get(KEY_NAME).toString()
                                        } catch (e: JSONException) { }
                                        return valA.compareTo(valB)
                                    }
                                })
                                for (i in 0 until participants.length()) {
                                    sortedJsonArray.put(jsonValues[i])
                                }

                                summonerInfo.putString("game_datetime", info.get("game_datetime").toString())
                                summonerInfo.putString("game_length", info.get("game_length").toString())
                                summonerInfo.putString("info", sortedJsonArray.toString())
                                summonerInfo.putString("participants", participantsUUID.toString())
                                Logger.d(jsonObject)
                                val intent = Intent(applicationContext, ResultActivity::class.java)
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

    //리사이클러뷰 아이템 삭제 인터페이스
    override fun onDeleteClick(summoner: Summoner) {
        viewModal.deleteSummoner(summoner)
    }

    //리사이클러뷰 아이템 클릭 인터페이스
    override fun onItemClick(summoner: Summoner) {
        if (isRunning==false) {
            summonerInfo = Bundle()
            binding.progressCircular.visibility = View.VISIBLE
            isRunning = true
            getSummonerPuuid(summoner.name)
        }
    }
}