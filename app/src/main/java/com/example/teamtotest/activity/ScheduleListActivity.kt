package com.example.teamtotest.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.teamtotest.R
import com.example.teamtotest.adapter.ScheduleListRVAdapter
import com.example.teamtotest.dto.ScheduleDTO
import kotlinx.android.synthetic.main.activity_schedule.*
import kotlinx.android.synthetic.main.activity_schedule_list.*

class ScheduleListActivity : AppCompatActivity() {
    var scheduleList: ArrayList<ScheduleDTO>? = null
    var PID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_list)

        //상단바
        setSupportActionBar(add_schedule_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    //상단바
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()

        scheduleList = intent.getParcelableArrayListExtra("schedule list")
        PID = intent.getStringExtra("PID")

        //어댑터, 레이아웃매니저 설정
        rv_schedule_list.adapter = ScheduleListRVAdapter(scheduleList!!,this, PID)
        rv_schedule_list.layoutManager = LinearLayoutManager(this)
    }
}
