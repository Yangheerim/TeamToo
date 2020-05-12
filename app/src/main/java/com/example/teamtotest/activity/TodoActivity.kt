package com.example.teamtotest.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.teamtotest.R
import com.example.teamtotest.adapter.TodoRVAdapter
import com.example.teamtotest.dto.TodoDTO
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_todo.*

class TodoActivity : AppCompatActivity() {
    private lateinit var todoRVAdapter: TodoRVAdapter
    private var todoList: ArrayList<TodoDTO> = ArrayList()
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private var PID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todo)

        //상단바
        setSupportActionBar(todo_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //PID에 해당하는 프로젝트 할일 가져오기
        PID = intent.getStringExtra("PID")
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("ProjectList").child(PID.toString()).child("todoList")

        val dbTodoEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                todoList.clear()
                for (data in dataSnapshot.children){
                    val todoDTO = data.getValue(TodoDTO::class.java)
                    todoDTO?.let { todoList.add(it) }
                    todoRVAdapter.notifyDataSetChanged()
                }
                changeView(todoList)    //투두리스트 개수에 따라 뷰 바꿔주기
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("TAG", databaseError.toString())
            }
        }
        databaseReference.addValueEventListener(dbTodoEventListener)

        // + 버튼 눌렀을 때
        todo_btn_add.setOnClickListener {
            startActivity(Intent(this, AddTodoActivity::class.java).putExtra("PID",PID))
        }

        //어댑터 설정
        todoRVAdapter = TodoRVAdapter(this, todoList, PID)
        rv_todo.adapter = todoRVAdapter
        rv_todo.layoutManager = LinearLayoutManager(this)
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

    fun changeView(todoList: ArrayList<TodoDTO>){
        if(todoList.isEmpty()){
            tv_todo.visibility = View.VISIBLE
            rv_todo.visibility = View.INVISIBLE
        }
        else{
            tv_todo.visibility = View.INVISIBLE
            rv_todo.visibility = View.VISIBLE
        }
    }
}