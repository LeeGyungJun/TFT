package com.augustin26.tft

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.augustin26.tft.databinding.ActivityMainBinding
import com.orhanobut.logger.Logger
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity(), FavoriteClickInterface, FavoriteDeleteInterface  {

    private lateinit var binding : ActivityMainBinding
    private lateinit var viewModal: SummonerViewModal
    private var isRunning = false //검색중일 때 버튼막는 flag
    private lateinit var summonerInfo : Bundle //ResultActivity로 넘길 Bundle

    private lateinit var apiManager : APIManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        apiManager = APIManager()

        binding.btnOk.setOnClickListener { //검색버튼
            if (binding.edtSummoner.text.isNullOrBlank()) return@setOnClickListener //검색어가 없으면 리턴
            if (!isRunning) { //검색중이 아니면 실행
                summonerInfo = Bundle() //번들을 새로 초기화
                binding.progressCircular.visibility = View.VISIBLE // progressbar 돌리기
                isRunning = true //검색중으로 바꿈

                doSearch(binding.edtSummoner.text.toString()) //검색 시작
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

    //검색 로직
    private fun doSearch(name : String) {

        val ceh = CoroutineExceptionHandler { coroutineContext, exception ->
            println("에러 내용: $exception")
            if (isRunning) {
                isRunning = false
                binding.progressCircular.visibility = View.INVISIBLE
                Toast.makeText(applicationContext, exception.message, Toast.LENGTH_SHORT).show()
            }
        }
        CoroutineScope(Dispatchers.IO+ceh).launch {
            val scope = Dispatchers.IO
            val result1 = withContext(scope) {
                val puuid = apiManager.getSummonerPuuid(name)
                if (puuid.optString("status").isNullOrBlank()) {
                    summonerInfo.putString("name", puuid.optString("name"))
                    summonerInfo.putString("id", puuid.optString("id"))
                    summonerInfo.putString("profileIconId", puuid.optString("profileIconId"))
                    summonerInfo.putString("summonerLevel", puuid.optString("summonerLevel"))
                    summonerInfo.putString("puuid", puuid.optString("puuid"))
                    summonerInfo.putString("summonerid", puuid.optString("id"))
                }else {
                    isRunning = false
                    runOnUiThread {
                        binding.progressCircular.visibility = View.INVISIBLE
                        if (JSONObject(puuid.optString("status")).optString("status_code") == "404") {
                            Toast.makeText(applicationContext, R.string.summoner_search_error, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(applicationContext, R.string.invalidate_key, Toast.LENGTH_SHORT).show()
                        }
                    }
                    throw Exception("Failed getPUUID")
                }
                puuid
            }
            val result2 = withContext(scope) {
                val entry = apiManager.getEntry(result1.optString("id"))
                summonerInfo.putString("tier", entry.optString("tier"))
                summonerInfo.putString("rank", entry.optString("rank"))
                summonerInfo.putString("leaguePoints", entry.optString("leaguePoints"))
                if (!entry.optString("status").isNullOrBlank()) {
                    isRunning = false
                    runOnUiThread {
                        binding.progressCircular.visibility = View.INVISIBLE
                        Toast.makeText(applicationContext, R.string.summoner_entry_error, Toast.LENGTH_SHORT).show()
                    }
                }
                withContext(Dispatchers.Default) {
                    val matches = apiManager.getMatches(result1.optString("puuid"))[0].toString() //첫 번쩨 경기만
                    if (matches.isBlank()) {
                        isRunning = false
                        runOnUiThread {
                            binding.progressCircular.visibility = View.INVISIBLE
                            Toast.makeText(applicationContext, R.string.matches_search_error, Toast.LENGTH_SHORT).show()
                        }
                    }
                    matches
                }
            }
            val match = apiManager.getMatch(result2)

            val metadata = JSONObject(match.optString("metadata"))
            val info = JSONObject(match.optString("info"))
            val participantsUUID = JSONArray(metadata.optString("participants"))
            val participants = JSONArray(info.optString("participants"))

            //info의 participants 정보를 소트
            val sortedJsonArray = JSONArray()

            val jsonValues: MutableList<JSONObject> = ArrayList()
            for (i in 0 until participants.length()) {
                jsonValues.add(participants.getJSONObject(i))
            }
            //등수 오름차순 JSON Sort
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
            Logger.d(match)
            runOnUiThread { binding.progressCircular.visibility = View.INVISIBLE }
            val intent = Intent(applicationContext, ResultActivity::class.java)
            intent.putExtra("summonerInfo", summonerInfo)
            startActivity(intent)
            isRunning = false
        }
    }

    //리사이클러뷰 아이템 삭제 인터페이스
    override fun onDeleteClick(summoner: Summoner) {
        viewModal.deleteSummoner(summoner)
    }

    //리사이클러뷰 아이템 클릭 인터페이스
    override fun onItemClick(summoner: Summoner) {
        if (!isRunning) {
            summonerInfo = Bundle()
            binding.progressCircular.visibility = View.VISIBLE
            isRunning = true

            doSearch(summoner.name)
        }
    }
}