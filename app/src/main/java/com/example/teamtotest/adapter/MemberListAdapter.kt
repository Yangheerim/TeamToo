package com.example.teamtotest.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.teamtotest.R
import com.example.teamtotest.activity.AddProjectActivity
import kotlinx.android.synthetic.main.user_circle_format.view.*
import java.util.*

class MemberListAdapter(val mDataset: ArrayList<String>, activity : AddProjectActivity?)//MyAdapter의 constructor
    : RecyclerView.Adapter<MemberListAdapter.MyViewHolder>() {

    var myDataset: ArrayList<String>? = null
    var activity : AddProjectActivity? = null

    inner class MyViewHolder(v: View) :
        RecyclerView.ViewHolder(v)

    init {
        myDataset = mDataset
        this.activity = activity
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder { // create a new view
        val v = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.user_circle_format,
                parent,
                false
            ) as LinearLayout  // 뷰 안에 특정 부분을 바꾸는거여서 inflate를 씀!

        return MyViewHolder(v)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.member_name.text = mDataset[position]
        holder.itemView.member_name.setOnClickListener {
            // 삭제하는 애
            activity?.deleteMemberList(position)
        }
    }

    override fun getItemCount(): Int {
        return mDataset.size // Return the size of your dataset (invoked by the layout manager)
    }

}