package com.augustin26.tft

interface FavoriteDeleteInterface {
    //삭제 이미지 클릭 메소드
    fun onDeleteClick(summoner: Summoner)
}

interface FavoriteClickInterface {
    //리사이클러뷰 아이템 클릭 메소드
    fun onItemClick(summoner: Summoner)
}