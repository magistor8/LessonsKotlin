package com.magistor8.weather.room

import androidx.room.*

@Dao
interface HistoryDao {

    @Query("SELECT * FROM HistoryEntity")
    fun all(): List<HistoryEntity>

    @Query("SELECT * FROM HistoryEntity WHERE city LIKE :city")
    fun getDataByCity(city: String): List<HistoryEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(entity: HistoryEntity)

    @Update
    fun update(entity: HistoryEntity)

    @Delete
    fun delete(entity: HistoryEntity)

}