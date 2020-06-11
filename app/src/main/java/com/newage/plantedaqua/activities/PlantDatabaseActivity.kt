package com.newage.plantedaqua.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.newage.plantedaqua.R
import com.newage.plantedaqua.models.Plants
import com.newage.plantedaqua.viewmodels.PlantDatabaseActivityViewModel
import org.koin.android.ext.android.inject
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