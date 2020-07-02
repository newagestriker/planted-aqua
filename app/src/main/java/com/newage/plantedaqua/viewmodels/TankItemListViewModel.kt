package com.newage.plantedaqua.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.newage.plantedaqua.dbhelpers.ExpenseDBHelper
import com.newage.plantedaqua.dbhelpers.MyDbHelper
import com.newage.plantedaqua.dbhelpers.TankDBHelper
import com.newage.plantedaqua.helpers.TinyDB
import com.newage.plantedaqua.models.TankItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
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
    private var goneToFragmentB = MutableLiveData<Boolean>()
    private var image:File? = null
    private var pos = -1
    private var tempItem = TankItems()
    private var editModeLiveData = MutableLiveData<Boolean>()
    private var checkBoxVisibility = false
    private val settingsDB = TinyDB(application)
    private var tankPicUriFromGallery : Uri? = null
    private var newUri : Uri? = null
    private var newFile : File? = null
    private var uriToDisplayImageLiveData = MutableLiveData<Uri>()
    private var tankNames = ArrayList<String>()
    private var tankIds = ArrayList<String>()
    // I_ID text,I_Name text,I_Category text,I_URI text,I_Currency text,I_Price real,I_Quantity integer,//6
    // I_BuyDate text,I_ExpDate text,I_Gender text,I_Food text,I_Care text,I_Quality text,I_Remarks text,I_Sci_Name text,I_Seller text)");

    init {

        val defaultCurrency = settingsDB.getString("DefaultCurrencySymbol")
        var tankItems1 = TankItems()
               val c = myDbHelper!!.getDataTICondition("I_Category", category)

        goneToFragmentB.value = false

        c?.let {
            if(c.moveToFirst()){
                do{

                    tankItems1 = TankItems()
                    tankItems1.itemName = c.getString(1)?:""
                    tankItems1.itemCategory = c.getString(2)?:""
                    tankItems1.Currency =  defaultCurrency
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
                    tankItems1.tag = c.getString(0)?:""
                    tankItems.add(tankItems1)
                }while (c.moveToNext())
            }
            c.close()
        }

        tankItemsLiveData.value = tankItems

        setEditMode(false)

        val cursor = tankDBHelper.getData(tankDBHelper.readableDatabase)
        cursor?.let{
            if(it.moveToFirst()){
                do {
                    //tankList[it.getString(1)]=it.getString(2)
                    if(it.getString(1)!=tankId) {
                        tankIds.add(it.getString(1))
                        tankNames.add(it.getString(2))
                    }
                }while (it.moveToNext())
            }
            it.close()
        }



    }

    fun getTankNames() = tankNames

    fun getTankIDFromPosition(pos: Int){
        Timber.d("Tank ID clicked ${tankIds[pos]}")
    }

    fun setGender(gender:String){
            tankItem.itemGender = gender
    }

    fun moveItemToAnotherTank(selectedPosition: Int) {


        val newTankId = tankIds[selectedPosition]
        val newTankDB = MyDbHelper.newInstance(getApplication(), newTankId)

        CoroutineScope(Dispatchers.IO).launch {
            if (tankItems.isNotEmpty()) {

                val itr: MutableIterator<TankItems> = tankItems.iterator()
                while (itr.hasNext()) {
                    val item = itr.next()
                    if (item.checked) {
                        Timber.d(item.tag)

                        val job = SupervisorJob()
                        CoroutineScope(Dispatchers.IO + job).launch {

                            item.apply {

                                newTankDB.addDataTI(newTankDB.writableDatabase, tag, itemName, category, itemUri, Currency, itemPrice.replace(",", ".").toFloat(), itemQuantity.toInt(), itemPurchaseDate, itemExpiryDate, itemGender, food, itemCare, itemRemarks, itemScientificName)
                                expenseDBHelper.updateExpenseItem("ItemID", tag, "AquariumID", newTankId)
                                expenseDBHelper.updateExpenseItem("ItemID", tag, "TankName", tankNames[selectedPosition])
                                myDbHelper!!.deleteItemTI("I_ID", tag)
                            }


                        }
                        itr.remove()
                    }
                }

                tankItemsLiveData.postValue(tankItems)

            }
        }
    }

    fun isEditModeActive():LiveData<Boolean> = editModeLiveData


    fun setEditMode(active : Boolean){
        editModeLiveData.value = active
    }

    fun checkGoneToFragmentB() : LiveData<Boolean> = goneToFragmentB


    fun hasGoneToFragmentB(hasGone:Boolean){
        goneToFragmentB.value = hasGone
    }

    fun setEachTankItemLiveDataValue(pos: Int){

        if(pos ==-1) {

           tankItem= TankItems()
            tankItem.itemCategory = category
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            tankItem.tag = category + "_" + timeStamp
            tankItem.mode = "creation"
            setNewUri(null)
            setDisplayImageUri(null)
        } else {

            tankItem= tankItems[pos]
            tempItem = tankItem.copy() // to restore previous state if back button is pressed
            tankItem.mode = "modification"
            setNewUri(Uri.parse(tankItem.itemUri))
            setDisplayImageUri(Uri.parse(tankItem.itemUri))
            this.pos = pos
        }



        resetEachTankItemLiveDataValue()
    }
     fun resetEachTankItemLiveDataValue(){
         tankItemLiveData.value = tankItem
     }

    fun getEachTankItemLiveData(): LiveData<TankItems> = tankItemLiveData

    fun getEachTankItem(): TankItems = tankItem


    //fun getTankPicUriFromGallery() : Uri = tankPicUriFromGallery!!


    fun getTankItems():LiveData<ArrayList<TankItems>> =  tankItemsLiveData


    //region ImageFile functions

    fun getDisplayImageUri() : LiveData<Uri> = uriToDisplayImageLiveData
    fun getImageFile():File?{
        return image
    }

    fun setImageFile(file: File?){

        file?.let{deleteImageFileIfExists()}
        image = file
    }

    fun deleteImageFileIfExists(){
       getImageFile()?.let{
           Timber.d("Image not null")
            if(it.absoluteFile.exists()){
                Timber.d("Image deleted")
                it.delete()
            }
        }
    }

    fun setTankPicUriFromGallery(uri: Uri?){
        tankPicUriFromGallery = uri
    }

    fun getTankPicUriFromGallery() = tankPicUriFromGallery

    fun setNewUri(uri: Uri?){
        newUri = uri
    }

    fun getNewUri() = newUri

    fun setNewFile(file: File?){
        newFile = file
    }

    fun getNewFile() = newFile

    fun setDisplayImageUri(uri: Uri?){
            uriToDisplayImageLiveData.value = uri

    }

    //endregion

    fun setCheckBoxVisibility(isVisible : Boolean){

        checkBoxVisibility = isVisible
        for(element in tankItems){
            element.shown = isVisible

        }

        tankItemsLiveData.value = tankItems


    }

    fun isCheckBoxVisible():Boolean = checkBoxVisibility

    fun addItemsToAddOrRemove(toAdd:Boolean,pos:Int){
       tankItems[pos].checked = toAdd

    }


    fun deleteSelectedItems() {

        CoroutineScope(Dispatchers.IO).launch {
        if(tankItems.isNotEmpty()) {

            val itr: MutableIterator<TankItems> = tankItems.iterator()
            while (itr.hasNext()) {
                val item = itr.next()
                if (item.checked) {
                    Timber.d(item.tag)


                    CoroutineScope(Dispatchers.IO).launch {
                        myDbHelper!!.deleteItemTI("I_ID", item.tag)
                        expenseDBHelper.deleteExpense("ItemID", item.tag)
                        val file = File(item.itemUri)
                        if (file.absoluteFile.exists()) {
                            file.delete()
                            Timber.d("Image file deleted along with entire item")
                        }
                    }
                    itr.remove()
                }
            }

            tankItemsLiveData.postValue(tankItems)
        }
//            var subtracter = 1
//
//            for (i in 0..(tankItems.size-subtracter)) {
//
//                    if(tankItems[i].checked) {
//
//                        myDbHelper!!.deleteItemTI("I_ID", tankItems[i].tag)
//                        expenseDBHelper.deleteExpense("ItemID", tankItems[i].tag)
//                        tankItems.removeAt(i)
//                        subtracter++
//                    }
//
//            }
        }




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
            Timber.d(tankItem.itemUri)
            newUri?.let {
                Timber.d("There is new URI")
                if(itemUri.isNotBlank()) {
                    Timber.d("Item URI path :${Uri.parse(itemUri).path!!}")
                    val file = File(Uri.parse(itemUri).path!!)
                    if (file.exists()) {
                        Timber.d("File exists")
                        CoroutineScope(Dispatchers.IO).launch {
                            file.delete()
                            Timber.d("First Image deleted")
                        }
                    }
                }

                itemUri = it.toString()
            }
            tankPicUriFromGallery?.let {
                Timber.d("TankPicUri From Gallery not null")
                copyFileWithUri(it,Uri.parse(itemUri))
            }

            itemPrice = itemPrice.returnZeroIfBlank()
            itemQuantity = itemQuantity.returnOneIfBlank()

            val numericPrice = getNumericPrice()
            val numericQuantity = getNumericQuantity()
            if(itemPurchaseDate.isBlank()){
                itemPurchaseDate = "0000-00-00"
            }
            val dy = itemPurchaseDate.split("-".toRegex()).toTypedArray()[2].toInt()
            val mnth = itemPurchaseDate.split("-".toRegex()).toTypedArray()[1].toInt()
            val yr = itemPurchaseDate.split("-".toRegex()).toTypedArray()[0].toInt()
            if (mode == "creation") {
                myDbHelper.addDataTI(db, tag, itemName, category, itemUri, Currency, numericPrice, numericQuantity, itemPurchaseDate, itemExpiryDate, itemGender, food, itemCare, itemRemarks, itemScientificName)
                expenseDBHelper.addDataExpense(expenseDBHelper.writableDatabase, tag, aquaname, itemName, dy, mnth, yr, itemPurchaseDate, 0L, numericQuantity, numericPrice, numericPrice * numericQuantity, "", tankId)
                tankItems.add(tankItem)
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

        setImageFile(null)


    }

    fun restoreItemState(){
        Timber.d(tempItem.itemName)
        if(tempItem.mode=="modification") {
            tankItems[pos] = tempItem
        }

    }

    private val imageFileCopyJob = SupervisorJob()
     private fun copyFileWithUri(sourceUri: Uri, destUri: Uri) {


        CoroutineScope(Dispatchers.IO + imageFileCopyJob).launch {
            getApplication<Application>().applicationContext.contentResolver.openOutputStream(destUri).use { oStream -> getApplication<Application>().applicationContext.contentResolver.openInputStream(sourceUri)?.use { iStream -> oStream!!.write(iStream.readBytes()) } }
        }
    }

    override fun onCleared() {
        imageFileCopyJob.cancel()
        super.onCleared()
    }



}