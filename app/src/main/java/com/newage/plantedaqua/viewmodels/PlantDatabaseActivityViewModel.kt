package com.newage.plantedaqua.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.newage.plantedaqua.repositories.PlantDBRepository
import com.newage.plantedaqua.models.Plants

class PlantDatabaseActivityViewModel(private val plantDBRepository: PlantDBRepository) : ViewModel() {

    fun getPlantList():LiveData<List<Plants>>{

        return plantDBRepository.getDataFromPlantRepo()

    }
}