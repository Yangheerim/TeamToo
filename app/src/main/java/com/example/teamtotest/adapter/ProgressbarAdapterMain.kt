package com.example.teamtotest.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.teamtotest.R
import com.example.teamtotest.dto.ProjectDTO
import com.example.teamtotest.dto.TodoDTO
import com.example.teamtotest.dto.UserDTO
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.item_progress_bar.view.*
import kotlinx.android.synthetic.main.item_todo.view.todo_tv_d_day
import kotlinx.android.synthetic.main.item_todo.view.todo_tv_name
import kotlinx.android.synthetic.main.item_todo_dashboard.view.*
import java.util.*
import kotlin.math.absoluteValue

class ProgressbarAdapterMain(private val context: Context, private var projectDTOList: ArrayList<ProjectDTO?>) :
    RecyclerView.Adapter<ViewHolderHelper>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHelper {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_progress_bar, parent, false)
        return ViewHolderHelper(view)
    }

    override fun getItemCount(): Int {
        return projectDTOList.size
    }

    override fun onBindViewHolder(holder: ViewHolderHelper, position: Int) {

        if(projectDTOList[position]!!.startDate==null) { // 시작일과 마감일 데이터가 없을 때
            holder.itemView.progressbar.labelText =projectDTOList[position]!!.projectName + " (데이터가 없습니다.)"
            holder.itemView.progressbar.progress = 0.0F
        }else{// 시작일과 마감일 데이터가 있을 때

            val diffTime : Long = (projectDTOList[position]!!.endDate!!.time - projectDTOList[position]!!.startDate!!.time) / (24*60*60*1000) //ms -> day로 변환
            val today : Date = Date()
            val progressDay : Long = (projectDTOList[position]!!.endDate!!.time - today.time) / (24*60*60*1000)
            val progressPercent : Float = (progressDay / diffTime).toFloat()

            holder.itemView.progressbar.labelText =projectDTOList[position]!!.projectName + " ($progressPercent%)"
            holder.itemView.progressbar.progress = progressPercent
        }
    }
}