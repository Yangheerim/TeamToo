package com.example.teamtotest.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.teamtotest.R
import com.example.teamtotest.dto.TodoDTO
import kotlinx.android.synthetic.main.item_todo.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue

class TodoRVAdapter(private val context: Context, private var todoDTO: ArrayList<TodoDTO>) :
    RecyclerView.Adapter<ViewHolderHelper>() {
    private val today = Calendar.getInstance()

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
            } else if (diff < 0 ){
                holder.itemView.todo_tv_d_day.text = "D + ${diff.absoluteValue}"
            }
            else {
                holder.itemView.todo_tv_d_day.text = "D - $diff"
            }
        }
        holder.itemView.todo_tv_name.text = todoDTO[position].name

    }

}