package com.example.teamtotest.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.teamtotest.R
import kotlinx.android.synthetic.main.item_select_performer.view.*

//class FinalTestMemberListAdapter(private val MemberNameList: ArrayList<String>, context : Context)
class PerformerListAdapter(private val MemberNameList: ArrayList<String>)
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
        holder.itemView.item_performer_memberName.text = MemberNameList[position]
        holder.itemView.item_performer_checked.visibility = View.INVISIBLE
        holder.itemView.setBackgroundResource(R.drawable.button3)
        holder.itemView.setOnClickListener{
            if(holder.itemView.item_performer_checked.visibility == View.INVISIBLE){
                holder.itemView.item_performer_checked.visibility = View.VISIBLE
                holder.itemView.setBackgroundResource(R.drawable.button4)
                performerPositionList.add(position)
            }else{
                holder.itemView.item_performer_checked.visibility = View.INVISIBLE
                holder.itemView.setBackgroundResource(R.drawable.button3)
            }
        }
    }

    override fun getItemCount(): Int {
        return MemberNameList.size
    }

    public fun getPerformerPositionList() : ArrayList<Int>{
        return performerPositionList
    }


}