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

    private var jsonArray: JSONArray = JSONArray("[]")
    private var jsonObject: JSONObject = JSONObject("{}")

    /**
      okhttp에서 동기처리는 execute, 비동기처리는 enqueue
      코루틴 자체가 비동기 처리이기 때문에 execute를 사용해야한다.

      추가로, execute 사용하고 response.body 는 한번만 사용해야한다.
      로그찍는다고 두번쓰면 에러남
     */

    //소환사 puuid
    suspend fun getSummonerPuuid(name : String) : JSONObject {
        url = tft.summonerUrl + name + "?api_key=" + tft.key
        request = Request.Builder().url(url).build()
        Timber.d(url)

        return withContext(Dispatchers.IO) {
            val response = okHttpClient.newCall(request).execute()
            try {
                jsonObject = JSONObject(response.body!!.string())
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
            val body = response.body!!.string()
            try {
                jsonObject = JSONObject(JSONArray(body)[0].toString())
            } catch (e: Exception) {
                jsonObject = JSONObject(body)
                Log.e("getEntry", "$e")
            }
            jsonObject
        }
    }

    //소환사 경기기록
    suspend fun getMatches(puuid: String) : JSONArray {
        url = tft.matchesUrl + puuid + "ids?count=" + count + "&api_key=" + tft.key
        request = Request.Builder().url(url).build()
        Timber.d(url)

        return withContext(Dispatchers.IO) {
            val response = okHttpClient.newCall(request).execute()
            try {
                jsonArray = JSONArray(response.body!!.string())
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
                jsonObject = JSONObject(response.body!!.string())
            } catch (e: Exception) {
                Log.e("getMatch", "$e")
            }
            jsonObject
        }
    }
}
