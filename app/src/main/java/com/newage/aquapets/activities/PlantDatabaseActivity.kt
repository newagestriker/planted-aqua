package com.newage.aquapets.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.newage.aquapets.R
import com.newage.aquapets.models.Plants
import com.newage.aquapets.viewmodels.PlantDatabaseActivityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlantDatabaseActivity : AppCompatActivity() {

    private val plantsArrayList = ArrayList<Plants>()
    private val plantDatabaseActivityViewModel:PlantDatabaseActivityViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plant_database)

        plantDatabaseActivityViewModel.getPlantList().observe(this, Observer {
            plantsArrayList.clear()
            plantsArrayList.addAll(it)
        })
    }


}