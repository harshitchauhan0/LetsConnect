package com.harshit.letsconnect

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.harshit.letsconnect.adapters.ChatRecyclerViewAdapter
import com.harshit.letsconnect.databinding.ActivityChatBinding
import com.harshit.letsconnect.extrasUtils.ExtraUtils
import com.harshit.letsconnect.models.ChatroomModel
import com.harshit.letsconnect.models.MessageModel
import com.harshit.letsconnect.models.UserModel
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.Date


class ChatActivity : AppCompatActivity() {
    private lateinit var binding:ActivityChatBinding
    private lateinit var userModel: UserModel
    private lateinit var database:FirebaseFirestore
    private lateinit var chatroomId:String
    private lateinit var adapter: ChatRecyclerViewAdapter
    private lateinit var auth: FirebaseAuth
    private var chatroomModel: ChatroomModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_chat)
        database = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        binding.backBtn.setOnClickListener { onBackPressed() }
        val profilePick:CircleImageView = findViewById(R.id.profile_image)
        val date: Date? = intent.getSerializableExtra(ExtraUtils.TIME) as? Date
        userModel =  UserModel(intent.getStringExtra(ExtraUtils.PHONE)!!,intent.getStringExtra(ExtraUtils.NAME)!!,Timestamp(date!!),intent.getStringExtra(ExtraUtils.UID)!!)
        userModel.setToken(intent.getStringExtra(ExtraUtils.TOKEN)!!)
        binding.otherUsername.text = userModel.getUsername()
        chatroomId = getChatRoomId(auth.currentUser?.uid.toString(),userModel.getUserId())
        ExtraUtils.getOtherImage(userModel.getUserId()).downloadUrl.addOnCompleteListener { task->
            if(task.isSuccessful){
                val uri = task.result
                Glide.with(applicationContext).load(uri).apply(RequestOptions.circleCropTransform()).into(profilePick)
            }
        }

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
        chatroomModel?.lastMessage = message
        database.collection(ExtraUtils.CHATROOM).document(chatroomId).set(chatroomModel as ChatroomModel)

        val messageModel = MessageModel(message,auth.currentUser!!.uid, Timestamp.now())
        database.collection(ExtraUtils.CHATROOM).document(chatroomId).collection(ExtraUtils.CHATS).add(messageModel).addOnCompleteListener {
            if(it.isSuccessful){
                binding.chatMessageInput.setText("")
                sendNotification(message)
            }
            else{
                Log.v("TAG",it.exception.toString())
            }
        }
    }

    private fun sendNotification(message: String) {
        database.collection(ExtraUtils.USERS).document(auth.currentUser!!.uid).get().addOnCompleteListener {
            if(it.isSuccessful){
                val model = it.result.toObject(UserModel::class.java)
                if(model!=null){
                    try {
                        val jsonObject = JSONObject()
                        val notificationObj = JSONObject()
                        notificationObj.put("title", model.getUsername())
                        notificationObj.put("body", message)
                        val dataObj = JSONObject()
                        dataObj.put("userId", model.getUserId())
                        jsonObject.put("notification", notificationObj)
                        jsonObject.put("data", dataObj)
                        jsonObject.put("to", userModel.getToken())
                        callApi(jsonObject)
                    } catch (e: Exception) {
                        Log.v("TAG",e.toString())
                    }
                }
            }
        }
    }

    private fun callApi(jsonObject:JSONObject){
        val json: MediaType = "application/json; charset=utf-8".toMediaType()
        val client = OkHttpClient()
        val url = ExtraUtils.URL_NOTIFICATION
        val body: RequestBody = jsonObject.toString().toRequestBody(json)
        val request: Request = Request.Builder()
            .url(url)
            .post(body)
            .header("Authorization", "Bearer ${ExtraUtils.NOTIFICATION_API_KEY}")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.v("TAG",e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
            }
        })


    }


    private fun getCreateChatRoomModel() {

        database.collection(ExtraUtils.CHATROOM).document(chatroomId).get().addOnCompleteListener {
            if(it.isSuccessful){
                chatroomModel = it.result.toObject(ChatroomModel::class.java)
                if(chatroomModel == null){
                    chatroomModel = ChatroomModel(chatroomId, mutableListOf(auth.currentUser?.uid.toString(),userModel.getUserId()),Timestamp.now(),"")
                    database.collection(ExtraUtils.CHATROOM).document(chatroomId).set(chatroomModel as ChatroomModel)
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