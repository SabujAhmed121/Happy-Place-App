package com.example.happyplaceapp
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface registerDao {
    @Insert
    suspend fun insert(registerEntity: registerEntity)

    @Update
    suspend fun update(registerEntity: registerEntity)

    @Delete
    suspend fun delete(registerEntity: registerEntity)

    @Query("Select * From `register-table`")
    fun fetchAllData(): Flow<List<registerEntity>>

    @Query("Select * From `register-table` where id=:id")
    fun fetchDataById(id: Int): Flow<registerEntity>
}