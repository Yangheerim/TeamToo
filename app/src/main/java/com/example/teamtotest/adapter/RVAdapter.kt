package com.example.teamtotest.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.teamtotest.BaseCalendar
import com.example.teamtotest.R
import com.example.teamtotest.activity.ScheduleListActivity
import com.example.teamtotest.dto.ScheduleDTO
import kotlinx.android.synthetic.main.item_calendar.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RVAdapter(private val context: Context) : RecyclerView.Adapter<RVAdapter.ViewHolder>() {
    private val baseCalendar = BaseCalendar()   //캘린더 객체 만들기
    private val cal = Calendar.getInstance()
    var currentMonth: String = ""
    private var scheduleDTO = arrayListOf<ScheduleDTO>()

    init {
        baseCalendar.initBaseCalendar {
            refreshView(it) //함수를 parameter로 넘겨주는 것!!
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_calendar, parent, false)
        val adapter = ScheduleRVAdapter(context)
        return ViewHolder(view, adapter)
    }

    override fun getItemCount(): Int {
        return BaseCalendar.LOW_OF_CALENDAR * BaseCalendar.DAYS_OF_WEEK
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //일요일인 경우
        if (position % BaseCalendar.DAYS_OF_WEEK == 0) holder.itemView.tv_date.setTextColor(
            Color.parseColor("#ff1200")
        )
        else holder.itemView.tv_date.setTextColor(Color.parseColor("#676d6e"))

        //오늘 날짜 굵게 표시
        val fmt = SimpleDateFormat("yyyyMMdd", Locale.KOREA)
        if (fmt.format(cal.time) == fmt.format(baseCalendar.data[position].time)) {
            holder.itemView.tv_date.setTypeface(null, Typeface.BOLD)
        }

        if (position < baseCalendar.prevMonthTailOffset || position >= baseCalendar.prevMonthTailOffset + baseCalendar.currentMonthMaxDate) {
            holder.itemView.tv_date.alpha = 0.3f    //이전달이나 다음달인 경우 연하게
        } else {
            holder.itemView.tv_date.alpha = 1f  //이번달인 경우 진하게
        }
        holder.itemView.tv_date.text = baseCalendar.data[position].get(Calendar.DATE).toString()

        //일주일 간 정렬해주기 위한 일주일단위 스케줄 리스트
        val weekList = getWeekList(position)
        holder.adapter.setList(baseCalendar.data[position], weekList)   //그 날짜에 해당하는 Calendar 객체와 일주일단위 스케줄 리스트 넘김

        //리스트 보여주기 위한 하루단위 스케줄 리스트
        val dayList = getDayList(position)

        //날짜 클릭했을 때 스케줄 리스트 보여주기
        if (dayList.isNotEmpty()) {
            holder.itemView.view_over.setOnClickListener {
                val intent = Intent(context, ScheduleListActivity::class.java)
                intent.putExtra("schedule list", dayList)
                context.startActivity(intent)
            }
        }
    }

    private fun getWeekList(position: Int): ArrayList<ScheduleDTO> {
        return ArrayList(
            scheduleDTO.filter {
                compareCalendarInRange(baseCalendar.data[position], it)
            }
        )
    }

    private fun getDayList(position: Int): ArrayList<ScheduleDTO> {
        return ArrayList(
            scheduleDTO.filter {
                compareCalendarInRange2(baseCalendar.data[position], it)
            }
        )
    }

    fun changeToPrevMonth() {
        baseCalendar.changeToPrevMonth {
            refreshView(it)
        }
    }

    fun changeToNextMonth() {
        baseCalendar.changeToNextMonth {
            refreshView(it)
        }
    }

    //달력 다시 그리기
    private fun refreshView(calendar: Calendar) {
        notifyDataSetChanged()
        currentMonth = SimpleDateFormat("yyyy MM", Locale.KOREAN).format(calendar.time)
    }

    //달력에 사용될 data 설정
    fun setData(dto: ArrayList<ScheduleDTO>) {
        scheduleDTO = dto
        notifyDataSetChanged()
    }

    //그 주에 해당하는 스케줄인지
    private fun compareCalendarInRange(base: Calendar, dto: ScheduleDTO): Boolean {
        val calStart = Calendar.getInstance().apply {
            time = Date(dto.startTime)
            set(Calendar.DAY_OF_WEEK, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val calEnd = Calendar.getInstance().apply {
            time = Date(dto.endTime)
            set(Calendar.DAY_OF_WEEK, 7)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return base.after(calStart) && base.before(calEnd)
    }

    //그 날에 해당하는 스케줄인지
    private fun compareCalendarInRange2(base: Calendar, dto: ScheduleDTO): Boolean {
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


    inner class ViewHolder(view: View, val adapter: ScheduleRVAdapter) : RecyclerView.ViewHolder(view) {
        init {
            view.rv_schedule.adapter = adapter
        }
    }

}