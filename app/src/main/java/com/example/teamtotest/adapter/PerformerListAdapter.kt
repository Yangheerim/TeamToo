package com.example.teamtotest.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.teamtotest.R
import com.example.teamtotest.dto.UserDTO
import kotlinx.android.synthetic.main.item_select_performer.view.*

//class FinalTestMemberListAdapter(private val MemberNameList: ArrayList<String>, context : Context)
class PerformerListAdapter(private val memberList: ArrayList<UserDTO>, private val prePerformerUIDList: ArrayList<String>, private val uidList : ArrayList<String>)
    : RecyclerView.Adapter<PerformerListAdapter.MyViewHolder>() {

    private var performerPositionList : ArrayList<Int> =  ArrayList<Int>()

    inner class MyViewHolder(v: View) :
        RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_select_performer,
                parent,
                false
            ) as LinearLayout  // 뷰 안에 특정 부분을 바꾸는거여서 inflate를 씀!

        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.item_performer_memberName.text = memberList[position].name
        holder.itemView.item_performer_checked.visibility = View.INVISIBLE
        holder.itemView.setBackgroundResource(R.drawable.button3)
        Log.e("uid",prePerformerUIDList.toString())

        if (uidList[position] in prePerformerUIDList){    // 이미 수행자인 사람 표시해주기
            holder.itemView.item_performer_checked.visibility = View.VISIBLE
            holder.itemView.setBackgroundResource(R.drawable.button4)
            performerPositionList.add(position)
        }

        holder.itemView.setOnClickListener{
            if(holder.itemView.item_performer_checked.visibility == View.INVISIBLE){    // 체크 안되어 있을 때
                holder.itemView.item_performer_checked.visibility = View.VISIBLE
                holder.itemView.setBackgroundResource(R.drawable.button4)
                performerPositionList.add(position)
            }else{  // 체크 되어있을 때
                holder.itemView.item_performer_checked.visibility = View.INVISIBLE
                holder.itemView.setBackgroundResource(R.drawable.button3)
                performerPositionList.remove(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return memberList.size
    }

    public fun getPerformerPositionList() : ArrayList<Int>{
        Log.e("getPerformer", performerPositionList.toString())
        return performerPositionList
    }


}