package com.example.teamtotest.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.teamtotest.R
import com.example.teamtotest.dto.ScheduleDTO
import kotlinx.android.synthetic.main.item_schedule_list.view.*

//해당 날짜를 눌렀을 때 스케줄을 리스트 형식으로 보여주는 어댑터

class ScheduleListRVAdapter(var scheduleList: ArrayList<ScheduleDTO>?) :
    RecyclerView.Adapter<ViewHolderHelper>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHelper {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_schedule_list, parent, false)
        return ViewHolderHelper(view)
    }

    override fun getItemCount(): Int {
        return scheduleList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolderHelper, position: Int) {
        holder.itemView.schedule_tv_list.text = scheduleList!![position].name
        holder.itemView.imageView.setColorFilter(scheduleList!![position].color)
    }

}