package com.newage.plantedaqua.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.newage.plantedaqua.R
import com.newage.plantedaqua.viewmodels.PlantDatabaseActivityViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlantDatabaseActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plant_database)


    }

    fun redirectToUrl(view: View){
        val webIntent = Intent(this, ConditionsActivity::class.java)
        webIntent.putExtra("URL", "https://plantedaquaapp.blogspot.com/p/privacy-policy.html")
        startActivity(webIntent)
    }
}