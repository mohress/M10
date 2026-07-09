package com.example.data.repository

import com.example.data.database.ProfileDao
import com.example.data.database.ProfileEntity
import kotlinx.coroutines.flow.Flow

class ProfileRepository(private val profileDao: ProfileDao) {
    val allProfiles: Flow<List<ProfileEntity>> = profileDao.getAllProfiles()

    suspend fun getProfileById(id: Int): ProfileEntity? {
        return profileDao.getProfileById(id)
    }

    suspend fun insertProfile(profile: ProfileEntity): Long {
        return profileDao.insertProfile(profile)
    }

    suspend fun updateProfile(profile: ProfileEntity) {
        profileDao.updateProfile(profile)
    }

    suspend fun deleteProfile(profile: ProfileEntity) {
        profileDao.deleteProfile(profile)
    }

    suspend fun deleteProfileById(id: Int) {
        profileDao.deleteProfileById(id)
    }
}
