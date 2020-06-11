package com.newage.plantedaqua.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.newage.plantedaqua.fragments.plantdb.PlantDBRepository
import com.newage.plantedaqua.models.Plants
import com.newage.plantedaqua.room.PlantDao

class PlantDatabaseActivityViewModel(private val plantDBRepository: PlantDBRepository) : ViewModel() {

    fun getPlantList():LiveData<List<Plants>>{

        return plantDBRepository.getDataFromPlantRepo()

    }
}