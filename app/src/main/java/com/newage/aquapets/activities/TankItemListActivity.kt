package com.newage.aquapets.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.newage.aquapets.R
import com.newage.aquapets.adapters.ShowcaseRecyclerAdapter
import com.newage.aquapets.dbhelpers.MyDbHelper
import com.newage.aquapets.models.TankItems
import com.newage.aquapets.viewmodels.TankItemListViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber
import kotlin.collections.ArrayList

class TankItemListActivity : AppCompatActivity() {
    private lateinit var adapterTankItems: ShowcaseRecyclerAdapter<TankItems>
    private val ITEM_DETAILS_CREATION = 45
    private val ITEMS_DETAILS_MODIFICATION = 43
    private val tankItems = ArrayList<TankItems>()
    private var tankItems1 = TankItems()
    private var aquariumID: String? = null
    private var category: String? = null
    private var tankItemsLinear: LinearLayout? = null
    private var myDbHelper: MyDbHelper? = null
    private val tankItemListViewModel : TankItemListViewModel  by viewModel { parametersOf(aquariumID,category)  }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tank_item_list)
        aquariumID = intent.getStringExtra("AquariumID")
        category = intent.getStringExtra("ItemCategory")
        val aquaName = intent.getStringExtra("AquariumName")
        val type: String = when (category) {
            "E" -> resources.getString(R.string.Eq)
            "Fr" -> resources.getString(R.string.Fauna)
            "Fl" -> resources.getString(R.string.Flora)
            else -> ""
        }

        supportActionBar!!.title = "$type : $aquaName"
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayUseLogoEnabled(true)
//        myDbHelper = MyDbHelper.newInstance(this, aquariumID)
//        val c = myDbHelper!!.getDataTICondition("I_Category", category)
//        when (category) {
//            "E" -> {
//                while (c.moveToNext()) {
//                    tankItems1 = TankItems()
//                    tankItems1.tag = c.getString(0)
//                    tankItems1.txt1 = c.getString(1)
//                    tankItems1.txt2 = c.getInt(6).toString()
//                    tankItems1.txt3 = c.getString(7)
//                    tankItems1.txt4 = c.getString(8)
//                    tankItems1.quickNote = c.getString(13)
//                    tankItems1.itemUri = c.getString(3)
//                    tankItems.add(tankItems1)
//                }
//            }
//            else -> {
//                while (c.moveToNext()) {
//                    tankItems1 = TankItems()
//                    tankItems1.tag = c.getString(0)
//                    tankItems1.txt1 = c.getString(1)
//                    tankItems1.txt2 = c.getInt(6).toString()
//                    tankItems1.txt3 = c.getString(13)
//                    tankItems1.txt4 = c.getString(7)
//                    tankItems1.quickNote = c.getString(12)
//                    tankItems1.itemUri = c.getString(3)
//                    tankItems.add(tankItems1)
//                }
//            }
//        }
//        c.close()
        tankItemsLinear = findViewById(R.id.TankItemsLinear)

        val itemsRecyclerView = findViewById<RecyclerView>(R.id.ItemsRecyclerView)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        itemsRecyclerView.layoutManager = layoutManager
        adapterTankItems = ShowcaseRecyclerAdapter(R.layout.tank_item_list_layout,object : ShowcaseRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(view: View?, pos: Int) {
                if (view!!.id == R.id.tankItemCheckBox) {
                    tankItemListViewModel.addItemsToAddOrRemove((view as CheckBox).isChecked,pos)
                }

            }

            override fun onItemLongClick(view: View?, pos: Int) {
                tankItemListViewModel.setEditMode(true)
            }
        })
        itemsRecyclerView.adapter = adapterTankItems

        tankItemListViewModel.getTankItems().observe(this, androidx.lifecycle.Observer {
            Timber.d("Change Observed")
           // val tempArrayList = ArrayList(it)
            val newList = mutableListOf<TankItems>()
            it.forEach { item -> newList.add(item.copy()) }

            adapterTankItems.submitList(newList)
        })

//        adapterTankItems = RecyclerAdapterTankItems(tankItems, category, this, object : RecyclerAdapterTankItems.OnItemClickListener {
//            override fun onClick(view: View, position: Int, uri: String) {
//                if (view.id == R.id.tankItemCheckBox) {
//                    tankItemListViewModel.addItemsToRemoveOrRemove((view as CheckBox).isChecked,tankItems[position].tag,position)
//
//                                        Timber.d("---------NEW LIST-------");
//                    for(Map.Entry<String,Integer> entry : selectedItems.entrySet()){
//                        Timber.d(entry.getKey());
//
//                        }
//                } else {
//                    quickNote(position)
//                }
//            }
//
//            override fun onLongClick(view: View, position: Int) {
//                for (tankItem in tankItems) {
//                    tankItem.shown = true
//                    adapterTankItems!!.notifyDataSetChanged()
//                }
//
//                Intent intent=new Intent(TankItemListActivity.this,CreateTankItemsActivity.class);
//                intent.putExtra("mode","modification");
//                intent.putExtra("ItemID",tankItems.get(position).getTag());
//                intent.putExtra("AquariumID",aquariumID);
//                intent.putExtra("ItemCategory",category);
//                intent.putExtra("position",position);
//                startActivityForResult(intent,ITEMS_DETAILS_MODIFICATION);
//            }
//        })
//        itemsRecyclerView.adapter = adapterTankItems
//        val simpleCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
//            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
//                return false
//            }
//
//            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                val position = viewHolder.layoutPosition //swiped position
//                clicked = false
//                if (direction == ItemTouchHelper.LEFT || direction == ItemTouchHelper.RIGHT) { //swipe left
//                    temp = TankItems()
//                    temp = tankItems[position]
//                    TagList.add(temp!!.tag)
//                    tankItems.removeAt(position)
//                    adapterTankItems!!.notifyItemRemoved(position)
//                    snackbar = Snackbar
//                            .make(tankItemsLinear!!, "Record deleted", Snackbar.LENGTH_LONG)
//                            .setAction("UNDO") {
//                                tankItems.add(position, temp!!)
//                                clicked = true
//                                TagList.removeAt(TagList.size - 1)
//                                adapterTankItems!!.notifyItemInserted(position)
//                                snackbar!!.dismiss()
//                            }.addCallback(object : Snackbar.Callback() {
//                                override fun onDismissed(snackbar: Snackbar, dismissType: Int) {
//                                    super.onDismissed(snackbar, dismissType)
//                                    var Tag: String
//                                    if ((dismissType == DISMISS_EVENT_TIMEOUT || dismissType == DISMISS_EVENT_ACTION || dismissType == DISMISS_EVENT_SWIPE || dismissType == DISMISS_EVENT_CONSECUTIVE || dismissType == DISMISS_EVENT_MANUAL) && !clicked) {
//                                        for (i in TagList.indices) {
//                                            Tag = TagList[i]
//                                            myDbHelper!!.deleteItemTI("I_ID", Tag)
//                                            val expenseDBHelper = ExpenseDBHelper.getInstance(this@TankItemListActivity)
//                                            expenseDBHelper.deleteExpense("ItemID", Tag)
//                                            TagList.removeAt(i)
//                                            //System.out.println("Tag : "+Tag);
//                                        }
//                                    }
//                                }
//                            })
//                    snackbar!!.show()
//                }
//            }
//        }
//        val itemTouchHelper = ItemTouchHelper(simpleCallback)
//        itemTouchHelper.attachToRecyclerView(itemsRecyclerView)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ITEM_DETAILS_CREATION) {
                tankItems1 = TankItems()
                tankItems1.itemUri = data!!.getStringExtra("ImageUri")!!
                tankItems1.txt1 = data.getStringExtra("Txt1")!!
                tankItems1.txt2 = data.getStringExtra("Txt2")!!
                tankItems1.txt3 = data.getStringExtra("Txt3")!!
                tankItems1.txt4 = data.getStringExtra("Txt4")!!
                tankItems1.tag = data.getStringExtra("Tag")!!
                tankItems1.quickNote = data.getStringExtra("QuickNote")!!
                tankItems1.shown = tankItems[tankItems.size - 1].shown
                tankItems.add(tankItems1)
                adapterTankItems.notifyItemInserted(tankItems.size - 1)
            }
            if (requestCode == ITEMS_DETAILS_MODIFICATION) {
                val position = data!!.getIntExtra("Position", -1)
                tankItems[position].itemUri = data.getStringExtra("ImageUri")!!
                tankItems[position].txt1 = data.getStringExtra("Txt1")!!
                tankItems[position].txt2 = data.getStringExtra("Txt2")!!
                tankItems[position].txt3 = data.getStringExtra("Txt3")!!
                tankItems[position].txt4 = data.getStringExtra("Txt4")!!
                tankItems[position].tag = data.getStringExtra("Tag")!!
                tankItems[position].quickNote = data.getStringExtra("QuickNote")!!
                adapterTankItems.notifyItemChanged(position)

                //System.out.println(tankItems.get(position).getTxt3());
            }
        }
    }


    private fun quickNote(position: Int) {
        val preNote: String
        val id = tankItems[position].tag
        val view = layoutInflater.inflate(R.layout.quicknote_dialog, null)
        val quickNote = view.findViewById<EditText>(R.id.enterQuickNote)
        quickNote.isCursorVisible = false
        quickNote.setOnClickListener { quickNote.isCursorVisible = true }
        val c = myDbHelper!!.getDataTICondition("I_ID", id)
        c.moveToNext()
        preNote = c.getString(13)
        c.close()
        quickNote.setText(preNote)
        val builder = AlertDialog.Builder(this)
        builder.setView(view)
                .setTitle(R.string.QN)
                .setIcon(R.drawable.plantedaqua)
                .setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
                    val qNote = quickNote.text.toString()
                    tankItems[position].quickNote = qNote
                    adapterTankItems.notifyItemChanged(position)
                    myDbHelper!!.updateItemTISingleItem(id, "I_Remarks", qNote)
                }
                .setNegativeButton(resources.getString(R.string.Cancel)) { dialog, _ -> dialog.dismiss() }
        builder.show()
    }
}