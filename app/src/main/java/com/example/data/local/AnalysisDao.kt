package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AnalysisDao {
    @Query("SELECT * FROM analysis_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<AnalysisEntity>>

    @Query("SELECT * FROM analysis_history WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): AnalysisEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: AnalysisEntity)

    @Query("DELETE FROM analysis_history WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM analysis_history")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM analysis_history")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM analysis_history WHERE riskLevel = 'HIGH'")
    fun getHighRiskCount(): Flow<Int>
}
