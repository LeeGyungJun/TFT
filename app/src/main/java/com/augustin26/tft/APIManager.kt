package com.augustin26.tft

import android.util.Log
import kotlinx.coroutines.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber

class APIManager {

    private val const = Const()
    private val okHttpClient = OkHttpClient()

    private var count = 20 //getMatches 검색 개수

    private lateinit var url : String
    private lateinit var request: Request
    private lateinit var jsonObject: JSONObject
    private lateinit var jsonArray: JSONArray

    //소환사 puuid
    suspend fun getSummonerPuuid(name : String) : JSONObject {
        url = const.summonerUrl + name + "?api_key=" + const.key
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
        url = const.summonerEntry + id + "?api_key=" + const.key
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
        url = const.matchesUrl + puuid + "/ids?count=" + count + "&api_key=" + const.key
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
        url = const.matchUrl + match + "?api_key=" + const.key
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