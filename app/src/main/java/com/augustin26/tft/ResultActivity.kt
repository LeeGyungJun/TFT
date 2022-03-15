package com.augustin26.tft

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.augustin26.tft.databinding.ActivityResultBinding
import com.orhanobut.logger.Logger
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ResultActivity : AppCompatActivity() {
    private lateinit var binding : ActivityResultBinding
    private lateinit var summonerInfo : Bundle
    private lateinit var viewModal: SummonerViewModal

    private val const = Const(this)
    private lateinit var puuid : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_result)
        viewModal = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application))[SummonerViewModal::class.java] //뷰모델 초기화
        summonerInfo = intent.getBundleExtra("summonerInfo")!!
        puuid = summonerInfo.get("puuid").toString()
        val participants = summonerInfo.get("participants")
        val info = JSONArray(summonerInfo.get("info").toString())
        repeat(8) {
            if (JSONObject(info[it].toString()).get("puuid")==puuid) {
                binding.tvSummonerEliminated.text = "${(JSONObject(info[it].toString()).get("players_eliminated").toString().toInt()+1)}등"
                binding.tvSummonerGoldLeft.text = "남은골드:${(JSONObject(info[it].toString()).get("gold_left").toString().toInt())}"
                return@repeat
            }
        }

        getSummonerByPuuid(JSONArray(participants.toString())[0].toString(), 1) // 소
        getSummonerByPuuid(JSONArray(participants.toString())[1].toString(), 2) // 환
        getSummonerByPuuid(JSONArray(participants.toString())[2].toString(), 3) // 사
        getSummonerByPuuid(JSONArray(participants.toString())[3].toString(), 4) // 이
        getSummonerByPuuid(JSONArray(participants.toString())[4].toString(), 5) // 름
        getSummonerByPuuid(JSONArray(participants.toString())[5].toString(), 6) //
        getSummonerByPuuid(JSONArray(participants.toString())[6].toString(), 7) //
        getSummonerByPuuid(JSONArray(participants.toString())[7].toString(), 8) //

        binding.apply {
            progressCircular.visibility = View.INVISIBLE //뱅글뱅글
            tvTier.text = summonerInfo.get("tier") as String
            tvRank.text = summonerInfo.get("rank") as String
            tvLeaguePoints.text = summonerInfo.get("leaguePoints") as String
            tvGameDatetime.text = SimpleDateFormat("yyyy년 MM월 dd일 HH:mm:ss").format(Date((summonerInfo.get("game_datetime") as String).toLong()))
            tvGameLength.text = summonerInfo.get("game_length") as String
            btnFavorite.setOnClickListener { //즐겨찾기 버튼
                val favoriteSummoner = Summoner(summonerInfo.get("name") as String)
                viewModal.addSummoner(favoriteSummoner)
                Toast.makeText(applicationContext,"${summonerInfo.get("name") as String}을(를) 즐겨찾기에 추가하였습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        getSummonerIconImage(summonerInfo.get("profileIconId") as String) //소환사 아이콘
    }

    //소환사 계정 아이콘 가져오기
    private fun getSummonerIconImage(profileIconId : String) {
        val url = const.summonerIcon + profileIconId + ".png"
        val okHttpClient = OkHttpClient()
        val request = Request.Builder().url(url).build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                Timber.d("$response")
                if (response.isSuccessful) {
                    object : Handler(Looper.getMainLooper()) {
                        override fun handleMessage(msg: Message) {
                            try {
                                Timber.d("$response")
                                val bytes: ByteArray = response.body!!.bytes()
                                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                binding.imgSummonerIcon.setImageBitmap(bitmap)
                                binding.tvSummonerLevel.text = summonerInfo.get("summonerLevel") as String
                                binding.tvSummonerName.text = summonerInfo.get("name") as String
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }.sendEmptyMessage(1)
                }else {
                }
            }
        })
    }

    //소환사 이름 가져오기
    private fun getSummonerByPuuid(puuid : String, n : Int) {
        val url = const.summonerUrlByPuuid + puuid + "?api_key=" + const.key
        val okHttpClient = OkHttpClient()
        val request = Request.Builder().url(url).build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Logger.d("getSummonerByPuuidFailed")
                binding.progressCircular.visibility = View.INVISIBLE
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    object : Handler(Looper.getMainLooper()) {
                        override fun handleMessage(msg: Message) {
                            try {
                                val body = response.body?.string()
                                val rootObj = JSONObject(body.toString())
                                runOnUiThread {
                                    when (n) {
                                        1 -> binding.tvSummoner1.text = rootObj.get("name").toString()
                                        2 -> binding.tvSummoner2.text = rootObj.get("name").toString()
                                        3 -> binding.tvSummoner3.text = rootObj.get("name").toString()
                                        4 -> binding.tvSummoner4.text = rootObj.get("name").toString()
                                        5 -> binding.tvSummoner5.text = rootObj.get("name").toString()
                                        6 -> binding.tvSummoner6.text = rootObj.get("name").toString()
                                        7 -> binding.tvSummoner7.text = rootObj.get("name").toString()
                                        8 -> binding.tvSummoner8.text = rootObj.get("name").toString()
                                    }
                                }
                                Logger.d(rootObj)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                binding.progressCircular.visibility = View.INVISIBLE
                            }
                        }
                    }.sendEmptyMessage(1)
                }
            }
        })
    }
}