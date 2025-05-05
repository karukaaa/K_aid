package com.example.myapplication

import androidx.room.*

@Dao
interface RequestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: Request)

    @Query("SELECT * FROM requests")
    suspend fun getAllRequests(): List<Request>

    @Delete
    suspend fun deleteRequest(request: Request)

    @Query("DELETE FROM requests")
    suspend fun clearAll()
}
