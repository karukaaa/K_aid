package com.example.myapplication.requestcreation

import androidx.room.*
import com.example.myapplication.requestlist.Request

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
