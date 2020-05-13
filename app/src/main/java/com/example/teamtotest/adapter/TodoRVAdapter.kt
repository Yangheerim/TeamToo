package com.example.teamtotest.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.print.PrinterId
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.teamtotest.R
import com.example.teamtotest.activity.ModifyTodoActivity
import com.example.teamtotest.activity.ScheduleListActivity
import com.example.teamtotest.dto.TodoDTO
import com.example.teamtotest.dto.UserDTO
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.item_todo.view.*
import java.util.*
import kotlin.math.absoluteValue

class TodoRVAdapter(private val context: Context, private var todoDTO: ArrayList<TodoDTO>, private var PID: String?) :
    RecyclerView.Adapter<ViewHolderHelper>() {
    private val today = Calendar.getInstance()
    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var databaseReference = firebaseDatabase.getReference("UserList")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHelper {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_todo, parent, false)
        return ViewHolderHelper(view)
    }

    override fun getItemCount(): Int {
        return todoDTO.size
    }

    override fun onBindViewHolder(holder: ViewHolderHelper, position: Int) {
        val deadCal = Calendar.getInstance()
        deadCal.time = Date(todoDTO[position].deadLine)

        if (today.get(Calendar.YEAR) == deadCal.get(Calendar.YEAR)) { //같은 연도인 경우
            val diff = deadCal.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR)
            if (diff == 0) {
                holder.itemView.todo_tv_d_day.text = "D - day"
            } else if (diff < 0) {
                holder.itemView.todo_tv_d_day.text = "D + ${diff.absoluteValue}"
            } else {
                holder.itemView.todo_tv_d_day.text = "D - $diff"
            }
        }
        holder.itemView.todo_tv_name.text = todoDTO[position].name

        // 할일 지정자 나타내주기
        if (todoDTO[position].performers.size > 1) {
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        if (snapshot.key == todoDTO[position].performers[0]) {
                            Log.d("Key --->", snapshot.key)
                            // member로 등록되어있는 user의 UID를 가진 정보를 찾으면 다른 info를 DTO로 가져와서 일단 이름만 저장! -> 이름 동그라미로 리스트 보여줘야하니깐!
                            val userDTO: UserDTO = snapshot.getValue(UserDTO::class.java)!!

                            holder.itemView.todo_performers.text = userDTO.name
                            holder.itemView.todo_performer_extra.text =
                                "+ " + (todoDTO[position].performers.size - 1).toString()
                        }
                    }
                }

                override fun onCancelled(dataSnapshot: DatabaseError) {
                    Log.w("ExtraUserInfoActivity", "loadPost:onCancelled")
                }
            })
        } else {
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        if (snapshot.key == todoDTO[position].performers[0]) {
                            Log.d("Key --->", snapshot.key)
                            // member로 등록되어있는 user의 UID를 가진 정보를 찾으면 다른 info를 DTO로 가져와서 일단 이름만 저장! -> 이름 동그라미로 리스트 보여줘야하니깐!
                            val userDTO: UserDTO = snapshot.getValue(UserDTO::class.java)!!

                            holder.itemView.todo_performers.text = userDTO.name
                            holder.itemView.todo_performer_extra.visibility = View.GONE
                        }
                    }
                }

                override fun onCancelled(dataSnapshot: DatabaseError) {
                    Log.w("ExtraUserInfoActivity", "loadPost:onCancelled")
                }
            })
        }

        // 할일 수정화면으로 전환
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ModifyTodoActivity::class.java)
            intent.putExtra("PID", PID)

            databaseReference = firebaseDatabase.getReference("ProjectList").child(PID.toString()).child("todoList")
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        if (snapshot.child("name").value == todoDTO[position].name) {
                            intent.putExtra("todoID", snapshot.key.toString())
                            context.startActivity(intent)
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.w("ExtraUserInfoActivity", "loadPost:onCancelled")
                }
            })
        }

        // 할일 삭제하기
        holder.itemView.setOnLongClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("삭제하시겠습니까?")
            builder.setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, which ->  })
            builder.setPositiveButton("예",
                DialogInterface.OnClickListener { dialog, which ->
                    databaseReference = firebaseDatabase.getReference("ProjectList").child(PID.toString()).child("todoList")
                    databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (snapshot in dataSnapshot.children) {
                                if (snapshot.child("name").value == todoDTO[position].name) {
                                    snapshot.ref.removeValue()
                                }
                            }
                        }
                        override fun onCancelled(p0: DatabaseError) {
                            Log.w("ExtraUserInfoActivity", "loadPost:onCancelled")
                        }
                    })

                })
            builder.show()
            return@setOnLongClickListener true
        }
    }
}