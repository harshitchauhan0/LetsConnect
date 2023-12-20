package com.harshit.letsconnect.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.harshit.letsconnect.R
import com.harshit.letsconnect.extrasUtils.ExtraUtils
import com.harshit.letsconnect.models.GptMessage


class GptAdapter(private val chats:MutableList<GptMessage>) : RecyclerView.Adapter<GptAdapter.MyViewHolder>() {
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var leftChatLayout: LinearLayout
        var rightChatLayout: LinearLayout
        var leftChatTextview: TextView
        var rightChatTextview: TextView

        init {
            leftChatLayout = itemView.findViewById(R.id.left_chat_layout)
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout)
            leftChatTextview = itemView.findViewById(R.id.left_chat_textview)
            rightChatTextview = itemView.findViewById(R.id.right_chat_textview)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val chatView = LayoutInflater.from(parent.context).inflate(R.layout.message_chat,parent,false)
        return MyViewHolder(chatView)
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val message: GptMessage = chats[position]
        if (message.sentBy == ExtraUtils.SENT_BY_ME) {
            holder.leftChatLayout.visibility = View.GONE
            holder.rightChatLayout.visibility = View.VISIBLE
            holder.rightChatTextview.text = message.message
        } else {
            holder.rightChatLayout.visibility = View.GONE
            holder.leftChatLayout.visibility = View.VISIBLE
            holder.leftChatTextview.text = message.message
        }
    }
}