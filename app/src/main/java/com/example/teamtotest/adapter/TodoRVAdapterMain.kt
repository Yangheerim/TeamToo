package com.example.teamtotest.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
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
            when {
                diff == 0 -> {
                    holder.itemView.d_todo_tv_d_day.text = "D - day"
                }
                diff < 0 -> {
                    holder.itemView.d_todo_tv_d_day.text = "D + ${diff.absoluteValue}"
//                    holder.itemView.d_todo_tv_d_day.visibility = View.INVISIBLE
                }
                else -> {
                    holder.itemView.d_todo_tv_d_day.text = "D - $diff"
                }
            }
        }

        holder.itemView.d_todo_tv_name.text = todoDTO[position].name

    }
}