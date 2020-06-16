package com.newage.plantedaqua.room

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.newage.plantedaqua.models.Plants

@Dao
interface PlantDao {

    @Query("SELECT * FROM aquatic_plants_from_net ORDER BY scientificName")
    fun getAquaticPlantsList() : LiveData<List<Plants>>


    @Query("SELECT * FROM aquatic_plants_from_net WHERE plantID=:arg0")
    fun getAquaticPlantById(arg0:Long): LiveData<Plants>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAquaticPlant(plants: Plants)

    @Query("DELETE FROM aquatic_plants_from_net")
    fun deleteAllPlantsData()

}


@Database(entities = [Plants::class],version = 1,exportSchema = false)
abstract class PlantDB : RoomDatabase() {

    abstract val plantDao: PlantDao



    companion object {


        @Volatile
        private var INSTANCE: PlantDB? = null


        fun getInstance(context: Context): PlantDB {

            var instance = INSTANCE
            synchronized(this) {


                if (instance == null) {


                    instance = Room.databaseBuilder(context.applicationContext,
                            PlantDB::class.java, "Plant_DB")
                            .fallbackToDestructiveMigration()
                            .build()

                    INSTANCE = instance
                }

            }

            return INSTANCE!!
        }


    }
}