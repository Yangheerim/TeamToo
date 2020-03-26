package com.example.teamtotest.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.teamtotest.FinalTestDialog
import com.example.teamtotest.R
import com.example.teamtotest.activity.FinalTestActivity
import kotlinx.android.synthetic.main.item_final_test_member.view.*

//class FinalTestMemberListAdapter(private val MemberNameList: ArrayList<String>, context : Context)
class FinalTestMemberListAdapter(private val MemberNameList: ArrayList<String>, activity : FinalTestActivity)
    : RecyclerView.Adapter<FinalTestMemberListAdapter.MyViewHolder>() {

    val activity : FinalTestActivity = activity

    inner class MyViewHolder(v: View) :
        RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_final_test_member,
                parent,
                false
            ) as LinearLayout  // 뷰 안에 특정 부분을 바꾸는거여서 inflate를 씀!

        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.item_final_test_member_name.text = MemberNameList[position]
        holder.itemView.setOnClickListener{
            var finalTestDialog : FinalTestDialog = FinalTestDialog(activity, MemberNameList[position], holder.itemView, position)
            finalTestDialog.callDialog()
        }
    }

    override fun getItemCount(): Int {
        return MemberNameList.size
    }


}