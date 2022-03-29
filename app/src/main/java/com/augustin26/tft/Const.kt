package com.augustin26.tft

import android.content.Context

class Const(context : Context) {
    val key = ""
    val matchesUrl = "https://asia.api.riotgames.com/tft/match/v1/matches/by-puuid/" ///ids?count=20&api_key=
    val matchUrl = "https://asia.api.riotgames.com/tft/match/v1/matches/" //?api_key=
    val summonerUrl = "https://kr.api.riotgames.com/tft/summoner/v1/summoners/by-name/" //?api_key=
    val summonerEntry = "https://kr.api.riotgames.com/tft/league/v1/entries/by-summoner/" //id
    val summonerIcon = "https://ddragon.poro.gg/12.3.1/img/profileicon/" //4903.png (profileIconId)
    val summonerUrlByPuuid = "https://kr.api.riotgames.com/tft/summoner/v1/summoners/by-puuid/" //?api_key=
    val championIcon = "https://ddragon.poro.gg/12.3.1/img/champion/" //Jhin.png
    val championStars = "https://cdn.lolchess.gg/images/tft/stars/" //cost1_stars2.png
}
