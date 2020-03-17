package com.example.teamtotest.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.teamtotest.BaseCalendar
import com.example.teamtotest.R
import com.example.teamtotest.adapter.RVAdapter
import com.example.teamtotest.dto.MembersDTO
import com.example.teamtotest.dto.ScheduleDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_calendarview.view.*

class CalendarFragment : Fragment() {
    private lateinit var calendarRVAdapter: RVAdapter
    private lateinit var mView: View
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userID:String
    private var pidList = arrayListOf<String>()
    private var scheduleList = arrayListOf<ScheduleDTO>()
    private lateinit var scheduleDTO: ScheduleDTO

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.fragment_calendar, container, false)
        initView()
        return mView
    }

    private fun initView() {
        //로그인한 사람의 uid 가져오기
        userID = firebaseAuth.currentUser!!.uid

        //유저가 속한 프로젝트 리스트 가져오기
        findProjectList(userID)

        //스케줄 리스트 가져오고 어댑터에 데이터 보내기
        findScheduleList()

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

    private fun findProjectList(uid : String){
        databaseReference = firebaseDatabase.getReference("ProjectList")

        val dbScheduleEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                pidList.clear()
                for (data in dataSnapshot.children) {   //PID마다 훑기
                    val membersDTO = data.child("members").getValue(MembersDTO::class.java)!!
                    if (uid in membersDTO.UID_list!!) {  //프로젝트에 속해있을 경우
                        pidList.add(data.key.toString())    //프로젝트 id를 리스트에 넣음
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("CalendarFragment", databaseError.toString())
            }
        }
        databaseReference.addValueEventListener(dbScheduleEventListener)
    }

    private fun findScheduleList(){
        databaseReference = firebaseDatabase.getReference("ProjectList")

        val dbScheduleEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                scheduleList.clear()
                for (data in dataSnapshot.children){
                    if(data.key.toString() in pidList){
                        data.child("scheduleList").children.forEach {
                             scheduleDTO = it.getValue(ScheduleDTO::class.java)!!
                             scheduleList.add(scheduleDTO)   //스케줄 리스트에 추가
                        }
                    }
                }
                calendarRVAdapter.setData(scheduleList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", databaseError.toString())
            }
        }
        databaseReference.addValueEventListener(dbScheduleEventListener)
    }
}