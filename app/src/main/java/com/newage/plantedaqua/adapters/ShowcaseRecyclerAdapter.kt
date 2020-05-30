package com.newage.plantedaqua.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.newage.plantedaqua.R
import com.newage.plantedaqua.databinding.ShowcaseRecyclerViewItemBinding
import com.newage.plantedaqua.models.GalleryInfo

class ShowcaseRecyclerAdapter<T>(val items : ArrayList<T>, private val layout_ID : Int) : RecyclerView.Adapter<ShowcaseRecyclerAdapter.ShowcaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowcaseViewHolder {
        return ShowcaseViewHolder.from(parent,layout_ID)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ShowcaseViewHolder, position: Int) {
        holder.bind(items[position],layout_ID)
    }

    class ShowcaseViewHolder(private val binding: ShowcaseRecyclerViewItemBinding) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(parent: ViewGroup,layout_ID: Int) : ShowcaseViewHolder {
                var binding: Any? = null

                //Matching Bindings with Layout ID
                when(layout_ID) {
                    R.layout.showcase_recycler_view_item -> {
                        binding  = DataBindingUtil.inflate(LayoutInflater.from(parent.context), layout_ID, parent, false) as ShowcaseRecyclerViewItemBinding
                    }
                }
                return ShowcaseViewHolder(binding as ShowcaseRecyclerViewItemBinding)
            }
        }

        fun <T> bind(item:T,layout_ID: Int){

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
                            .into(binding.tankImageInShowcase)
                    binding.setVariable(BR.galleryInfo,item)
                }
            }

            binding.executePendingBindings()

        }

    }





}