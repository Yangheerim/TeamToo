package com.example.teamtotest.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.teamtotest.R
import com.example.teamtotest.dto.TodoDTO
import com.example.teamtotest.dto.UserDTO
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.item_todo.view.todo_tv_d_day
import kotlinx.android.synthetic.main.item_todo.view.todo_tv_name
import kotlinx.android.synthetic.main.item_todo_dashboard.view.*
import java.util.*
import kotlin.math.absoluteValue

class TodoRVAdapterMain(private val context: Context, private var todoDTO: ArrayList<TodoDTO>) :
    RecyclerView.Adapter<ViewHolderHelper>() {
    private val today = Calendar.getInstance()
    var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    var databaseReference = firebaseDatabase.getReference("UserList")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHelper {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_todo_dashboard, parent, false)
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
            } else if (diff < 0 ){
                holder.itemView.todo_tv_d_day.text = "D + ${diff.absoluteValue}"
            }
            else {
                holder.itemView.todo_tv_d_day.text = "D - $diff"
            }
        }
        holder.itemView.todo_tv_name.text = todoDTO[position].name
        Log.d("TodoRVAdapter", todoDTO[position].performers.size.toString())
        if(todoDTO[position].performers.size>1){
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        if (snapshot.key == todoDTO[position].performers[0]) {
                            Log.d("Key --->", snapshot.key)
                            // member로 등록되어있는 user의 UID를 가진 정보를 찾으면 다른 info를 DTO로 가져와서 일단 이름만 저장! -> 이름 동그라미로 리스트 보여줘야하니깐!
                            val userDTO : UserDTO = snapshot.getValue(UserDTO::class.java)!!

                            holder.itemView.todo_performers.text = userDTO.name
//                            holder.itemView.todo_performer_extra.text = "+ "+ (todoDTO[position].performers.size-1).toString()
                        }
                    }
                }
                override fun onCancelled(dataSnapshot: DatabaseError) {
                    Log.w("ExtraUserInfoActivity", "loadPost:onCancelled")
                }
            })
        }else{
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        if (snapshot.key == todoDTO[position].performers[0]) {
                            Log.d("Key --->", snapshot.key)
                            // member로 등록되어있는 user의 UID를 가진 정보를 찾으면 다른 info를 DTO로 가져와서 일단 이름만 저장! -> 이름 동그라미로 리스트 보여줘야하니깐!
                            val userDTO : UserDTO = snapshot.getValue(UserDTO::class.java)!!

                            holder.itemView.todo_performers.text = userDTO.name
                        }
                    }
                }
                override fun onCancelled(dataSnapshot: DatabaseError) {
                    Log.w("ExtraUserInfoActivity", "loadPost:onCancelled")
                }
            })
        }
    }
}