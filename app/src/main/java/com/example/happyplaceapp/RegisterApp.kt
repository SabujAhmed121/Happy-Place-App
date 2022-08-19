package com.example.happyplaceapp

import android.app.Application

class RegisterApp : Application(){
    val db by lazy {
        registerDatabase.getInstance(this)
    }
}