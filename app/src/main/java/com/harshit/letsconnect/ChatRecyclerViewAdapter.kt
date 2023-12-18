package com.harshit.letsconnect


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions


class ChatRecyclerViewAdapter(
    options: FirestoreRecyclerOptions<MessageModel?>,
    var context: Context
) :
    FirestoreRecyclerAdapter<MessageModel, ChatRecyclerViewAdapter.ChatModelViewHolder>(options) {
     override fun onBindViewHolder(
        holder: ChatModelViewHolder,
        position: Int,
        model: MessageModel
    ) {
        Log.i("haushd", "asjd")
        if (model.senderId.equals(FirebaseUtils.currentUserId())) {
            holder.leftChatLayout.visibility = View.GONE
            holder.rightChatLayout.visibility = View.VISIBLE
            holder.rightChatTextview.setText(model.message)
        } else {
            holder.rightChatLayout.visibility = View.GONE
            holder.leftChatLayout.visibility = View.VISIBLE
            holder.leftChatTextview.setText(model.message)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatModelViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.message_chat, parent, false)
        return ChatModelViewHolder(view)
    }

    inner class ChatModelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
}