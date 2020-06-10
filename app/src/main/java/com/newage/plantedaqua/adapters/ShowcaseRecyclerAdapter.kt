package com.newage.plantedaqua.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.newage.plantedaqua.R
import com.newage.plantedaqua.databinding.EachPlantItemBinding
import com.newage.plantedaqua.databinding.ShowcaseRecyclerViewItemBinding
import com.newage.plantedaqua.models.GalleryInfo
import com.newage.plantedaqua.models.Plants

class ShowcaseRecyclerAdapter<T>(val items : ArrayList<T>, private val layout_ID : Int, private val onItemClickListener: OnItemClickListener) : RecyclerView.Adapter<ShowcaseRecyclerAdapter.ShowcaseViewHolder>() {

    interface OnItemClickListener{
        fun onItemClick(view:View?,pos:Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowcaseViewHolder {
        return ShowcaseViewHolder.from(parent,layout_ID,onItemClickListener)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ShowcaseViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class ShowcaseViewHolder(private val binding: ViewDataBinding, private val onItemClickListener: OnItemClickListener,private val layout_ID: Int) : RecyclerView.ViewHolder(binding.root),View.OnClickListener {

        override fun onClick(v: View?) {
            onItemClickListener.onItemClick(v,layoutPosition)
        }

        init {
            when(layout_ID) {
                R.layout.showcase_recycler_view_item -> {
                    (binding as ShowcaseRecyclerViewItemBinding).showcaseTankMainLayout.setOnClickListener(this)
                }
                R.layout.each_plant_item -> {
                    (binding as EachPlantItemBinding).plantsItemMainLayout.setOnClickListener(this)
                }
            }

        }

        companion object {
            fun from(parent: ViewGroup, layout_ID: Int, onItemClickListener: OnItemClickListener) : ShowcaseViewHolder {

                //Matching Bindings with Layout ID

                val binding: ViewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), layout_ID, parent, false)
                return ShowcaseViewHolder(binding,onItemClickListener,layout_ID)
            }
        }


        fun <T> bind(item:T){

            //Create an instance of Circular Progress Drawable
            val circularProgressDrawable = CircularProgressDrawable(binding.root.context)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.start()

            //Matching Bindings with Layout ID
            when(layout_ID) {
                R.layout.showcase_recycler_view_item -> {

                    val picUrl = (item as GalleryInfo).tankImageURL.replace("\\u003d", "=")
                    Glide.with(binding.root.context)
                            .load(picUrl)
                            .placeholder(circularProgressDrawable)
                            .into((binding as ShowcaseRecyclerViewItemBinding).tankImageInShowcase)
                    binding.setVariable(BR.galleryInfo,item)
                }

                R.layout.each_plant_item -> {
                    Glide.with(binding.root.context)
                            .load((item as Plants).plantPicUri)
                            .placeholder(circularProgressDrawable)
                            .into((binding as EachPlantItemBinding).plantPic)
                    binding.setVariable(BR.boundPlants,item)
                }
            }

            binding.executePendingBindings()

        }

    }





}