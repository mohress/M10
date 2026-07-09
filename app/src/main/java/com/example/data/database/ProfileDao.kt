package com.example.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM browser_profiles ORDER BY lastUsedTimestamp DESC, createdTimestamp DESC")
    fun getAllProfiles(): Flow<List<ProfileEntity>>

    @Query("SELECT * FROM browser_profiles WHERE id = :id")
    suspend fun getProfileById(id: Int): ProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: ProfileEntity): Long

    @Update
    suspend fun updateProfile(profile: ProfileEntity)

    @Delete
    suspend fun deleteProfile(profile: ProfileEntity)

    @Query("DELETE FROM browser_profiles WHERE id = :id")
    suspend fun deleteProfileById(id: Int)
}
