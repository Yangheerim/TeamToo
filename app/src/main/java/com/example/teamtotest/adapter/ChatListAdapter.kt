package com.example.teamtotest.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.teamtotest.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.item_chat_list1.view.*
import kotlinx.android.synthetic.main.item_chat_list2.view.*
import java.util.*

class ChatListAdapter(var ChatMessage : ArrayList<HashMap<String,String>>)//MyAdapter의 constructor
    : RecyclerView.Adapter<ChatListAdapter.MyViewHolder>() {


    private var ChatMessageList: ArrayList<java.util.HashMap<String, String>>?=null
    private var firebaseAuth = FirebaseAuth.getInstance()

    inner class MyViewHolder(v: View) :
        RecyclerView.ViewHolder(v)

    init {
        this.ChatMessageList = ChatMessage
    }

    override fun getItemViewType(position: Int): Int {
        val userUID = ChatMessageList!![position]["userUID"]
        return if (firebaseAuth.currentUser!!.uid == userUID) { //** 이거 UID로 비교하는걸로 바꿔야함
            2
        } else {
            1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder { // create a new view

        var layoutId = 0

        if (viewType == 1) {    // 남일때
            layoutId = R.layout.item_chat_list1
        } else if (viewType == 2) {  // 나일때
            layoutId = R.layout.item_chat_list2
        } else {
            Log.d("View type 오류 : ", viewType.toString() + "")
        }

        val v = LayoutInflater.from(parent.context)
            .inflate(layoutId, parent, false) as LinearLayout  // 뷰 안에 특정 부분을 바꾸는거여서 inflate를 씀!

        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val viewType = getItemViewType(position)

        if (viewType == 1) {    // 남일때
            holder.itemView.message_sent_time1.text = ChatMessageList!!.get(position)["date"]
            holder.itemView.member_name1.text = ChatMessageList!!.get(position)["who"]
            holder.itemView.message1.text = ChatMessageList!!.get(position)["message"]
            if(ChatMessageList!!.get(position)["isRead"]=="0") {
                holder.itemView.chat_list_format_isRead.visibility = View.INVISIBLE
            }else{
                holder.itemView.chat_list_format_isRead.text = ChatMessageList!!.get(position)["isRead"]
                holder.itemView.chat_list_format_isRead.visibility = View.VISIBLE
            }
        } else if (viewType == 2) {  // 나일때
            holder.itemView.message_sent_time2.text = ChatMessageList!!.get(position)["date"]
            holder.itemView.member_name2.text = ChatMessageList!!.get(position)["who"]
            holder.itemView.message2.text = ChatMessageList!!.get(position)["message"]
            if(ChatMessageList!!.get(position)["isRead"]=="0") {
                holder.itemView.chat_list_format2_isRead.visibility = View.INVISIBLE
            }else{
                holder.itemView.chat_list_format2_isRead.text = ChatMessageList!!.get(position)["isRead"]
                holder.itemView.chat_list_format2_isRead.visibility = View.VISIBLE
            }
        } else {
            Log.d("View type 오류 : ", viewType.toString() + "")
        }
    }


    override fun getItemCount(): Int {
        return ChatMessageList!!.size // Return the size of your dataset (invoked by the layout manager)
    }
}