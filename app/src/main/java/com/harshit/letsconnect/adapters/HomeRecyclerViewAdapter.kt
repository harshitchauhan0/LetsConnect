package com.harshit.letsconnect.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.harshit.letsconnect.ChatActivity
import com.harshit.letsconnect.models.ChatroomModel
import com.harshit.letsconnect.extrasUtils.ExtraUtils
import com.harshit.letsconnect.R
import com.harshit.letsconnect.models.UserModel
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat

class HomeRecyclerViewAdapter(options: FirestoreRecyclerOptions<ChatroomModel>, context: Context) : FirestoreRecyclerAdapter<ChatroomModel, HomeRecyclerViewAdapter.MyViewHolder>(options) {

    val context:Context
    init {
        this.context = context
    }
    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var circleImageView:CircleImageView
        var name:TextView
        var lastMessage:TextView
        var lastMessageTime:TextView
        init {
            circleImageView = itemView.findViewById(R.id.profile_image)
            name = itemView.findViewById(R.id.chat_name)
            lastMessage = itemView.findViewById(R.id.chat_last_message)
            lastMessageTime = itemView.findViewById(R.id.chat_time)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.main_chat_row,parent,false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: ChatroomModel) {
        ExtraUtils.getOtherUserFromChatroom(model.userIds).get().addOnCompleteListener {
            if(it.isSuccessful){
                val userModel = it.result.toObject(UserModel::class.java)

                if (userModel != null) {
                    ExtraUtils.getOtherImage(userModel.getUserId()).downloadUrl.addOnCompleteListener { task->
                        if(task.isSuccessful){
                            val uri = task.result
                            Glide.with(context).load(uri).apply(RequestOptions.circleCropTransform()).into(holder.circleImageView);
                        }
                    }
                }


                holder.name.text = userModel?.getUsername()
                holder.lastMessage.text = if(model.lastMessageSenderId == ExtraUtils.currentUserId()){
                    "You: "+model.lastMessage
                }
                else{
                    model.lastMessage
                }
                holder.lastMessageTime.text = SimpleDateFormat("HH:MM").format(model.lastMessageTimestamp!!.toDate())

                holder.itemView.setOnClickListener {
                    val i = Intent(context, ChatActivity::class.java)
                    i.putExtra("name",userModel?.getUsername())
                    i.putExtra("uid",userModel?.getUserId())
                    i.putExtra("phone",userModel?.getPhoneNumber())
                    i.putExtra("time",userModel?.getTimestamp()?.toDate())
                    i.putExtra("token",userModel?.getToken())
                    ContextCompat.startActivity(context,i,null)
                }
            }
            else{
                Log.v("TAG",it.exception.toString())
            }
        }
    }
}