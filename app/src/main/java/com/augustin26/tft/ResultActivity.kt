package com.augustin26.tft

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.augustin26.tft.databinding.ActivityResultBinding
import com.bumptech.glide.Glide
import org.json.JSONArray
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

        binding.apply {
            progressCircular.visibility = View.INVISIBLE //뱅글뱅글
            tvSummonerName.text = summonerInfo.get("name") as String
            tvSummonerLevel.text = summonerInfo.get("summonerLevel") as String
            tvTier.text = summonerInfo.get("tier") as String
            tvRank.text = summonerInfo.get("rank") as String
            tvLeaguePoints.text = summonerInfo.get("leaguePoints") as String
            tvGameDatetime.text = SimpleDateFormat("yyyy년 MM월 dd일 HH:mm:ss").format(Date((summonerInfo.get("game_datetime") as String).toLong()))
            btnFavorite.setOnClickListener { //즐겨찾기 버튼
                val favoriteSummoner = Summoner(summonerInfo.get("name") as String)
                viewModal.addSummoner(favoriteSummoner)
                Toast.makeText(applicationContext,"${summonerInfo.get("name") as String}을(를) 즐겨찾기에 추가하였습니다.", Toast.LENGTH_SHORT).show()
            }
            summonerRecyclerview.layoutManager = LinearLayoutManager(applicationContext)
            val favoriteRVAdapter = SummonerRVAdapter(applicationContext, summonerInfo) //즐겨찾기 어댑터 초기화
            summonerRecyclerview.adapter = favoriteRVAdapter //리사이클러뷰에 어댑터 설정
        }

        val url = const.summonerIcon + summonerInfo.get("profileIconId") as String + ".png"

        Glide.with(this)//소환사 아이콘
            .load(url)
            .into(binding.imgSummonerIcon)
    }

}