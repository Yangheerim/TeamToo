package com.example.teamtotest.activity

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.teamtotest.AlarmService
import com.example.teamtotest.Push
import com.example.teamtotest.R
import com.example.teamtotest.dto.MessageDTO
import com.example.teamtotest.dto.ScheduleDTO
import com.example.teamtotest.dto.TodoDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_add_schedule.*
import kotlinx.android.synthetic.main.activity_add_todo.*
import java.text.SimpleDateFormat
import java.util.*

class AddScheduleActivity : AppCompatActivity() {
    private var start = Calendar.getInstance().apply { set(Calendar.MINUTE, 0) }
    private var end = Calendar.getInstance().apply {
        add(Calendar.HOUR, 1)
        set(Calendar.MINUTE, 0)
    }
    private val format1 = SimpleDateFormat("yyyy / MM / dd", Locale.KOREA)
    private val format2 = SimpleDateFormat("a  h : mm", Locale.KOREA)
    private val spinnerAdapter by lazy {
        ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.spinner)
        )
    }
    private var alarmPosition = 0
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private var PID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_schedule)

        //시작,종료 textview 설정
        setText(true)
        setText(false)

        //알림 설정
        schedule_spinner.adapter = spinnerAdapter
        schedule_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                schedule_spinner.prompt = "없음"
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                //아이템이 클릭 되면 맨 위부터 position 0번부터 순서대로 동작하게 됩니다.
                alarmPosition = position
            }

        }

        //상단바
        setSupportActionBar(add_schedule_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //시작 날짜 눌렀을 때
        start_date.setOnClickListener {
            val picker = DatePickerDialog(
                this,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                dateListener_start,
                start.get(Calendar.YEAR),
                start.get(Calendar.MONTH),
                start.get(Calendar.DAY_OF_MONTH)
            )
            picker.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            picker.show()
        }
        //시작 시간 눌렀을 때
        start_time.setOnClickListener {
            val picker = TimePickerDialog(
                this,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                timeListener_start,
                start.get(Calendar.HOUR_OF_DAY),
                start.get(Calendar.MINUTE),
                false
            )
            picker.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            picker.show()
        }
        //종료 날짜 눌렀을 때
        end_date.setOnClickListener {
            val picker = DatePickerDialog(
                this,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                dateListener_end,
                end.get(Calendar.YEAR),
                end.get(Calendar.MONTH),
                end.get(Calendar.DAY_OF_MONTH)
            )
            picker.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            picker.show()
        }
        //종료 시간 눌렀을 때
        end_time.setOnClickListener {
            val picker = TimePickerDialog(
                this,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                timeListener_end,
                end.get(Calendar.HOUR_OF_DAY),
                end.get(Calendar.MINUTE),
                false
            )
            picker.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            picker.show()
        }

        //등록하기 눌렀을 때
        schedule_btn_create.setOnClickListener {
            if (schedule_et_name.text.toString() == "") {
                Toast.makeText(this.applicationContext, "스케줄 제목을 입력해주세요.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val scheduleDTO = ScheduleDTO(
                    schedule_et_name.text.toString(),
                    start.timeInMillis,
                    end.timeInMillis,
                    schedule_et_place.text.toString(),
                    alarmPosition,
                    schedule_et_note.text.toString()
                )
                //DB에 업로드
                firebaseDatabase = FirebaseDatabase.getInstance()
                PID = intent.getStringExtra("PID")
                databaseReference =
                    firebaseDatabase.getReference("ProjectList").child(PID.toString())
                        .child("scheduleList")
                databaseReference.push().setValue(scheduleDTO)


                if (alarmPosition != 0) {
                    // 알림 매니저에 넘겨줄 intent
                    val mAlarmIntent = Intent(this, AlarmService::class.java)
                    mAlarmIntent.putExtra("PID", PID)
                    mAlarmIntent.putExtra("schedule_name", schedule_et_name.text.toString())
                    mAlarmIntent.putExtra("type", "Alarm_schedule")

                    // 알림 매니저 설정
                    val mTime = getTime(start, alarmPosition)
                    val mPendingIntent = PendingIntent.getService(this, 222, mAlarmIntent, 0)
                    val mAlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    mAlarmManager.set(AlarmManager.RTC_WAKEUP, mTime, mPendingIntent)
                }

                // 스케줄 등록 푸시 알림
                Push(PID.toString(), schedule_et_name.text.toString(), "Schedule")

                addMessageNotificationToDB(scheduleDTO)



                finish()
            }
        }
    }

    // 스케줄 추가 시 채팅창에 알림 메세지 저장
    private fun addMessageNotificationToDB(scheduleDTO: ScheduleDTO) {
        val firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()
        val messageDTO =
            MessageDTO(
                "새로운 스케줄이 추가되었습니다.",
                firebaseAuth.currentUser!!.displayName.toString(),
                firebaseAuth.currentUser!!.uid.toString(),
                scheduleData = scheduleDTO
            )
        val current = Date()
        val utc = Date(current.time - Calendar.getInstance().timeZone.getOffset(current.time))
        val dateFormat = SimpleDateFormat("yyyyMMddHHmmss")
        val date_formatted = dateFormat.format(utc)


        databaseReference = firebaseDatabase!!.getReference()
        databaseReference = databaseReference!!.child("ProjectList").child(PID.toString()).child("messageList")
            .child(date_formatted)
        databaseReference!!.setValue(messageDTO)

    }


    //상단바 눌렸을 때
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    //시작날짜 설정 눌렀을 때
    private val dateListener_start: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            start.set(Calendar.YEAR, year)
            start.set(Calendar.MONTH, month)
            start.set(Calendar.DATE, dayOfMonth)
            setText(true)

            //시작이 종료보다 늦을 때
            if (start.time.after(end.time)) {
                end = start.clone() as Calendar
                end.add(Calendar.HOUR_OF_DAY, 1)
                setText(false)
                return@OnDateSetListener
            }
        }

    //시작시간 설정 눌렀을 때
    private val timeListener_start: TimePickerDialog.OnTimeSetListener =
        TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            start.set(Calendar.HOUR_OF_DAY, hourOfDay)
            start.set(Calendar.MINUTE, minute)
            setText(true)

            //시작이 종료보다 늦을 때
            if (start.time.after(end.time)) {
                end = start.clone() as Calendar
                end.add(Calendar.HOUR_OF_DAY, 1)
                setText(false)
                return@OnTimeSetListener
            }
        }

    //종료날짜 설정 눌렀을 때
    private val dateListener_end: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            val cal = end.clone() as Calendar
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DATE, dayOfMonth)
            if (start.time.after(cal.time)) {
                Toast.makeText(this, "시작 일시 보다 이전일 수 없습니다", Toast.LENGTH_SHORT).show()
                return@OnDateSetListener
            }
            end.set(Calendar.YEAR, year)
            end.set(Calendar.MONTH, month)
            end.set(Calendar.DATE, dayOfMonth)

            setText(false)
        }

    //종료시간 설정 눌렀을 때
    private val timeListener_end: TimePickerDialog.OnTimeSetListener =
        TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            val cal = end.clone() as Calendar
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
            cal.set(Calendar.MINUTE, minute)
            if (start.time.after(cal.time)) {
                Toast.makeText(this, "시작 일시 보다 이전일 수 없습니다", Toast.LENGTH_SHORT).show()
                return@OnTimeSetListener
            }

            end.set(Calendar.HOUR_OF_DAY, hourOfDay)
            end.set(Calendar.MINUTE, minute)

            setText(false)
        }

    private fun setText(isStart: Boolean) {
        if (isStart) {
            start_date.text = format1.format(start.time)
            start_time.text = format2.format(start.time)
        } else {
            end_date.text = format1.format(end.time)
            end_time.text = format2.format(end.time)
        }
    }

    private fun getTime(date: Calendar, position: Int): Long {
        //1: 5분전, 2: 15분전, 3: 30분전, 4: 1시간전, 5: 1일전, 6: 2일전 7: 1주전
        val setTime = Calendar.getInstance().apply { set(Calendar.SECOND, 0) }
        when (position) {
            1 -> setTime.set(Calendar.MINUTE, date.get(Calendar.MINUTE) - 5)
            2 -> setTime.set(Calendar.MINUTE, date.get(Calendar.MINUTE) - 15)
            3 -> setTime.set(Calendar.MINUTE, date.get(Calendar.MINUTE) - 30)
            4 -> setTime.set(Calendar.HOUR_OF_DAY, date.get(Calendar.HOUR) - 1)
            5 -> setTime.set(Calendar.DATE, date.get(Calendar.DATE) - 1)
            6 -> setTime.set(Calendar.DATE, date.get(Calendar.DATE) - 2)
            7 -> setTime.set(Calendar.DATE, date.get(Calendar.DATE) - 7)
        }

        return setTime.time.time
    }
}
