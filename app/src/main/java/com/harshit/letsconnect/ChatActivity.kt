package com.harshit.letsconnect

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.harshit.letsconnect.databinding.ActivityChatBinding
import java.util.Date

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;


class ChatActivity : AppCompatActivity() {
    private lateinit var binding:ActivityChatBinding
    private lateinit var userModel:UserModel
    private lateinit var database:FirebaseFirestore
    private lateinit var chatroomId:String
    private lateinit var adapter:ChatRecyclerViewAdapter
    private lateinit var auth: FirebaseAuth
    private var chatroomModel: ChatroomModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_chat)
        database = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        binding.backBtn.setOnClickListener { onBackPressed() }
        val date: Date? = intent.getSerializableExtra("time") as? Date
        userModel =  UserModel(intent.getStringExtra("phone")!!,intent.getStringExtra("name")!!,Timestamp(date!!),intent.getStringExtra("uid")!!)
        binding.otherUsername.text = userModel.getUsername()
        chatroomId = getChatRoomId(auth.currentUser?.uid.toString(),userModel.getUserId())
        getCreateChatRoomModel()

        binding.messageSendBtn.setOnClickListener {
            val message = binding.chatMessageInput.text.toString()
            if(!TextUtils.isEmpty(message)){
                sendMessage(message)
            }
        }

        recyclerViewConfiguration()
    }

    private fun recyclerViewConfiguration() {
        val query: Query = database.collection("chatroom").document(chatroomId).collection("chats")
            .orderBy("timestamp", Query.Direction.DESCENDING)

        val options = FirestoreRecyclerOptions.Builder<MessageModel>().setQuery(query, MessageModel::class.java).build()

        adapter = ChatRecyclerViewAdapter(options, applicationContext)
        val manager = LinearLayoutManager(this)
        manager.reverseLayout = true
        binding.chatRecyclerView.layoutManager = manager
        binding.chatRecyclerView.adapter = adapter
        adapter.startListening()
        // scrolling problem solution
        adapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                binding.chatRecyclerView.smoothScrollToPosition(0)
            }
        })
    }

    private fun sendMessage(message: String) {
        chatroomModel?.lastMessageSenderId = auth.currentUser!!.uid
        chatroomModel?.lastMessageTimestamp = Timestamp.now()
        database.collection("chatroom").document(chatroomId).set(chatroomModel as ChatroomModel)

        val messageModel = MessageModel(message,auth.currentUser!!.uid, Timestamp.now())
        database.collection("chatroom").document(chatroomId).collection("chats").add(messageModel).addOnCompleteListener {
            if(it.isSuccessful){
                binding.chatMessageInput.setText("")
            }
            else{
                Log.v("TAG",it.exception.toString())
            }
        }
    }

    private fun getCreateChatRoomModel() {

        database.collection("chatroom").document(chatroomId).get().addOnCompleteListener {
            if(it.isSuccessful){
                chatroomModel = it.result.toObject(ChatroomModel::class.java)
                if(chatroomModel == null){
                    chatroomModel = ChatroomModel(chatroomId, mutableListOf(auth.currentUser?.uid.toString(),userModel.getUserId()),Timestamp.now(),"")
                    database.collection("chatroom").document(chatroomId).set(chatroomModel as ChatroomModel)
                }
            }
            else{
                Log.v("TAG",it.exception.toString())
            }
        }
    }

    private fun getChatRoomId(user1:String,user2:String): String{
        return if(user1.hashCode()<user2.hashCode()){
            "$user1@$user2"
        } else{
            "$user2@$user1"
        }
    }
}