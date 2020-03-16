package com.example.teamtotest.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.teamtotest.BaseCalendar
import com.example.teamtotest.R
import com.example.teamtotest.adapter.RVAdapter
import com.example.teamtotest.dto.ScheduleDTO
import kotlinx.android.synthetic.main.activity_schedule.*
import kotlinx.android.synthetic.main.activity_schedule_list.*
import kotlinx.android.synthetic.main.fragment_calendarview.*

class ScheduleActivity : AppCompatActivity() {
    private lateinit var scheduleRecyclerViewAdapter: RVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        //상단바
        setSupportActionBar(schedule_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        scheduleRecyclerViewAdapter = RVAdapter(this)
        refreshCurrentMonth()
        rv_schedule.adapter = scheduleRecyclerViewAdapter   //adapter 설정

        rv_schedule.layoutManager =
            GridLayoutManager(this,
                BaseCalendar.DAYS_OF_WEEK
            ) as RecyclerView.LayoutManager? //LayoutManager 설정

        // < 버튼 눌렀을 때
        tv_prev_month.setOnClickListener {
            scheduleRecyclerViewAdapter.changeToPrevMonth()
            refreshCurrentMonth()
        }
        // > 버튼 눌렀을 때
        tv_next_month.setOnClickListener {
            scheduleRecyclerViewAdapter.changeToNextMonth()
            refreshCurrentMonth()
        }
        // + 버튼 눌렀을 때
        schedule_btn_add.setOnClickListener {
            startActivityForResult(Intent(this, AddScheduleActivity::class.java),100)
        }

    }

    private fun refreshCurrentMonth() {
        tv_current_month.text = scheduleRecyclerViewAdapter.currentMonth
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //어댑터로 dto 보내기
        if(resultCode == Activity.RESULT_OK && data != null){
            var dto = data.getParcelableExtra<ScheduleDTO>("schedule")

            scheduleRecyclerViewAdapter.setData(dto)
        }
        else{

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
