package com.example.teamtotest.activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.teamtotest.R
import com.example.teamtotest.dto.TodoDTO
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_add_todo.*
import java.text.SimpleDateFormat
import java.util.*

class AddTodoActivity : AppCompatActivity() {
    private var deadline = Calendar.getInstance().apply {
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
        setContentView(R.layout.activity_add_todo)

        firebaseDatabase = FirebaseDatabase.getInstance()

        deadline_date.text = format1.format(deadline.time)
        deadline_time.text = format2.format(deadline.time)

        //상단바
        setSupportActionBar(add_todo_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //마감기한 날짜 눌렀을 때
        deadline_date.setOnClickListener {
            val picker = DatePickerDialog(
                this,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                datelistener,
                deadline.get(Calendar.YEAR),
                deadline.get(Calendar.MONTH),
                deadline.get(Calendar.DAY_OF_MONTH)
            )
            picker.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            picker.show()
        }
        //마감기한 시간 눌렀을 때
        deadline_time.setOnClickListener {
            val picker = TimePickerDialog(
                this,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                timelistener,
                deadline.get(Calendar.HOUR_OF_DAY),
                deadline.get(Calendar.MINUTE),
                false
            )
            picker.window!!.setBackgroundDrawableResource(android.R.color.transparent)
            picker.show()
        }

        //과제 수행자 지정
//        todo_btn_members.setOnClickListener {
//
//        }

        //알림 설정
        todo_spinner.adapter = spinnerAdapter
        todo_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                todo_spinner.prompt = "없음"
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //아이템이 클릭 되면 맨 위부터 position 0번부터 순서대로 동작하게 됩니다.
                alarmPosition = position
            }

        }

        //등록하기 눌렀을 때
        todo_btn_create.setOnClickListener {
            if (todo_et_name.text.toString() == "") {
                Toast.makeText(this.applicationContext, "할일명을 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                val todoDTO = TodoDTO(
                    todo_et_name.text.toString(),
                    todo_et_note.text.toString(),
                    deadline.time.time,
                    alarmPosition
                )
                //DB에 업로드
                PID = intent.getStringExtra("PID")
                databaseReference = firebaseDatabase.getReference("ProjectList").child(PID.toString()).child("todoList")
                databaseReference.push().setValue(todoDTO)

                finish()
            }
        }
    }

    //상단바 눌렸을 때
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    //마감기한 날짜 설정 눌렀을 때
    private val datelistener: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            deadline.set(Calendar.YEAR, year)
            deadline.set(Calendar.MONTH, month)
            deadline.set(Calendar.DATE, dayOfMonth)
            setText()

            return@OnDateSetListener
        }

    //마감기한 시간 설정 눌렀을 때
    private val timelistener: TimePickerDialog.OnTimeSetListener =
        TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            deadline.set(Calendar.HOUR_OF_DAY, hourOfDay)
            deadline.set(Calendar.MINUTE, minute)
            setText()

            return@OnTimeSetListener
        }

    //마감기한 텍스트뷰 바꿔주기
    private fun setText() {
        deadline_date.text = format1.format(deadline.time)
        deadline_time.text = format2.format(deadline.time)
    }
}