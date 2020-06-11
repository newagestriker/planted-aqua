package com.newage.plantedaqua.fragments.plantdb

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.newage.plantedaqua.R
import com.newage.plantedaqua.activities.ConditionsActivity
import com.newage.plantedaqua.databinding.EachPlantItemBinding
import com.newage.plantedaqua.databinding.FragmentPlantDetailsBinding
import com.newage.plantedaqua.models.Plants
import kotlinx.android.synthetic.main.showcase_recycler_view_item.*


class PlantDetails : Fragment(), View.OnClickListener{

    private lateinit var binding: FragmentPlantDetailsBinding

    override fun onClick(v: View?) {
        when(v!!.tag){

            "authorlink" -> {redirectToUrl(binding.mainBoundPlants?.plantPicAuthorLink)}
            "licencelink" -> {redirectToUrl(binding.mainBoundPlants?.plantPicLicenceLink)}

        }
    }

    private fun redirectToUrl(url:String?){
        url?.let {
            val webIntent = Intent(activity, ConditionsActivity::class.java)
            webIntent.putExtra("URL", it)
            startActivity(webIntent)
        }
    }

    private val args : PlantDetailsArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_plant_details,container,false)
        binding.lifecycleOwner = this
        binding.mainBoundPlants = args.receivedPlant

        //Create an instance of Circular Progress Drawable
        val circularProgressDrawable = CircularProgressDrawable(binding.root.context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()
        binding.apply {
            Glide.with(root.context)
                    .load(mainBoundPlants!!.plantPicUri)
                    .placeholder(circularProgressDrawable)
                    .into(MainPlantImage)
            invalidateAll()

            AuthorNameText.setOnClickListener(this@PlantDetails)
            LicenceName.setOnClickListener(this@PlantDetails)
        }



        return binding.root
    }


}