package com.augustin26.tft

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "summonersTable")
data class Summoner (
    @PrimaryKey
    @ColumnInfo(name = "name")
    val name :String)