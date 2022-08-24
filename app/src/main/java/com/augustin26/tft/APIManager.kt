package com.augustin26.tft

import android.util.Log
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber

class APIManager {

    private val tft = TFT()
    private val okHttpClient = OkHttpClient()

    private var count = 20 //getMatches 검색 개수

    private lateinit var url : String
    private lateinit var request: Request
    private lateinit var jsonObject: JSONObject
    private lateinit var jsonArray: JSONArray

    /**
      okhttp에서 동기처리는 execute, 비동기처리는 enqueue
      코루틴 자체가 비동기 처리이기 때문에 execute를 사용해야한다.
     */

    //소환사 puuid
    suspend fun getSummonerPuuid(name : String) : JSONObject {
        url = tft.summonerUrl + name + "?api_key=" + tft.key
        request = Request.Builder().url(url).build()
        Timber.d(url)

        return withContext(Dispatchers.IO) {
            val response = okHttpClient.newCall(request).execute()
            try {
                if (response.isSuccessful) {
                    jsonObject = JSONObject(response.body!!.string())
                }else {
                    jsonObject = JSONObject(response.body!!.string())
                }
            } catch (e: Exception) {
                Log.e("getSummonerPuuid", "$e")
            }
            jsonObject
        }
    }

    //소환사 랭크
    suspend fun getEntry(id: String) : JSONObject {
        url = tft.summonerEntry + id + "?api_key=" + tft.key
        request = Request.Builder().url(url).build()
        Timber.d(url)

        return withContext(Dispatchers.IO) {
            val response = okHttpClient.newCall(request).execute()
            try {
                val body = response.body!!.string()
                if (response.isSuccessful) {
                    jsonObject = JSONObject(JSONArray(body)[0].toString())
                }else {
                    jsonObject = JSONObject(JSONArray(body)[0].toString())
                }
            } catch (e: Exception) {
                Log.e("getSummonerPuuid", "$e")
            }
            jsonObject
        }
    }

    //소환사 경기기록
    suspend fun getMatches(puuid: String) : JSONArray {
        url = tft.matchesUrl + puuid + "/ids?count=" + count + "&api_key=" + tft.key
        request = Request.Builder().url(url).build()
        Timber.d(url)

        return withContext(Dispatchers.IO) {
            val response = okHttpClient.newCall(request).execute()
            try {
                if (response.isSuccessful) {
                    jsonArray = JSONArray(response.body!!.string())
                }else {
                    jsonArray = JSONArray(response.body!!.string())
                }
            } catch (e: Exception) {
                Log.e("getMatches", "$e")
            }
            jsonArray
        }
    }

    //경기 정보
    suspend fun getMatch(match: String) : JSONObject {
        url = tft.matchUrl + match + "?api_key=" + tft.key
        request = Request.Builder().url(url).build()
        Timber.d(url)

        return withContext(Dispatchers.IO) {
            val response = okHttpClient.newCall(request).execute()
            try {
                if (response.isSuccessful) {
                    jsonObject = JSONObject(response.body!!.string())
                }else {
                    jsonObject = JSONObject(response.body!!.string())
                }
            } catch (e: Exception) {
                Log.e("getMatch", "$e")
            }
            jsonObject
        }
    }
}