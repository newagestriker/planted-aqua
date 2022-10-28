package com.newage.aquapets.repositories

import androidx.lifecycle.LiveData
import com.newage.aquapets.models.Plants
import com.newage.aquapets.room.PlantDao

class PlantDBRepository(private val plantDao: PlantDao) {



   fun storeDataInDB(plants: Plants){

            plantDao.insertAquaticPlant(plants)


    }

    fun clearTable(){
        plantDao.deleteAllPlantsData()
    }

    fun getDataFromPlantRepo():LiveData<List<Plants>>{
        return plantDao.getAquaticPlantsList()
    }

}