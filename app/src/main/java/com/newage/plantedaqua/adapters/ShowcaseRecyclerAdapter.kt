package com.newage.plantedaqua.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.newage.plantedaqua.R
import com.newage.plantedaqua.databinding.EachPlantItemBinding
import com.newage.plantedaqua.databinding.ShowcaseRecyclerViewItemBinding
import com.newage.plantedaqua.databinding.TankItemListLayoutBinding
import com.newage.plantedaqua.models.GalleryInfo
import com.newage.plantedaqua.models.Plants
import com.newage.plantedaqua.models.TankItems
import timber.log.Timber


class ShowcaseRecyclerAdapter<T>(private val layout_ID : Int, private val onItemClickListener: OnItemClickListener) : ListAdapter<T,ShowcaseRecyclerAdapter.ShowcaseViewHolder>(ShowcaseDiffCallBack<T>(layout_ID)) {

    interface OnItemClickListener{
        fun onItemClick(view:View?,pos:Int)
        fun onItemLongClick(view:View?,pos:Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowcaseViewHolder {
        return ShowcaseViewHolder.from(parent,layout_ID,onItemClickListener)
    }


    override fun onBindViewHolder(holder: ShowcaseViewHolder, position: Int) {
        holder.bind(getItem(position))

    }

    class ShowcaseViewHolder(private val binding: ViewDataBinding, private val onItemClickListener: OnItemClickListener,private val layout_ID: Int) : RecyclerView.ViewHolder(binding.root),View.OnClickListener,View.OnLongClickListener {

        override fun onLongClick(v: View?): Boolean {
            onItemClickListener.onItemLongClick(v,layoutPosition)
            return true
        }
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

                R.layout.tank_item_list_layout -> {
                    (binding as TankItemListLayoutBinding).card1.setOnClickListener(this)
                    binding.card1.setOnLongClickListener(this)
                    binding.tankItemCheckBox.setOnClickListener(this)
                    binding.editTankImageItemView.setOnClickListener(this)
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

                R.layout.tank_item_list_layout ->{

                    val tankpicUri: Uri = Uri.parse((item as TankItems).itemUri)
                    Glide.with(binding.root.context)
                            .load(tankpicUri)
                            .apply(RequestOptions()
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .placeholder(R.drawable.aquarium2)
                                    .error(R.drawable.aquarium2))
                            .into((binding as TankItemListLayoutBinding).tankItemImage)
                    binding.setVariable(BR.boundTankItems,item)

                }
            }

            binding.executePendingBindings()

        }

    }


    class ShowcaseDiffCallBack<T>(private val layout_ID : Int): DiffUtil.ItemCallback<T>() {


        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
           when (layout_ID){
               R.layout.showcase_recycler_view_item ->{
                   return (oldItem as GalleryInfo).userID == (newItem as GalleryInfo).userID
               }
               R.layout.tank_item_list_layout ->{
                   Timber.d("Item id check : ${(oldItem as TankItems).tag == (newItem as TankItems).tag}")
                   return (oldItem as TankItems).tag == (newItem as TankItems).tag

               }
           }

            return (oldItem as Plants).plantID == (newItem as Plants).plantID
        }

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            when (layout_ID){
                R.layout.showcase_recycler_view_item ->{
                    return (newItem as GalleryInfo) == (oldItem as GalleryInfo)
                }

                R.layout.tank_item_list_layout ->{
                    Timber.d("Item content check :old item-> ${(oldItem as TankItems).shown} and new item -> ${(newItem as TankItems).shown}")
                    return (oldItem as TankItems) == (newItem as TankItems)
                }
            }

            return (oldItem as Plants) == (newItem as Plants)
        }
        }

    }





