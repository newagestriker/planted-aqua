package com.newage.plantedaqua.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import com.google.firebase.database.*
import com.newage.plantedaqua.helpers.TinyDB
import com.newage.plantedaqua.models.Plants
import com.newage.plantedaqua.room.PlantDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

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