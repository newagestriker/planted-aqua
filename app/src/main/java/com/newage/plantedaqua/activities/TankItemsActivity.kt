package com.newage.plantedaqua.activities


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE

import android.widget.ImageView
import androidx.appcompat.app.AlertDialog

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer

import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.newage.plantedaqua.R
import com.newage.plantedaqua.models.TankItems

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
        deleteItemImageView.setOnClickListener {

            tankItemListViewModel.deleteSelectedItems()

        }
        changeTankImageView.setOnClickListener{

            val alertDialog = AlertDialog.Builder(this@TankItemsActivity)
            alertDialog.setItems(tankItemListViewModel.getTankNames().toArray(arrayOf(String()))) { _: DialogInterface, i: Int ->
                tankItemListViewModel.moveItemToAnotherTank(i)
            }
                    .setNegativeButton(R.string.Cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setTitle("Select the tank to which you want to move the item(s)")
                    .create().show()
        }


        saveTankItemDetails.setOnClickListener{
            tankItemListViewModel.apply {
                if(getEachTankItem().itemName.isBlank()){
                    Snackbar.make(parentTankItemsLayout,"Item name should not be blank",Snackbar.LENGTH_LONG).show()
                }
                else {

                    saveTankItemDetails()
                    tankItemListViewModel.hasGoneToFragmentB(false)
                    Navigation.findNavController(this@TankItemsActivity,R.id.tankItemsNavHostFragment).navigate(R.id.action_createTankItemsFragment_to_tankItemListFragment)
                }
            }
        }

        resetEditModeImage.setOnClickListener{
            tankItemListViewModel.setEditMode(false)
        }

        addItemImageView.setOnClickListener {
            tankItemListViewModel.hasGoneToFragmentB(true)
            tankItemListViewModel.setEachTankItemLiveDataValue(-1)
            Navigation.findNavController(this@TankItemsActivity,R.id.tankItemsNavHostFragment).navigate(R.id.action_tankItemListFragment_to_createTankItemsFragment)
        }

        tankItemListViewModel.isEditModeActive().observe(this, Observer {
            if(it) {
                deleteItemImageView.showIconsAnimations()
                changeTankImageView.showIconsAnimations()
                resetEditModeImage.showIconsAnimations()
                tankItemListViewModel.setCheckBoxVisibility(true)
            }
            else {
                deleteItemImageView.hideIconsAnimations()
                changeTankImageView.hideIconsAnimations()
                resetEditModeImage.hideIconsAnimations()
                tankItemListViewModel.setCheckBoxVisibility(false)
            }
        })

        tankItemListViewModel.checkGoneToFragmentB().observe(this, androidx.lifecycle.Observer {
            if(it) {
                changeMenuOnMoveToEachItem()
            }
            else{
                changeMenuOnMoveToItemList()
            }
        })
    }

    private fun changeMenuOnMoveToEachItem(){
        addItemImageView.hideIconsAnimations()
        helpItemImageView.hideIconsAnimations()
        if(tankItemListViewModel.isCheckBoxVisible()) {
            changeTankImageView.hideIconsAnimations()
            deleteItemImageView.hideIconsAnimations()
            resetEditModeImage.hideIconsAnimations()
        }
        saveTankItemDetails.showIconsAnimations()
    }

    private fun changeMenuOnMoveToItemList(){
        addItemImageView.showIconsAnimations()
        helpItemImageView.showIconsAnimations()
        if(tankItemListViewModel.isCheckBoxVisible()) {
            changeTankImageView.showIconsAnimations()
            deleteItemImageView.showIconsAnimations()
            resetEditModeImage.showIconsAnimations()
        }
        saveTankItemDetails.hideIconsAnimations()
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
        if(saveTankItemDetails.visibility == VISIBLE){
            tankItemListViewModel.apply{
                hasGoneToFragmentB(false)
                deleteImageFileIfExists()
                restoreItemState()
                //  tankItemListViewModel.hasGoneToFragmentB(false)
            }
        }
        super.onBackPressed()
    }

}