package com.example.happyplaceapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [registerEntity::class], version = 1)
abstract class registerDatabase: RoomDatabase() {

        abstract fun registerDao(): registerDao

        companion object {
            @Volatile
            private var INSTANCE: registerDatabase? = null

            fun getInstance(context: Context): registerDatabase{
                synchronized(this){
                    var instance = INSTANCE

                    if (instance == null){
                        instance = Room.databaseBuilder(
                            context.applicationContext,
                           registerDatabase::class.java,
                            "employee-database"
                        ).fallbackToDestructiveMigration().build()

                        INSTANCE = instance
                    }
                    return instance
                }
            }
        }
    }