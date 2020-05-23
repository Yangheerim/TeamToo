package com.example.teamtotest.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.teamtotest.R
import com.example.teamtotest.dto.ProgressDTO
import com.example.teamtotest.dto.ProjectDTO
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_progress_setting.*
import java.text.SimpleDateFormat
import java.util.*


class ProgressSettingActivity : AppCompatActivity() {

    private var date_listener : DatePickerDialog.OnDateSetListener? = null
    private var date_listener2 : DatePickerDialog.OnDateSetListener? = null

    private var start_day : Date ?= null
    private var end_day : Date ?= null

    private var PID : String? = null
    var myProjectDTO : ProjectDTO ?= null

    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var databaseReference: DatabaseReference = firebaseDatabase.getReference("ProjectList")

    val dateFormat = SimpleDateFormat("yyyyMMddHHmmss")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress_setting)

        val getintent = intent /*데이터 수신*/
        if (getintent != null) {
            PID = getintent.extras!!.getString("PID")
        }
        getProjectInfo()

        calendarInit()
        progress_button.setOnClickListener {
            setProgressDateInfoToDB()
        }
    }

    private fun getProjectInfo(){

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    if(snapshot.key == PID){
                        myProjectDTO  = snapshot.getValue(ProjectDTO::class.java)!!
                        progress_project_name.text = myProjectDTO!!.projectName
                        if(myProjectDTO!!.progressData!=null){
                            val start = myProjectDTO!!.progressData!!.startDate
                            val end = myProjectDTO!!.progressData!!.endDate
                            progress_startday.text = "${start.substring(0,4)} / ${start.substring(4,6)} / ${start.substring(6,8)}"
                            progress_endday.text = "${end.substring(0,4)} / ${end.substring(4,6)} / ${end.substring(6,8)}"
                        }
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("ExtraUserInfoActivity", "loadPost:onCancelled",
                    databaseError.toException()
                )
            }
        })
    }

    private fun setProgressDateInfoToDB(){

        when {
            start_day == null-> {Toast.makeText(this, "시작일을 설정해주세요", Toast.LENGTH_SHORT).show()}
            end_day == null -> {Toast.makeText(this, "종료일을 설정해주세요", Toast.LENGTH_SHORT).show()}
            start_day!!.time.toLong() > end_day!!.time.toLong() -> {Toast.makeText(this, "시작일보다 종료일이 빠릅니다", Toast.LENGTH_SHORT).show()}
            else -> {
                var progressDTO : ProgressDTO = ProgressDTO()
                progressDTO.startDate = dateFormat.format(start_day)
                progressDTO.endDate = dateFormat.format(end_day)
                databaseReference.child(PID!!).child("progress").setValue(progressDTO)
                finish()
            }
        }

    }


    @SuppressLint("SetTextI18n")
    private fun calendarInit(){
        val calendar : Calendar = Calendar.getInstance()

        // default는 오늘날짜
        progress_startday.text = ""+calendar.get(Calendar.YEAR)+" / "+(calendar.get((Calendar.MONTH))+1)+" / "+calendar.get(Calendar.DAY_OF_MONTH)
        progress_endday.text = ""+calendar.get(Calendar.YEAR)+" / "+(calendar.get((Calendar.MONTH))+1)+" / "+calendar.get(Calendar.DAY_OF_MONTH)

        date_listener = DatePickerDialog.OnDateSetListener{ datePicker: DatePicker, year: Int, month: Int, day: Int ->

            val tmpCal : Calendar = Calendar.getInstance()
            tmpCal.timeInMillis=0
            tmpCal.set(year, month, day)
            start_day = Date()
            start_day = tmpCal.time
            progress_startday.text = "$year / ${month+1} / $day"
            Log.d("Start Date --->", start_day.toString())

        }

        date_listener2 = DatePickerDialog.OnDateSetListener{ datePicker: DatePicker, year: Int, month: Int, day: Int ->

                    val tmpCal : Calendar = Calendar.getInstance()
                    tmpCal.timeInMillis=0
                    tmpCal.set(year, month, day)
                    end_day = Date()
                    end_day = tmpCal.time
                    progress_endday.text = "$year / ${month+1} / $day"
                    Log.d("End Date --->", end_day.toString())

        }

        progress_startday.setOnClickListener {
            val dateDialog : DatePickerDialog
                    = DatePickerDialog(this, date_listener, calendar.get(Calendar.YEAR), calendar.get(
                Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            dateDialog.show()
        }

        progress_endday.setOnClickListener {
            val dateDialog : DatePickerDialog
                    = DatePickerDialog(this, date_listener2, calendar.get(Calendar.YEAR), calendar.get(
                Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            dateDialog.show()
        }

    }

}
