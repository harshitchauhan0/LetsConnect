package com.harshit.letsconnect.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.harshit.letsconnect.R
import com.harshit.letsconnect.adapters.GptAdapter
import com.harshit.letsconnect.databinding.FragmentRandomBinding
import com.harshit.letsconnect.extrasUtils.ExtraUtils
import com.harshit.letsconnect.models.GptMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject


class RandomFragment : Fragment() {

    private lateinit var binding:FragmentRandomBinding
    private lateinit var adapter: GptAdapter
    private lateinit var list: MutableList<GptMessage>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        list = mutableListOf()
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_random,container,false)
        adapter = GptAdapter(list)
        binding.chatRecyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.stackFromEnd = true
        binding.chatRecyclerView.layoutManager = layoutManager
        binding.messageSendBtn.setOnClickListener {
            val question = binding.chatMessageInput.text.toString()
            getResponse(question) { response ->
                addToChat(response,ExtraUtils.SENT_BY_ME)
            }
            binding.chatMessageInput.setText("")
        }
        return binding.root
    }

    fun addResponse(response: String?) {
        if(list.size >= 1 ) {
            list.removeAt(list.size - 1)
        }
        addToChat(response!!, ExtraUtils.SENT_BY_BOT)
    }


    fun getResponse(question: String, callback: (String) -> Unit){
        val url = ExtraUtils.LINK_CHAT_GPT
        val accessToken = ExtraUtils.API_KEY

        CoroutineScope(Dispatchers.Default).launch {
            val parametter = JSONObject()
            try {
                parametter.put("model", "text-davinci-003")
                parametter.put("prompt", question)
                parametter.put("max_tokens", 1000)
                parametter.put("temperature", 0)
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }

            val request = object : JsonObjectRequest(Method.POST, url, parametter,
                Response.Listener<JSONObject> { response ->
                    try {
                        val jsonArray = response.getJSONArray("choices")
                        val answer = jsonArray.getJSONObject(0).getString("text")
                        addResponse(answer)
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                },
                Response.ErrorListener { error ->
                    addResponse("Failed due to: ${error.toString()}")
                    Log.v("TAG", error.toString())
                }) {

                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = "Bearer $accessToken"
                    return headers
                }
            }

            val requestQueue = Volley.newRequestQueue(activity)
            requestQueue.add(request)
        }
    }

    private fun addToChat(question: String, sentByMe: String) {
        activity?.runOnUiThread {
            list.add(GptMessage(question,sentByMe))
            adapter.notifyDataSetChanged()
            binding.chatRecyclerView.smoothScrollToPosition(adapter.itemCount)
        }
    }

}