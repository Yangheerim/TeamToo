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
import kotlinx.android.synthetic.main.item_chat_noti1.view.*
import kotlinx.android.synthetic.main.item_chat_noti2.view.*
import kotlinx.android.synthetic.main.item_chat_notification.view.*
import java.text.SimpleDateFormat
import java.util.*


class ChatListAdapter(var ChatMessage : ArrayList<HashMap<String,String>>) //MyAdapter의 constructor
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

        if(ChatMessageList!![position]["who"]=="")
            return 3;
        if(ChatMessageList!![position]["todoName"]!=null && firebaseAuth.currentUser!!.uid != userUID)
            return 4;
        if(ChatMessageList!![position]["todoName"]!=null && firebaseAuth.currentUser!!.uid == userUID)
            return 5;
        if(ChatMessageList!![position]["scheduleName"]!=null && firebaseAuth.currentUser!!.uid != userUID)
            return 6;
        if(ChatMessageList!![position]["scheduleName"]!=null && firebaseAuth.currentUser!!.uid == userUID)
            return 7;
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
        } else if (viewType == 3) {
            layoutId = R.layout.item_chat_notification
        } else if (viewType == 4) {  // 남일때
            layoutId = R.layout.item_chat_noti1
        } else if (viewType == 5) {
            layoutId = R.layout.item_chat_noti2
        } else if (viewType == 6) {  // 남일때
            layoutId = R.layout.item_chat_noti1
        } else if (viewType == 7) {
            layoutId = R.layout.item_chat_noti2
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
            val tmpDate :String = ChatMessageList!![position]["date"]!!
            holder.itemView.message_sent_time1.text = tmpDate.substring(8, 10)+":"+ tmpDate.substring(10, 12)
            holder.itemView.member_name1.text = ChatMessageList!!.get(position)["who"]
            holder.itemView.message1.text = ChatMessageList!!.get(position)["message"]
            if(ChatMessageList!!.get(position)["isRead"]=="0") {
                holder.itemView.chat_list_format_isRead.visibility = View.INVISIBLE
            }else{
                holder.itemView.chat_list_format_isRead.text = ChatMessageList!!.get(position)["isRead"]
                holder.itemView.chat_list_format_isRead.visibility = View.VISIBLE
            }
        } else if (viewType == 2) {  // 나일때
            val tmpDate :String = ChatMessageList!![position]["date"]!!
            holder.itemView.message_sent_time2.text = tmpDate.substring(8, 10)+":"+ tmpDate.substring(10, 12)
            holder.itemView.member_name2.text = ChatMessageList!!.get(position)["who"]
            holder.itemView.message2.text = ChatMessageList!!.get(position)["message"]
            if(ChatMessageList!!.get(position)["isRead"]=="0") {
                holder.itemView.chat_list_format2_isRead.visibility = View.INVISIBLE
            }else{
                holder.itemView.chat_list_format2_isRead.text = ChatMessageList!!.get(position)["isRead"]
                holder.itemView.chat_list_format2_isRead.visibility = View.VISIBLE
            }
        } else if (viewType == 3) { // 들어왔을때, 나갔을때 알림메세지
            holder.itemView.item_notify.text = ChatMessageList!!.get(position)["message"]
        }else if (viewType == 4) {  // todo
            val tmpDate :String = ChatMessageList!![position]["date"]!!
            holder.itemView.noti1_message.text = ChatMessageList!!.get(position)["who"].toString()+"님이 할일을 추가했습니다."
            holder.itemView.noti1_message_sent_time.text = tmpDate.substring(8, 10)+":"+ tmpDate.substring(10, 12)
            holder.itemView.noti1_member_name.text = ChatMessageList!!.get(position)["who"]

            holder.itemView.noti1_todoName.text = ChatMessageList!!.get(position)["todoName"]
            holder.itemView.noti1_deadline.text = ChatMessageList!!.get(position)["deadline"]
            holder.itemView.noti1_performer.text = ChatMessageList!!.get(position)["performer"]

            if(ChatMessageList!!.get(position)["isRead"]=="0") {
                holder.itemView.noti1_isRead.visibility = View.INVISIBLE
            }else{
                holder.itemView.noti1_isRead.text = ChatMessageList!!.get(position)["isRead"]
                holder.itemView.noti1_isRead.visibility = View.VISIBLE
            }
        } else if (viewType == 5) {  // todo
            val tmpDate :String = ChatMessageList!![position]["date"]!!
            holder.itemView.noti2_message.text = ChatMessageList!!.get(position)["who"].toString()+"님이 할일을 추가했습니다."
            holder.itemView.noti2_message_sent_time.text = tmpDate.substring(8, 10)+":"+ tmpDate.substring(10, 12)
            holder.itemView.noti2_member_name.text = ChatMessageList!!.get(position)["who"]

            holder.itemView.noti2_todoName.text = ChatMessageList!!.get(position)["todoName"]
            holder.itemView.noti2_deadline.text = ChatMessageList!!.get(position)["deadline"]
            holder.itemView.noti2_performer.text = ChatMessageList!!.get(position)["performer"]

            if(ChatMessageList!!.get(position)["isRead"]=="0") {
                holder.itemView.noti2_isRead.visibility = View.INVISIBLE
            }else{
                holder.itemView.noti2_isRead.text = ChatMessageList!!.get(position)["isRead"]
                holder.itemView.noti2_isRead.visibility = View.VISIBLE
            }

        }else if (viewType == 6) {  // schedule
            val tmpDate :String = ChatMessageList!![position]["date"]!!
            holder.itemView.noti1_message.text = ChatMessageList!!.get(position)["who"].toString()+"님이 스케줄을 추가했습니다."
            holder.itemView.noti1_message_sent_time.text = tmpDate.substring(8, 10)+":"+ tmpDate.substring(10, 12)
            holder.itemView.noti1_member_name.text = ChatMessageList!!.get(position)["who"]

            holder.itemView.noti1_todoName.text = ChatMessageList!!.get(position)["scheduleName"]
            holder.itemView.noti1_deadline.text = ChatMessageList!!.get(position)["startDate"]
            holder.itemView.noti1_performer.text = ChatMessageList!!.get(position)["endDate"]

            if(ChatMessageList!!.get(position)["isRead"]=="0") {
                holder.itemView.noti1_isRead.visibility = View.INVISIBLE
            }else{
                holder.itemView.noti1_isRead.text = ChatMessageList!!.get(position)["isRead"]
                holder.itemView.noti1_isRead.visibility = View.VISIBLE
            }

        }else if (viewType == 7) {  // schedule
            val tmpDate :String = ChatMessageList!![position]["date"]!!
            holder.itemView.noti2_message.text = ChatMessageList!!.get(position)["who"].toString()+"님이 스케줄을 추가했습니다."
            holder.itemView.noti2_message_sent_time.text = tmpDate.substring(8, 10)+":"+ tmpDate.substring(10, 12)
            holder.itemView.noti2_member_name.text = ChatMessageList!!.get(position)["who"]

            holder.itemView.noti2_todoName.text = ChatMessageList!!.get(position)["scheduleName"]
            holder.itemView.noti2_deadline.text = "시작 시간 : "+ ChatMessageList!!.get(position)["startDate"]
            holder.itemView.noti2_performer.text = "종료 시간 : "+ ChatMessageList!!.get(position)["endDate"]

            if(ChatMessageList!!.get(position)["isRead"]=="0") {
                holder.itemView.noti2_isRead.visibility = View.INVISIBLE
            }else{
                holder.itemView.noti2_isRead.text = ChatMessageList!!.get(position)["isRead"]
                holder.itemView.noti2_isRead.visibility = View.VISIBLE
            }

        }else {
            Log.d("View type 오류 : ", viewType.toString() + "")
        }
    }


    override fun getItemCount(): Int {
        return ChatMessageList!!.size // Return the size of your dataset (invoked by the layout manager)
    }
}