package com.example.teamtotest.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.teamtotest.R
import com.example.teamtotest.activity.ProgressSettingActivity
import com.example.teamtotest.dto.ProgressDTO
import com.example.teamtotest.dto.ProjectDTO
import kotlinx.android.synthetic.main.item_progress_bar.view.*
import java.text.SimpleDateFormat
import java.util.*

class ProgressbarAdapterMain(private val context: Context,
                             private var projectDTOList: ArrayList<ProjectDTO?>,
                             private var myProjectPIDlist: ArrayList<String>):
    RecyclerView.Adapter<ViewHolderHelper>() {

    val dateFormat = SimpleDateFormat("yyyyMMddHHmmss")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHelper {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_progress_bar, parent, false)
        return ViewHolderHelper(view)
    }

    override fun getItemCount(): Int {
        return projectDTOList.size
    }

    override fun onBindViewHolder(holder: ViewHolderHelper, position: Int) {

        if(projectDTOList[position]!!.progressData==null) { // 시작일과 마감일 데이터가 없을 때

            holder.itemView.item_progress_bar.progress = 0
            holder.itemView.item_progress_percent.text = "(No data)"
            holder.itemView.item_progress_project_name.text = projectDTOList[position]!!.projectName

        }else{ // 시작일과 마감일 데이터가 있을 때
            Log.d("ProgressBar---->", projectDTOList[position]!!.progressData.toString())
            val progressData : ProgressDTO = projectDTOList[position]!!.progressData!!
            val start_day = dateFormat.parse(progressData.startDate)
            val end_day = dateFormat.parse(progressData.endDate)


            val diffTime : Long = (end_day.time - start_day.time) //ms -> day로 변환
            val today : Date = Date()
            val progressDay : Long = (end_day.time - today.time)
            val progressPercent : Int = (progressDay*100 / diffTime).toInt()
            Log.d("ProgressBar2-1---->", diffTime.toString())
            Log.d("ProgressBar2-2---->", progressDay.toString())
            Log.d("ProgressBar2-3---->", progressPercent.toString())

            holder.itemView.item_progress_bar.progress = progressPercent
            holder.itemView.item_progress_percent.text = "($progressPercent %)"
            holder.itemView.item_progress_project_name.text = projectDTOList[position]!!.projectName
        }
        holder.itemView.setOnClickListener{
            val intent : Intent = Intent(context, ProgressSettingActivity::class.java)
            intent.putExtra("PID", myProjectPIDlist[position])
            context.startActivity(intent)
        }
    }
}

