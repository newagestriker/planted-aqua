package com.newage.aquapets.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.newage.aquapets.helpers.TinyDB
import com.newage.aquapets.repositories.PlantDBRepository
import com.newage.aquapets.models.Plants
import kotlinx.coroutines.*

class PlantDatabaseActivityViewModel(private val plantDBRepository: PlantDBRepository, application: Application) : AndroidViewModel(application) {

    private val tinyDB = TinyDB(application.applicationContext)
    private val listenVersionChangeJob = SupervisorJob()
    private val loadPlantDataJob = SupervisorJob()

    private var plants = Plants()
    init {
        val plantDBReference = FirebaseDatabase.getInstance().getReference("PDB")
        val plantDBVersionReference = FirebaseDatabase.getInstance().getReference("PDBVersion")
        var version: Int
        var newVersion : Int

        plantDBVersionReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                version = tinyDB.getInt("PLANT_DB_VERSION")
                newVersion = p0.getValue(Int::class.java)!!
                if(version < newVersion) {


                    CoroutineScope(Dispatchers.IO+listenVersionChangeJob).launch {
                        plantDBRepository.clearTable()

                        plantDBReference.addListenerForSingleValueEvent(object : ValueEventListener {


                            override fun onCancelled(p0: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                CoroutineScope(Dispatchers.IO + loadPlantDataJob).launch {
                                    for (child in p0.children) {
                                        plants = child.getValue(Plants::class.java)!!
                                        plantDBRepository.storeDataInDB(plants)
                                    }

                                    tinyDB.putInt("PLANT_DB_VERSION", newVersion)

                                }
                            }

                        })
                    }



                }
            }
        })

    }

    fun getPlantList():LiveData<List<Plants>>{

        return plantDBRepository.getDataFromPlantRepo()

    }

    override fun onCleared() {
        listenVersionChangeJob.cancel()
        loadPlantDataJob.cancel()
        super.onCleared()
    }
}