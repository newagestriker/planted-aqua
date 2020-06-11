package com.newage.plantedaqua.fragments.plantdb

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.newage.plantedaqua.R
import com.newage.plantedaqua.adapters.RecyclerAdapter
import com.newage.plantedaqua.adapters.ShowcaseRecyclerAdapter
import com.newage.plantedaqua.adapters.ShowcaseRecyclerAdapter.OnItemClickListener
import com.newage.plantedaqua.models.Plants
import com.newage.plantedaqua.viewmodels.PlantDatabaseActivityViewModel
import kotlinx.android.synthetic.main.content_a1.*
import kotlinx.android.synthetic.main.fragment_plant_list.*
import kotlinx.android.synthetic.main.fragment_plant_list.view.*
import kotlinx.android.synthetic.main.fragment_plant_list.view.plantsRecyclerView
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class PlantList : Fragment() {

    private val plantDatabaseActivityViewModel : PlantDatabaseActivityViewModel by sharedViewModel()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_plant_list, container, false)
        setUpViews(view)
        return view
    }

    private fun setUpViews(mainView: View){

        val plantsArrayList: ArrayList<Plants> = ArrayList()
   //     plantsArrayList.add(Plants(1L,"Java Fern","Microsorum pteropus",plantPicUri = "https://upload.wikimedia.org/wikipedia/commons/8/89/Microsorum_pteropus.jpg",plantAuthorName = "Mainak Dey",plantPicLicenceName = "by CC0"))

        val recyclerAdapter = ShowcaseRecyclerAdapter(plantsArrayList,R.layout.each_plant_item,object: OnItemClickListener{
            override fun onItemClick(view: View?, pos: Int) {
                val action = PlantListDirections.actionPlantListToPlantDetails(plantsArrayList[pos])
                Navigation.findNavController(mainView).navigate(action)
            }
        })
        mainView.plantsRecyclerView.adapter = recyclerAdapter
        plantDatabaseActivityViewModel.getPlantList().observe(viewLifecycleOwner, Observer {
            plantsArrayList.clear()
            plantsArrayList.addAll(it)
            recyclerAdapter.notifyDataSetChanged()

        })

    }




}