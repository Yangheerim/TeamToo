package com.example.teamtotest.activity

import android.R
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.teamtotest.PerformerDialog
import com.example.teamtotest.Push
import com.example.teamtotest.dto.TodoDTO
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_add_todo.*
import java.text.SimpleDateFormat
import java.util.*

class ModifyTodoActivity : AppCompatActivity() {
    private var deadline = Calendar.getInstance()
    private val format1 = SimpleDateFormat("yyyy / MM / dd", Locale.KOREA)
    private val format2 = SimpleDateFormat("a  h : mm", Locale.KOREA)
    private val spinnerAdapter by lazy {
        ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(com.example.teamtotest.R.array.spinner)
        )
    }
    private var alarmPosition = 0
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private var PID: String? = null
    private var todoID: String? = null
    private var todoDTO:TodoDTO? = null

    private var performerUIDList : ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.teamtotest.R.layout.activity_add_todo)

        firebaseDatabase = FirebaseDatabase.getInstance()
        PID = intent.getStringExtra("PID")
        todoID = intent.getStringExtra("todoID")

        //상단바
        setSupportActionBar(add_todo_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // DB에서 todoDTO 가져오기
        databaseReference = firebaseDatabase.getReference("ProjectList").child(PID.toString()).child("todoList").child(todoID.toString())
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                todoDTO = dataSnapshot.getValue(TodoDTO::class.java)
                todoDTO?.let { setOrigin(it) }
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })

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
        todo_btn_select_performer.setOnClickListener {
            var performerDialog : PerformerDialog = PerformerDialog(this)
            performerDialog.PID = PID
            performerDialog.prePerformerUIDList = performerUIDList
            performerDialog.callDialog()
        }

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
            }
            if (performerUIDList.isEmpty()){
                Toast.makeText(this.applicationContext, "과제수행자를 지정해주세요.", Toast.LENGTH_SHORT).show()
            }
            else {
                val todoDTO = TodoDTO(
                    todo_et_name.text.toString(),
                    todo_et_note.text.toString(),
                    deadline.time.time,
                    performerUIDList,
                    alarmPosition
                )
                //DB에 업로드
                databaseReference = firebaseDatabase.getReference("ProjectList").child(PID.toString()).child("todoList").child(todoID.toString())
                databaseReference.setValue(todoDTO)

                // 할일 수정 푸시 알림
                Push(PID.toString(), todo_et_name.text.toString(),"Todo2")

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

    private fun setOrigin(dto: TodoDTO){
        todoDTO = dto
        todo_et_name.setText(todoDTO!!.name)
        todo_et_note.setText(todoDTO!!.note)

        deadline.time = Date(todoDTO!!.deadLine)
        deadline_date.text = format1.format(Date(todoDTO!!.deadLine))
        deadline_time.text = format2.format(Date(todoDTO!!.deadLine))

        setPerformer(todoDTO!!.performers)
        todo_spinner.setSelection(todoDTO!!.alarm)

        todo_btn_create.text = "수정하기"
    }

    fun setPerformer(performerUIDList_ : ArrayList<String>){
        performerUIDList = performerUIDList_
        add_todo_performer_num.text = performerUIDList.size.toString()
    }
}