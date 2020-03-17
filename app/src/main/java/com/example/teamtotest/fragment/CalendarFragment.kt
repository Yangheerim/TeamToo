package com.example.teamtotest.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.teamtotest.BaseCalendar
import com.example.teamtotest.R
import com.example.teamtotest.adapter.RVAdapter
import kotlinx.android.synthetic.main.fragment_calendarview.view.*

class CalendarFragment : Fragment() {
    private lateinit var calendarRVAdapter: RVAdapter
    private lateinit var mView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_calendar, container, false)
        initView()
        return mView
    }

    private fun initView() {
        calendarRVAdapter = RVAdapter(requireContext())
        refreshCurrentMonth()
        mView.rv_schedule.layoutManager = GridLayoutManager(context, BaseCalendar.DAYS_OF_WEEK) //LayoutManager 설정
        mView.rv_schedule.adapter = calendarRVAdapter //RecyclerViewAdapter 설정

        //이전달 클릭할 경우
        mView.tv_prev_month.setOnClickListener {
            calendarRVAdapter.changeToPrevMonth()
            refreshCurrentMonth()
        }
        //다음달 클릭할 경우
        mView.tv_next_month.setOnClickListener {
            calendarRVAdapter.changeToNextMonth()
            refreshCurrentMonth()
        }
    }

    //월 textview 바꾸기
    private fun refreshCurrentMonth() {
        mView.tv_current_month.text = calendarRVAdapter.currentMonth
    }
}