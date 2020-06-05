package com.newage.plantedaqua.helpers

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.newage.plantedaqua.R
import com.newage.plantedaqua.databinding.EachTankDetailLayoutBinding
import com.newage.plantedaqua.models.TanksDetails


class TanksPlaceholderFragment(private val tanksDetails:TanksDetails) : Fragment(),View.OnClickListener{

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.tankOptionsImage -> showTankOptions()
            R.id.resetImage -> showTankOptions()

        }
    }


    private var tankOptionsVisible = true

    private lateinit var  binding : EachTankDetailLayoutBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.each_tank_detail_layout,container,false)
        binding.tankOptionsImage.setOnClickListener(this)
        binding.resetImage.setOnClickListener(this)
        return binding.root
    }

    companion object{
       private const val TANKS_FRAGMENT_SECTION_NUMBER = "tank_section_number"
        @JvmStatic
        fun newInstance(tankDetails:TanksDetails,sectionNumber : Int):TanksPlaceholderFragment{
            val tanksPlaceholderFragment = TanksPlaceholderFragment(tankDetails)
            val args = Bundle()
            args.putInt(TANKS_FRAGMENT_SECTION_NUMBER,sectionNumber)
            tanksPlaceholderFragment.arguments = args
            return tanksPlaceholderFragment
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        binding.boundTankDetails=tanksDetails
        Glide.with(this)
                .load(tanksDetails.tankPicUri)
                .placeholder(R.drawable.aquarium2)
                .into(binding.imageView)

        super.onActivityCreated(savedInstanceState)
    }


    private fun showTankOptions(){

        binding.apply {
            tankOptionsImage.visibility = if (tankOptionsVisible) View.GONE else View.VISIBLE
            resetImage.fadeAnimation()
            deleteTank.fadeAnimation()
            editImage.fadeAnimation()

            if (tankOptionsVisible) {
                TankNameText.animate().translationY(-100f).duration = 300L
                constraintLayoutEachTankOptions.apply {
                    alpha = 0f
                    scaleX = 0f
                    scaleY = 0f
                    visibility = View.VISIBLE
                    animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(500L).setListener(null)
                }
            }
                 else {
                TankNameText.animate().translationY(0f).duration = 300L
                    constraintLayoutEachTankOptions.apply {

                        animate().alpha(0f).scaleX(0f).scaleY(0f).setDuration(500L).setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                visibility = View.GONE
                            }
                        })
                    }
                }


        }





        tankOptionsVisible=!tankOptionsVisible

    }

    private fun View.fadeAnimation(){
        if (tankOptionsVisible){
            alpha = 0f
            visibility = View.VISIBLE
            animate().alpha(1f).setDuration(500L)
        }
        else {

                        visibility = View.GONE

        }
    }


}

class TanksSectionsPagerAdapter(private val tanksDetailsList: ArrayList<TanksDetails>, fm:FragmentManager): FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){

    override fun getCount(): Int {
        return tanksDetailsList.size
    }

    override fun getItem(position: Int): Fragment {
        return TanksPlaceholderFragment.newInstance(tanksDetailsList[position],position)
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }
}