package com.example.teamtotest.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.teamtotest.R
import com.example.teamtotest.dto.ScheduleDTO
import kotlinx.android.synthetic.main.item_schedule.view.*
import java.util.*

class ScheduleRVAdapter(private val context: Context) : RecyclerView.Adapter<ViewHolderHelper>() {
    private val scheduleDTO = arrayListOf<ScheduleDTO>()
    private var today = Calendar.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHelper {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_schedule, parent, false)
        return ViewHolderHelper(view)
    }

    override fun getItemCount(): Int {
        return scheduleDTO.size
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolderHelper, position: Int) {
        val dto = scheduleDTO[position]

        //스케줄 달력에 표시해주기
        if (compareCalendarInRange(today, dto)) {
            if (isToday(dto)) { //스케줄 시작날짜인 경우
                holder.itemView.schedule_tv_name.text = scheduleDTO[position].name
                holder.itemView.schedule_tv_name.setTextColor(context.getColor(android.R.color.white))
            }
            holder.itemView.schedule_tv_name.setBackgroundColor(dto.color)

        } else {
            holder.itemView.schedule_tv_name.text = ""
            holder.itemView.schedule_tv_name.setBackgroundColor(context.getColor(android.R.color.white))
        }
    }

    private fun isToday(dto: ScheduleDTO): Boolean {
        val calStart = Calendar.getInstance().apply {
            time = Date(today.timeInMillis)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val calEnd = Calendar.getInstance().apply {
            time = Date(today.timeInMillis)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        val cal = Calendar.getInstance().apply {
            time = Date(dto.startTime)
        }

        return cal.after(calStart) && cal.before(calEnd)
    }

    //해당 날짜가 스케줄DTO 시작과 끝 사이에 있는지
    private fun compareCalendarInRange(base: Calendar, dto: ScheduleDTO): Boolean {
        val calStart = Calendar.getInstance().apply {
            time = Date(dto.startTime)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val calEnd = Calendar.getInstance().apply {
            time = Date(dto.endTime)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return base.after(calStart) && base.before(calEnd)
    }

    fun setList(calendar: Calendar, list: List<ScheduleDTO>) {
        today = calendar.clone() as Calendar
        scheduleDTO.clear()
        scheduleDTO.addAll(list)
        scheduleDTO.sort()
        notifyDataSetChanged()
    }
}