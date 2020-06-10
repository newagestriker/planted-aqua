package com.newage.plantedaqua.fragments.plantdb

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.newage.plantedaqua.R
import com.newage.plantedaqua.adapters.RecyclerAdapter
import com.newage.plantedaqua.adapters.ShowcaseRecyclerAdapter
import com.newage.plantedaqua.adapters.ShowcaseRecyclerAdapter.OnItemClickListener
import com.newage.plantedaqua.models.Plants
import kotlinx.android.synthetic.main.content_a1.*
import kotlinx.android.synthetic.main.fragment_plant_list.*
import kotlinx.android.synthetic.main.fragment_plant_list.view.*
import kotlinx.android.synthetic.main.fragment_plant_list.view.plantsRecyclerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PlantList.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlantList : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var plantsArrayList: ArrayList<Plants>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        plantsArrayList = ArrayList()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_plant_list, container, false)
        setUpViews(view)
        return view
    }

    private fun setUpViews(mainView: View){

        plantsArrayList.add(Plants("1","Java Fern","Microsorum pteropus",plantPicUri = "https://upload.wikimedia.org/wikipedia/commons/8/89/Microsorum_pteropus.jpg"))

        val recyclerAdapter = ShowcaseRecyclerAdapter(plantsArrayList,R.layout.each_plant_item,object: OnItemClickListener{
            override fun onItemClick(view: View?, pos: Int) {
                Navigation.findNavController(mainView).navigate(R.id.action_plantList_to_plantDetails)
            }
        })

        mainView.plantsRecyclerView.adapter = recyclerAdapter
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PlantList.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                PlantList().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}