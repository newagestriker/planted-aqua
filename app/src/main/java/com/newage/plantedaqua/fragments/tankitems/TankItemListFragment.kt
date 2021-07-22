package com.newage.plantedaqua.fragments.tankitems

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.newage.plantedaqua.R
import com.newage.plantedaqua.adapters.ShowcaseRecyclerAdapter
import com.newage.plantedaqua.models.TankItems
import com.newage.plantedaqua.viewmodels.TankItemListViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel


import org.koin.core.parameter.parametersOf
import timber.log.Timber

class TankItemListFragment : Fragment() {


    private val tankItemListViewModel : TankItemListViewModel by sharedViewModel()
    private lateinit var adapterTankItems: ShowcaseRecyclerAdapter<TankItems>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_tank_item_list, container, false)

        val itemsRecyclerView = view.findViewById<RecyclerView>(R.id.ItemsFragmentRecyclerView)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
        itemsRecyclerView.layoutManager = layoutManager
        adapterTankItems = ShowcaseRecyclerAdapter(R.layout.tank_item_list_layout,object : ShowcaseRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(view: View?, pos: Int) {
                if (view!!.id == R.id.tankItemCheckBox) {
                    tankItemListViewModel.addItemsToAddOrRemove((view as CheckBox).isChecked,pos)
                }
                else if(view.id == R.id.editTankImageItemView){
                    tankItemListViewModel.hasGoneToFragmentB(true)
                    tankItemListViewModel.setEachTankItemLiveDataValue(pos)
                    Navigation.findNavController(view).navigate(R.id.action_tankItemListFragment_to_createTankItemsFragment)
                }

            }

            override fun onItemLongClick(view: View?, pos: Int) {
                tankItemListViewModel.setEditMode(true)

            }
        })
        itemsRecyclerView.adapter = adapterTankItems

        tankItemListViewModel.getTankItems().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Timber.d("Change Observed")
            // val tempArrayList = ArrayList(it)
            val newList = mutableListOf<TankItems>()
            it.forEach { item -> newList.add(item.copy()) }
            adapterTankItems.submitList(newList)
        })

        return view
    }


}