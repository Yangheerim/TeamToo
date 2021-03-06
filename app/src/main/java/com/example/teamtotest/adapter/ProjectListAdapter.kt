package com.example.teamtotest.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.teamtotest.R
import com.example.teamtotest.activity.ChatActivity
import kotlinx.android.synthetic.main.item_project_list.view.*

class ProjectListAdapter(
    private val projectInfoList: ArrayList<HashMap<String, String>>,
    private val context: Context?
) : RecyclerView.Adapter<ProjectListAdapter.MyViewHolder>() {
    var PID: String? = null

    inner class MyViewHolder(v: View) :
        RecyclerView.ViewHolder(v)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_project_list,
                parent,
                false
            ) as LinearLayout  // 뷰 안에 특정 부분을 바꾸는거여서 inflate를 씀!

        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.project_name.text = projectInfoList[position]["projectName"]
        holder.itemView.how_many_members.text = projectInfoList[position]["howManyMembers"]
        holder.itemView.last_message.text = projectInfoList[position]["lastMessage"]
        holder.itemView.last_message_sent_time.text =
            projectInfoList[position]["lastMessageSentTime"]
        if(projectInfoList[position]["noReadMessageCount"]=="0") {
            holder.itemView.noReadMessageCount.visibility = View.INVISIBLE
        }else{
            holder.itemView.noReadMessageCount.text = projectInfoList[position]["noReadMessageCount"]
            holder.itemView.noReadMessageCount.visibility = View.VISIBLE
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("PID", projectInfoList[position]["PID"])
            intent.putExtra("projectName", projectInfoList[position]["projectName"])
            intent.putExtra("howManyMembers", projectInfoList[position]["howManyMembers"])
            context!!.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return projectInfoList.size
    }

}