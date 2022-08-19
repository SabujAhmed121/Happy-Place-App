package com.example.happyplaceapp

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "register-table")
data class registerEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String = "",

    val image: String = "",
    val description: String = "",
    val date: String = "",
    val location: String = "",
    val latitude: Double,
    val longitude: Double
): Serializable