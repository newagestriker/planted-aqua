package com.newage.aquapets.viewmodels

import android.app.Application
import android.database.Cursor
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.newage.aquapets.dbhelpers.ExpenseDBHelper
import com.newage.aquapets.dbhelpers.NutrientDbHelper
import com.newage.aquapets.dbhelpers.TankDBHelper
import com.newage.aquapets.helpers.TanksPlaceholderFragment
import com.newage.aquapets.helpers.TinyDB
import com.newage.aquapets.models.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class A1ViewModel(application: Application) : AndroidViewModel(application) {

    private var tanksDetailsArrayList: ArrayList<TankDetails>? = null
    private var tinyDB = TinyDB(application)
    private var expenseDBHelper = ExpenseDBHelper.getInstance(application)
    private var tankDBHelper = TankDBHelper.newInstance(application)
    private var tankDetailArrayListLiveData = MutableLiveData<ArrayList<TankDetails>>()
    var defaultCurrency: String = tinyDB.getString("DefaultCurrencySymbol")


    init {
        tanksDetailsArrayList = ArrayList()
        var tanksDetails: TankDetails
        var c: Cursor?
        var sum: Float
        val cursor = tankDBHelper!!.getData(tankDBHelper!!.readableDatabase)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    tanksDetails = TankDetails.tankDetailBuilder(cursor.getString(1))
                        .addCurrency(tinyDB.getString("DefaultCurrencySymbol"))
                        .addPrice(cursor.getString(5))
                        .addVolume(cursor.getString(7))
                        .addVolumeMetric(cursor.getString(8))
                        .addName(cursor.getString(2))
                        .addPicUri(cursor.getString(3))
                        .addType(cursor.getString(4))
                        .addStatus(cursor.getString(9))
                        .addStartDate(cursor.getString(10))
                        .addEndDate(cursor.getString(11)).addCo2Supply(cursor.getString(12)).addLightRegion(if (cursor.getString(16) == null) "" else cursor.getString(16))
                        .addMicroDosageText(if (cursor.getString(20) == null) "" else cursor.getString(20))
                        .addMacroDosageText(if (cursor.getString(21) == null) "" else cursor.getString(21))

                    //Get Tank Expense
                    sum = 0f
                    c = expenseDBHelper!!.getExpensePerGroupWithGroupValue(
                        "TankName",
                        "",
                        "",
                        tanksDetails.tankName
                    )
                    if (c != null) {
                        if (c.moveToFirst()) {
                            do {
                                sum += c.getFloat(1)
                            } while (c.moveToNext())
                        }
                        c.close()
                    }
                    tanksDetails.cumExpenses = String.format(Locale.getDefault(), "%.2f", sum)
                    tanksDetailsArrayList!!.add(tanksDetails)
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        tankDetailArrayListLiveData.value = tanksDetailsArrayList!!

    }

    fun getTankSectionPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle) =
        TanksSectionsPagerAdapter(fm, lifecycle)

    fun updateMicroDetails(selectedFragmentNumber: Int) {

        val cursor = tankDBHelper!!.getDataCondition(
            "AquariumID",
            tanksDetailsArrayList!![selectedFragmentNumber].tankID
        )
        cursor?.let {
            if (it.moveToFirst()) {
                tanksDetailsArrayList!![selectedFragmentNumber].microDosageText =
                    if (cursor.getString(20) == null) "" else cursor.getString(20)

            }
            it.close()
        }


    }

    fun updateMacroDetails(selectedFragmentNumber: Int) {

        val cursor = tankDBHelper!!.getDataCondition(
            "AquariumID",
            tanksDetailsArrayList!![selectedFragmentNumber].tankID
        )
        cursor?.let {
            if (it.moveToFirst()) {
                tanksDetailsArrayList!![selectedFragmentNumber].macroDosageText =
                    if (cursor.getString(21) == null) "" else cursor.getString(21)

            }
            it.close()
        }

    }

    fun updateTankGallons(selectedFragmentNumber: Int) {
        val cursor = tankDBHelper!!.getDataCondition(
            "AquariumID",
            tanksDetailsArrayList!![selectedFragmentNumber].tankID
        )
        cursor?.let {
            if (it.moveToFirst()) {
                tanksDetailsArrayList!![selectedFragmentNumber].tankVolume = cursor.getString(7)
                tanksDetailsArrayList!![selectedFragmentNumber].tankVolumeMetric =
                    cursor.getString(8)
                tanksDetailsArrayList!![selectedFragmentNumber].tankLightRegion =
                    if (cursor.getString(16) == null) "" else cursor.getString(16)
            }
            it.close()
        }

    }


    fun calcCumExpense(pos: Int) {

        var sum = 0f
        val c = expenseDBHelper!!.getExpensePerGroupWithGroupValue(
            "TankName",
            "",
            "",
            tanksDetailsArrayList!![pos].tankName
        )
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    sum += c.getFloat(1)
                } while (c.moveToNext())
            }
            c.close()
        }

        tanksDetailsArrayList!![pos].cumExpenses = String.format(Locale.getDefault(), "%.2f", sum)

    }

    fun getTankDetailsArrayList() = tanksDetailsArrayList

    fun getTankDetailArrayListLiveData() = tankDetailArrayListLiveData

    fun deleteTankDataFromDatabase(aquariumID: String) {


        CoroutineScope(Dispatchers.IO).launch {
            getApplication<Application>().deleteDatabase(aquariumID)
            tankDBHelper!!.deleteItem("AquariumID", aquariumID)
            tankDBHelper!!.deleteItemReco(aquariumID)
            tankDBHelper!!.deleteItemLight(aquariumID)
            val nutrientDbHelper = NutrientDbHelper.newInstance(getApplication(), aquariumID)
            expenseDBHelper!!.deleteExpense("AquariumID", aquariumID)
            nutrientDbHelper.deleteNutrientTables()
        }
    }

    fun setDefaultCurrency() {
        defaultCurrency = tinyDB.getString("DefaultCurrencySymbol")
    }

    fun getTankDetailsArrayListLength() = tanksDetailsArrayList!!.size


    inner class TanksSectionsPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle) :
        FragmentStateAdapter(fm, lifecycle) {


        override fun getItemCount(): Int {
            return tanksDetailsArrayList!!.size
        }

        override fun createFragment(position: Int): Fragment {
            return TanksPlaceholderFragment.newInstance(position)
        }
    }


}