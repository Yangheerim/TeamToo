package com.example.teamtotest.adapter

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.teamtotest.FinalTestDialog
import com.example.teamtotest.R
import com.example.teamtotest.activity.FinalTestActivity
import kotlinx.android.synthetic.main.activity_final_test.*
import kotlinx.android.synthetic.main.item_final_test_member.view.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

//class FinalTestMemberListAdapter(private val MemberNameList: ArrayList<String>, context : Context)
class FinalTestMemberListAdapter(private val MemberNameList: ArrayList<String>, activity : FinalTestActivity, final_test_date_:String?)
    : RecyclerView.Adapter<FinalTestMemberListAdapter.MyViewHolder>() {

    val activity : FinalTestActivity = activity
    var final_test_date_ : String? = final_test_date_
    private val dateFormat = SimpleDateFormat("yyyyMMddHHmmss")

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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.item_final_test_member_name.text = MemberNameList[position]

        holder.itemView.setOnClickListener{
            val cal : Calendar = Calendar.getInstance()
            val current : Date = cal.time
            val formatted_current = dateFormat.format(current)
            val today = formatted_current.substring(0, 4) + "/" + formatted_current.substring(4, 6) + "/" + formatted_current.substring(6, 8)
            Log.d("Today --->", today)
            Log.d("final_test_date --->", final_test_date_)
            if(final_test_date_ != today) {
                Toast.makeText(activity, "평가 날짜가 오늘이 아닙니다", Toast.LENGTH_SHORT).show()
            }else {
                val finalTestDialog: FinalTestDialog =
                    FinalTestDialog(activity, MemberNameList[position], holder.itemView, position)
                finalTestDialog.callDialog()
            }
        }

    }

    override fun getItemCount(): Int {
        return MemberNameList.size
    }


}