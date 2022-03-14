package com.augustin26.tft

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.augustin26.tft.databinding.ActivityResultBinding
import okhttp3.*
import timber.log.Timber
import java.io.IOException


class ResultActivity : AppCompatActivity() {
    private lateinit var binding : ActivityResultBinding
    private lateinit var summonerInfo : Bundle
    private lateinit var viewModal: SummonerViewModal

    private val const = Const(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_result)

        viewModal = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(SummonerViewModal::class.java)

        val result = intent.getStringExtra("result")
        binding.tvResult.text = result
        summonerInfo = intent.getBundleExtra("summonerInfo")!!
        binding.tvTier.text = summonerInfo.get("tier") as String
        binding.tvRank.text = summonerInfo.get("rank") as String
        binding.tvLeaguePoints.text = summonerInfo.get("leaguePoints") as String
        getSummonerIconImage(summonerInfo.get("profileIconId") as String)

        binding.btnFavorite.setOnClickListener {
            val updateSummoner = Summoner(summonerInfo.get("name") as String)
            viewModal.addSummoner(updateSummoner)
        }
    }

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
}