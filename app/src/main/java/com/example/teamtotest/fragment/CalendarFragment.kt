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
import kotlinx.android.synthetic.main.fragment_calendar.view.*
import kotlinx.android.synthetic.main.fragment_calendarview.view.*

class CalendarFragment : Fragment() {
    private lateinit var scheduleRecyclerViewAdapter: RVAdapter
    private lateinit var mView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_calendar, container, false)
        initView()
        return mView
    }

    private fun initView() {
        scheduleRecyclerViewAdapter =
            RVAdapter(requireContext())
        refreshCurrentMonth()
        mView.calendar_view.rv_schedule.layoutManager =
            GridLayoutManager(context,
                BaseCalendar.DAYS_OF_WEEK
            ) //LayoutManager 설정
        mView.calendar_view.rv_schedule.adapter =
            scheduleRecyclerViewAdapter //RecyclerViewAdapter 설정
        /*
        mView.rv_schedule.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.HORIZONTAL
            )
        )
        */

        /*
        mView.rv_schedule.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        */

        mView.tv_prev_month.setOnClickListener {
            scheduleRecyclerViewAdapter.changeToPrevMonth() //이전달로 바꾸기
            refreshCurrentMonth()
        }

        mView.tv_next_month.setOnClickListener {
            scheduleRecyclerViewAdapter.changeToNextMonth() //다음달로 바꾸기
            refreshCurrentMonth()
        }
    }

    //월 문자열 바꾸기
    private fun refreshCurrentMonth() {
        mView.tv_current_month.text = scheduleRecyclerViewAdapter.currentMonth
    }
}