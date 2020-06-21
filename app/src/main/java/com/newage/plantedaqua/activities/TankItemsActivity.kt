package com.newage.plantedaqua.activities


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE

import android.widget.ImageView

import androidx.constraintlayout.widget.ConstraintLayout

import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.newage.plantedaqua.R

import com.newage.plantedaqua.viewmodels.TankItemListViewModel
import kotlinx.android.synthetic.main.activity_tank_items.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

import org.koin.core.parameter.parametersOf



class TankItemsActivity : AppCompatActivity() {
    var aquariumID : String? =null
    private var category : String? = null

    private lateinit var  tankItemListViewModel : TankItemListViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tank_items)
        aquariumID = intent.getStringExtra("AquariumID")
        category = intent.getStringExtra("ItemCategory")
        tankItemListViewModel = getViewModel { parametersOf(aquariumID,category) }
        val deleteTankItems = findViewById<ImageView>(R.id.deleteItemImageView)
        val parentLayout = findViewById<ConstraintLayout>(R.id.parentTankItemsLayout)
        deleteTankItems.setOnClickListener {

            tankItemListViewModel.deleteSelectedItems()

        }

        val saveTankItemDetails = findViewById<ImageView>(R.id.saveTankItemDetails)
        saveTankItemDetails.setOnClickListener{
            tankItemListViewModel.apply {
                if(getEachTankItem().itemName.isBlank()){
                    Snackbar.make(parentLayout,"Item name should not be blank",Snackbar.LENGTH_LONG).show()
                }
                else {
                    saveTankItemDetails()
                    addItemImageView.showIconsAnimations()
                    helpItemImageView.showIconsAnimations()
                    changeTankImageView.showIconsAnimations()
                    deleteTankItems.showIconsAnimations()
                    saveTankItemDetails.hideIconsAnimations()
                    Navigation.findNavController(this@TankItemsActivity,R.id.tankItemsNavHostFragment).navigate(R.id.action_createTankItemsFragment_to_tankItemListFragment)
                }
            }
        }


        val addItemImageView = findViewById<ImageView>(R.id.addItemImageView)
        addItemImageView.setOnClickListener {
            addItemImageView.hideIconsAnimations()
            helpItemImageView.hideIconsAnimations()
            changeTankImageView.hideIconsAnimations()
            deleteTankItems.hideIconsAnimations()
            saveTankItemDetails.showIconsAnimations()
            Navigation.findNavController(this@TankItemsActivity,R.id.tankItemsNavHostFragment).navigate(R.id.action_tankItemListFragment_to_createTankItemsFragment)
        }
    }

    private fun View.showIconsAnimations(){

        visibility = VISIBLE
        scaleX = 0f
        scaleY = 0f
        alpha = 0f
        animate().scaleY(1f).scaleX(1f).alpha(1f).setDuration(500L).setListener(null)
    }

    private fun View.hideIconsAnimations(){


        animate().scaleY(0f).scaleX(0f).alpha(0f).setDuration(500L).setListener(object : AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                visibility = GONE
            }
        })
    }


    override fun onBackPressed() {
        super.onBackPressed()

       tankItemListViewModel.getImageFile()?.let{
           if(it.exists()){
               it.delete()
               tankItemListViewModel.getEachTankItem().itemUri = ""
           }
       }


    }

}