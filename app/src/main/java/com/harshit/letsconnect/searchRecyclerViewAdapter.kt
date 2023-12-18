package com.harshit.letsconnect

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView

class searchRecyclerViewAdapter(options: FirestoreRecyclerOptions<UserModel>,context: Context) : FirestoreRecyclerAdapter<UserModel,searchRecyclerViewAdapter.MyViewHolder>(options) {

    val context:Context
    init {
        this.context = context
    }
    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var circleImageView:CircleImageView
        var name:TextView
        var phoneNumber:TextView
        init {
            circleImageView = itemView.findViewById(R.id.profile_image)
            name = itemView.findViewById(R.id.user_name_text)
            phoneNumber = itemView.findViewById(R.id.phone_text)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.search_rv_item,parent,false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: UserModel) {

        holder.phoneNumber.text = model.getPhoneNumber()
        if(model.getUserId() == FirebaseAuth.getInstance().currentUser?.uid){
            holder.name.text = model.getUsername() + " (Me)"
        }
        else{
            holder.name.text = model.getUsername()
        }

        ExtraUtils.getOtherImage(model.getUserId()).downloadUrl.addOnCompleteListener {task->
            if(task.isSuccessful){
                val uri = task.result
                Glide.with(context).load(uri).apply(RequestOptions.circleCropTransform()).into(holder.circleImageView);
            }
        }

        holder.itemView.setOnClickListener {
//            Log.v("TAG",model.toString())
            val i = Intent(context,ChatActivity::class.java)
            i.putExtra("name",model.getUsername())
            i.putExtra("uid",model.getUserId())
            i.putExtra("phone",model.getPhoneNumber())
            i.putExtra("time",model.getTimestamp().toDate())
            i.putExtra("token",model.getToken())
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ContextCompat.startActivity(context,i,null)
        }
    }
}