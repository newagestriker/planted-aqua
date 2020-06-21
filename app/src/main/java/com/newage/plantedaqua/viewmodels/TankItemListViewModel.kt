package com.newage.plantedaqua.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.newage.plantedaqua.helpers.ExpenseDBHelper
import com.newage.plantedaqua.helpers.MyDbHelper
import com.newage.plantedaqua.helpers.TankDBHelper
import com.newage.plantedaqua.helpers.TinyDB
import com.newage.plantedaqua.models.TankItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TankItemListViewModel(application: Application,private val tankId: String,val category:String) : AndroidViewModel(application) {

    private val expenseDBHelper = ExpenseDBHelper.getInstance(application)
    private val myDbHelper = MyDbHelper.newInstance(application,tankId)
    private val tankItemsLiveData = MutableLiveData<ArrayList<TankItems>>()
    private val tankDBHelper = TankDBHelper.newInstance(application)
    private val tankItems = ArrayList<TankItems>()
    private var tankItem = TankItems()
    private var tankItemLiveData = MutableLiveData<TankItems>()

    private var image:File? = null

    private val settingsDB = TinyDB(application)
    // I_ID text,I_Name text,I_Category text,I_URI text,I_Currency text,I_Price real,I_Quantity integer,//6
    // I_BuyDate text,I_ExpDate text,I_Gender text,I_Food text,I_Care text,I_Quality text,I_Remarks text,I_Sci_Name text,I_Seller text)");

    init {

        tankItem.Currency = settingsDB.getString("DefaultCurrencySymbol")
        var tankItems1 = TankItems()
               val c = myDbHelper!!.getDataTICondition("I_Category", category)


        c?.let {
            if(c.moveToFirst()){
                do{

                    tankItems1 = TankItems()
                    tankItems1.itemName = c.getString(1)?:""
                    tankItems1.itemCategory = c.getString(2)?:""
                    tankItems1.Currency = c.getString(4)?:""
                    tankItems1.itemPrice = String.format(Locale.getDefault(),"%.2f", c.getFloat(5))
                    tankItems1.itemQuantity = c.getString(6)?:""
                    tankItems1.itemPurchaseDate = c.getString(7)?:""
                    tankItems1.itemExpiryDate = c.getString(8)?:""
                    tankItems1.itemGender = c.getString(9)?:""
                    tankItems1.food = c.getString(10)?:""
                    tankItems1.itemCare = c.getString(11)?:""
                    tankItems1.itemQuality = c.getString(12)?:""
                    tankItems1.itemScientificName = c.getString(14)?:""
                    tankItems1.itemSeller = c.getString(15)?:""
                    tankItems1.quickNote = c.getString(13)?:""
                    tankItems1.itemUri = c.getString(3)?:""

                    when (category) {
                        "E" -> {


                                tankItems1.tag = c.getString(0)?:""
                                tankItems1.txt1 = c.getString(1)?:""
                                tankItems1.txt2 = c.getInt(6).toString()
                                tankItems1.txt3 = c.getString(7)?:""
                                tankItems1.txt4 = c.getString(8)?:""



                        }
                        else -> {
                                tankItems1.tag = c.getString(0)?:""
                                tankItems1.txt1 = c.getString(1)?:""
                                tankItems1.txt2 = c.getInt(6).toString()
                                tankItems1.txt3 = c.getString(13)?:""
                                tankItems1.txt4 = c.getString(7)?:""


                        }
                    }
                    tankItems.add(tankItems1)
                }while (c.moveToNext())
            }
            c.close()
        }

        tankItemsLiveData.value = tankItems
    }

    fun setEachTankItemLiveDataValue(pos: Int){
        tankItem = tankItems[pos]
        resetEachTankItemLiveDataValue()
    }
     fun resetEachTankItemLiveDataValue(){
         tankItemLiveData.value = tankItem
     }

    fun getEachTankItemLiveData(): LiveData<TankItems>{
        return tankItemLiveData
    }
    fun getEachTankItem(): TankItems{
        return tankItem
    }

    fun getTankItems():LiveData<ArrayList<TankItems>>{
        return tankItemsLiveData
    }

    fun getImageFile():File?{
        return image
    }

    fun setImageFile(file : File){
        image = file
    }

    fun makeCheckBoxVisible(){

        for(element in tankItems){
            element.shown = true

        }

        tankItemsLiveData.value = tankItems


    }

    fun addItemsToAddOrRemove(toAdd:Boolean,pos:Int){
       tankItems[pos].checked = toAdd


    }



    fun deleteSelectedItems() {
        if(tankItems.isNotEmpty()){
            var subtracter = 1

            for (i in 0..(tankItems.size-subtracter)) {

                    if(tankItems[i].checked) {

                        myDbHelper!!.deleteItemTI("I_ID", tankItems[i].tag)
                        expenseDBHelper.deleteExpense("ItemID", tankItems[i].tag)
                        tankItems.removeAt(i)
                        subtracter++
                    }

            }
        }

        tankItemsLiveData.value = tankItems


    }

    fun saveTankItemDetails(){
        var aquaname = ""

        val c = tankDBHelper.getDataCondition("AquariumID",tankId )
        if (c != null) {
            if (c.moveToFirst()) {
                aquaname = c.getString(2)
            }
            c.close()
        }
        val db = myDbHelper.writableDatabase
        tankItem.apply {
            val numericPrice = itemPrice.replace(",", ".").toFloat()
            val numericQuantity = itemQuantity.toInt()
            if(itemPurchaseDate.isNullOrBlank()){
                itemPurchaseDate = "0000-00-00"
            }
            val dy = itemPurchaseDate.split("-".toRegex()).toTypedArray()[2].toInt()
            val mnth = itemPurchaseDate.split("-".toRegex()).toTypedArray()[1].toInt()
            val yr = itemPurchaseDate.split("-".toRegex()).toTypedArray()[0].toInt()
            if (mode == "creation") {
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val id = category + "_" + timeStamp
                myDbHelper.addDataTI(db, id, itemName, category, itemUri, Currency, numericPrice, numericQuantity, itemPurchaseDate, itemExpiryDate, itemGender, food, itemCare, itemRemarks, itemScientificName)
                expenseDBHelper.addDataExpense(expenseDBHelper.writableDatabase, id, aquaname, itemName, dy, mnth, yr, itemPurchaseDate, 0L, numericQuantity, numericPrice, numericPrice * numericQuantity, "", tankId)
            }
            if (mode == "modification") {

                myDbHelper.updateItemTI(db, tag, itemName, category, itemUri, Currency, numericPrice,numericQuantity, itemPurchaseDate, itemExpiryDate, itemGender, food, itemCare, itemRemarks, itemScientificName)
                if (expenseDBHelper.checkExists(tag)) {
                    expenseDBHelper.updateDataExpense(expenseDBHelper.writableDatabase, tag, aquaname, itemName, dy, mnth, yr, itemPurchaseDate, 0L, numericQuantity, numericPrice, numericPrice * numericQuantity, "", tankId)
                } else {
                    expenseDBHelper.addDataExpense(expenseDBHelper.writableDatabase, tag, aquaname, itemName, dy, mnth, yr, itemPurchaseDate, 0L, numericQuantity, numericPrice, numericPrice * numericQuantity, "", tankId)
                }
            }
        }

        tankItems.add(tankItem)
    }

    private val imageFileCopyJob = SupervisorJob()
     fun copyFileWithUri(sourceUri: Uri, destUri: Uri) {


        CoroutineScope(Dispatchers.IO + imageFileCopyJob).launch {
            getApplication<Application>().applicationContext.contentResolver.openOutputStream(destUri).use { oStream -> getApplication<Application>().applicationContext.contentResolver.openInputStream(sourceUri)?.use { iStream -> oStream!!.write(iStream.readBytes()) } }
        }
    }

    override fun onCleared() {
        imageFileCopyJob.cancel()
        super.onCleared()
    }



}