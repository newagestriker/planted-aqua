package com.newage.aquapets.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.newage.aquapets.R
import com.newage.aquapets.activities.SingleChatActivity
import com.newage.aquapets.room.ChatUsers
import java.util.*

class ChatUserRecyclerAdapter(private val arrayList: ArrayList<ChatUsers>, var context: Context) : RecyclerView.Adapter<ChatUserRecyclerAdapter.RecyclerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.each_chat_user_layout, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.chatUserName.text = arrayList[position].chatUserName
        Glide
                .with(context)
                .load(arrayList[position].chatUserPhotoURL)
                .apply(RequestOptions()
                        .error(R.drawable.profle))
                .into(holder.chatUserImageView)

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    inner class RecyclerViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view),View.OnClickListener {
        var chatUserImageView : ImageView = view.findViewById(R.id.chat_user_pic)
        var chatUserName: TextView = view.findViewById(R.id.chat_user_name)
        var chatUserMainLayout : ConstraintLayout = view.findViewById(R.id.chat_user_main)

        init {
            chatUserMainLayout.setOnClickListener(this)
        }

        override fun onClick(v: View?) {

            val intent = Intent(context,SingleChatActivity::class.java)
            intent.putExtra("from_user",arrayList[layoutPosition].chatUserID)
            intent.putExtra("carrier","user")
            context.startActivity(intent)

        }
    }

}