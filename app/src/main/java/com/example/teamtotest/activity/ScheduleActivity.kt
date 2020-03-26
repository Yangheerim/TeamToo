package com.example.teamtotest.activity

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
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_schedule.*
import kotlinx.android.synthetic.main.fragment_calendarview.*

class ScheduleActivity : AppCompatActivity() {
    private lateinit var scheduleRecyclerViewAdapter: RVAdapter
    private var scheduleList = arrayListOf<ScheduleDTO>()
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private var PID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        //상단바
        setSupportActionBar(schedule_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //PID에 해당하는 스케줄 가져오기
        PID = intent.getStringExtra("PID")
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("ProjectList").child(PID.toString()).child("scheduleList")

        val dbScheduleEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                scheduleList.clear()
                for (data in dataSnapshot.children){
                    val scheduleDTO = data.getValue(ScheduleDTO::class.java)
                    scheduleDTO?.let { scheduleList.add(it) }
                }
                scheduleRecyclerViewAdapter.setData(scheduleList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", databaseError.toString())
            }
        }
        databaseReference.addValueEventListener(dbScheduleEventListener)

        scheduleRecyclerViewAdapter = RVAdapter(this)
        refreshCurrentMonth()
        rv_schedule.adapter = scheduleRecyclerViewAdapter   //adapter 설정
        rv_schedule.layoutManager = GridLayoutManager(this, BaseCalendar.DAYS_OF_WEEK)  //LayoutManager 설정

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
            startActivity(Intent(this, AddScheduleActivity::class.java).putExtra("PID",PID))
        }

    }

    private fun refreshCurrentMonth() {
        tv_current_month.text = scheduleRecyclerViewAdapter.currentMonth
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

}
