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

class PlantDBRepository(private val context: Context, private val plantDao: PlantDao) {

    private val tinyDB = TinyDB(context.applicationContext)

    private var plants = Plants()
    init {
        val plantDBReference = FirebaseDatabase.getInstance().getReference("PDB")
        val plantDBVersionReference = FirebaseDatabase.getInstance().getReference("PDBVersion")
        var version: Int

        plantDBVersionReference.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                version = tinyDB.getInt("PLANT_DB_VERSION")
                if(version < p0.getValue(Int::class.java)!!) {
                    plantDBReference.addListenerForSingleValueEvent(object : ValueEventListener {


                        override fun onCancelled(p0: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            for (child in p0.children) {
                                plants = child.getValue(Plants::class.java)!!
                                storeDataInDB(plants)
                            }

                        }

                    })
                }
            }
        })

    }

    private fun storeDataInDB(plants: Plants){
        val job = SupervisorJob()
        CoroutineScope(Dispatchers.IO + job).launch {
            plantDao.insertAquaticPlant(plants)
        }

    }

    fun getDataFromPlantRepo():LiveData<List<Plants>>{
        return plantDao.getAquaticPlantsList()
    }

}